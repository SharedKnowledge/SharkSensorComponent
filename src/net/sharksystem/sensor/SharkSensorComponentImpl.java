package net.sharksystem.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sharksystem.SharkException;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SharkSensorComponentImpl implements SharkSensorComponent, ASAPMessageReceivedListener {

    private SensorRepository repo;
    private ASAPPeer peer;
    private SharkSensorSerializer serializer;
    private String sensorId;
    private List<NewSensorDataReceivedListener> newSensorDataReceivedListeners = new ArrayList<>();

    @Override
    public void onStart(ASAPPeer asapPeer) throws SharkException {
        this.peer = asapPeer;
        this.peer.addASAPMessageReceivedListener(
                SharkSensorComponent.APP_FORMAT, this);
    }

    public SharkSensorComponentImpl(
            SensorRepository repo, SharkSensorSerializer serializer, String sensorId){
        this.repo = repo;
        this.serializer = serializer;
        this.sensorId = sensorId;
    }

    @Override
    public void setBehaviour(String s, boolean b) throws SharkUnknownBehaviourException, ASAPException, IOException {
        // IGNORE
    }

    @Override
    public void checkForNewDataInDB(){
        List<SensorData> newEntries = repo.selectEntriesWhereSentIsFalse(this.sensorId);
        if(newEntries!=null && !newEntries.isEmpty()){
            this.sendSensorData(newEntries);
        }
    }

    @Override
    public void sendSensorData(List<SensorData> list) {
        if(list!=null&& !list.isEmpty()) {
            try {
                for (SensorData data : list) {
                    String msg = serializer.serializeSensorData(data);
                    System.out.println("Message is: " + msg);
                    this.peer.sendASAPMessage(APP_FORMAT, URI, msg.getBytes());
                }
                //set flag to true to indicate that the entries have been sent to asap peer
                repo.updateEntries(list);
            } catch (JsonProcessingException | ASAPException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receiveSensorData(List<SensorData> sensorDataList) throws IOException {
        if(sensorDataList != null && !sensorDataList.isEmpty()) {
            this.repo.insertNewEntries(sensorDataList);
            this.notifyReceived();
        }
    }

    @Override
    public List<String> getAllBaseNames() {
        return this.repo.selectAllBNs();
    }

    @Override
    public List<SensorData> getSensorDataForId(String id){
        return repo.selectAllForId(id);
    }

    @Override
    public SensorData getSensorDataForIdAndTime(String id, double dt) {
        return repo.selectSpecificEntry(id, dt);
    }


    @Override
    public void asapMessagesReceived(ASAPMessages asapMessages, String s, List<ASAPHop> list) throws IOException {
        CharSequence uri = asapMessages.getURI();
        Iterator<byte[]> msgIter = asapMessages.getMessages();

        if (URI.equals(uri.toString())) {
            while (msgIter.hasNext()) {
                byte[] msgContent = msgIter.next();
                String msgString = new String(msgContent, StandardCharsets.UTF_8);
                List<SensorData> sensorDataList = serializer.deserializeSensorData(msgString);
                    this.receiveSensorData(sensorDataList);
            }
        }
    }

    @Override
    public void addNewSensorDataReceivedListener(NewSensorDataReceivedListener listener){
        if (listener != null) {
            this.newSensorDataReceivedListeners.add(listener);
        }
    }
    @Override
    public void removeSensorDataReceivedListener(NewSensorDataReceivedListener listener){
        if (listener != null) {
            this.newSensorDataReceivedListeners.remove(listener);
        }
    }
    @Override
    public void notifyReceived(){
        for(NewSensorDataReceivedListener listener: this.newSensorDataReceivedListeners){
            listener.dataReceived();
        }
    }
}
