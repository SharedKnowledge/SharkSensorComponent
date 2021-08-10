package net.sharksystem.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sharksystem.SharkException;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SharkSensorComponentImpl implements SharkSensorComponent, ASAPMessageReceivedListener {

    private SensorRepository repo;
    private ASAPPeer peer;
    private SharkSensorSerializer serializer;


    @Override
    public void onStart(ASAPPeer asapPeer) throws SharkException {
        this.peer = asapPeer;

        this.peer.addASAPMessageReceivedListener(
                SharkSensorComponent.APP_FORMAT, this);
    }

    public SharkSensorComponentImpl(SensorRepository repo, SharkSensorSerializer serializer){
        this.repo = repo;
        this.serializer = serializer;

    }

    @Override
    public void setBehaviour(String s, boolean b) throws SharkUnknownBehaviourException, ASAPException, IOException {
        // IGNORE
    }


    @Override
    public void checkForNewDataInDB(){
        //TODO
    }

    @Override
    public void sendNewSensorDataToASAPPeer(SensorData sensorData) {
        try {
            this.peer.sendASAPMessage(APP_FORMAT, URI, serializer.serializeSensorData(sensorData).getBytes());
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (ASAPException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SensorData> getSensorDataForId(String id){
        return repo.selectAllForId(id);
    }

    @Override
    public SensorData getSensorDataForIdAndTime(String id, Date dt) {
        return repo.selectSpecificEntry(id, dt);
    }

    @Override
    public void asapMessagesReceived(ASAPMessages asapMessages, String s, List<ASAPHop> list) throws IOException {
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
}
