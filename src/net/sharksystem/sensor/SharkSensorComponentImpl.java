package net.sharksystem.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sharksystem.SharkException;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SharkSensorComponentImpl implements SharkSensorComponent, ASAPMessageReceivedListener {

    private SensorRepository repo;
    private ASAPPeer peer;
    private SharkSensorSerializer serializer;
    private String sensorId;
    private SensorData newestEntry;



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
        List<SensorData> entries = new ArrayList<>();
        if(this.newestEntry==null){
            entries.addAll(repo.selectAllForId(this.sensorId));
            this.newestEntry = getNewestFromList(entries);
        }
        else{
            entries.addAll(repo.selectForIdNewerThan(this.newestEntry.getDt(), this.sensorId));
        }
        if(!entries.isEmpty()) {
            this.sendNewSensorDataToASAPPeer(entries);
        }
    }

    /*don't serialize the whole list because if there are some duplicates
     in two different lists the ASAP-Peer does not notice. If the sensorComponent gets re-started after being down,
     it re-sends all Entries concerning its own sensor module. It#s important that the ASAp-Peers only saves
     and distributes new records*/
    @Override
    public void sendNewSensorDataToASAPPeer(List<SensorData> list) {
        for(SensorData data: list) {
            try {
                this.peer.sendASAPMessage(APP_FORMAT, URI, serializer.serializeSensorData(data).getBytes());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ASAPException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receiveASAPMessage(ASAPMessages asapMessages) throws IOException {
        CharSequence uri = asapMessages.getURI();

        Iterator<byte[]> msgIter = asapMessages.getMessages();

        if (URI.equals(uri.toString())) {
            while (msgIter.hasNext()) {
                byte[] msgContent = msgIter.next();
                ByteArrayInputStream bais = new ByteArrayInputStream(msgContent);
                DataInputStream dais = new DataInputStream(bais);

                this.repo.insertNewEntries(serializer.deserializeSensorData(dais.readUTF()));

            }
        }
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
        this.receiveASAPMessage(asapMessages);
    }

    SensorData getNewestFromList(List<SensorData> list){
        SensorData newestEntry = list.get(0);

        for(SensorData data: list){
            if(data.getDt() > (newestEntry.getDt())){
                newestEntry = data;
            }
        }
        return newestEntry;
    }
}
