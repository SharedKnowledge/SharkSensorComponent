package net.sharksystem.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPPeerFS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class SensorDataApp {

    private static final String OWNER = "test-id-1";
    private static final String ROOT_FOLDER = "peer_storage";
    private static final String DB_URL = "jdbc:sqlite:RaspSensor/sensordb.db";

  public static void main(String[] args) {
    Collection<CharSequence> formats = new ArrayList<>();
    formats.add("shark/sensor");
    try {
        ASAPPeerFS asapPeer = new ASAPPeerFS(OWNER,ROOT_FOLDER,formats);
        SharkPeer sharkPeer = new SharkPeerFS(OWNER, ROOT_FOLDER);
        SensorRepository repo = new SensorRepositoryImpl(DB_URL);
        ObjectMapper mapper = new ObjectMapper();
        SharkSensorSerializer serializer = new SharkSensorSerializerImpl(mapper);
        DBUpdateSocket dbUpdateSocket = new DBUpdateSocket(7777);

        Thread socketShutdownHook = new Thread(() -> {
            try {
                dbUpdateSocket.stopSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Runtime.getRuntime().addShutdownHook(socketShutdownHook);
        SharkSensorComponentFactory factory = new SharkSensorComponentFactory(repo, serializer, dbUpdateSocket, OWNER);
        sharkPeer.addComponent(factory, SharkSensorComponent.class);

        sharkPeer.start(asapPeer);
        System.out.println("Peer started");
        Thread.sleep(360000);
    } catch (SharkException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ASAPException e) {
        e.printStackTrace();
    }

  }
}
