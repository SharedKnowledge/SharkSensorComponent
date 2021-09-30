package net.sharksystem.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.asap.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**The Code is based on:
 * https://github.com/SharedKnowledge/SharkCreditMoney/blob/master/tests/net/sharksystem/creditmoney/SharkCreditMoneyComponentTests.java
 * **/

public class SharkSensorComponentTests {
    static final CharSequence ROOTFOLDER = "sharkComponent";

    static final CharSequence ALICE = "Alice";
    static final String ALICE_ID = "Alice"+"ID";
    static final CharSequence ALICE_ROOTFOLDER = ROOTFOLDER + "/" + ALICE;
    private SharkTestPeerFS alicePeer;
    private SharkSensorComponent aliceComponent;
    private SensorRepository aliceRepo;
    private DBUpdateSocket aliceSocketThread;

    static final CharSequence BOB = "Bob";
    static final String BOB_ID = "Bob"+"ID";
    static final CharSequence BOB_ROOTFOLDER = ROOTFOLDER + "/" + BOB;
    private SharkTestPeerFS bobPeer;
    private SharkSensorComponent bobComponent;
    private SensorRepository bobRepo;
    private DBUpdateSocket bobSocketThread;
    private static int portNumber = 5000;

    private int getPortNumber() {
        return SharkSensorComponentTests.portNumber++;
    }


    private void setUpAliceBobExchangeScenario() throws SharkException {
        SharkTestPeerFS.removeFolder(ALICE_ROOTFOLDER);
        this.alicePeer = new SharkTestPeerFS(ALICE,ALICE_ROOTFOLDER);
        this.aliceRepo = mock(SensorRepository.class);
        this.aliceSocketThread = null;
        this.aliceComponent = TestHelper.setupComponent(
                this.alicePeer,aliceRepo,new SharkSensorSerializerImpl(new ObjectMapper()), aliceSocketThread,ALICE_ID);
        this.alicePeer.start();

        SharkTestPeerFS.removeFolder(BOB_ROOTFOLDER);
        this.bobPeer = new SharkTestPeerFS(BOB,BOB_ROOTFOLDER);
        this.bobRepo = mock(SensorRepository.class);
        this.bobSocketThread = null;
        this.bobComponent = TestHelper.setupComponent(
                this.bobPeer,bobRepo,new SharkSensorSerializerImpl(new ObjectMapper()), bobSocketThread,BOB_ID.toString());
        this.bobPeer.start();

    }
    public void runEncounter(SharkTestPeerFS leftPeer, SharkTestPeerFS rightPeer, boolean stop) throws SharkException, IOException, InterruptedException {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("                       start encounter: "
                + leftPeer.getASAPPeer().getPeerID() + " <--> " + rightPeer.getASAPPeer().getPeerID());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        leftPeer.getASAPTestPeerFS().startEncounter(this.getPortNumber(), rightPeer.getASAPTestPeerFS());
        Thread.sleep(1000);
        System.out.println("slept a moment");
        if(stop){
            System.out.println(">>>>>>>>>>>>>>>>>  stop encounter: "
                    +leftPeer.getASAPPeer().getPeerID()+ "<-->"+rightPeer.getASAPPeer().getPeerID());
            leftPeer.getASAPTestPeerFS().stopEncounter((rightPeer.getASAPTestPeerFS()));
        }
    }

    @Test
    public void sendSensorDataTestWithSingleEntry() throws JsonProcessingException, ASAPException, SharkException {
        ASAPPeerFS mockPeer = Mockito.mock(ASAPPeerFS.class);
        SensorRepository repo = Mockito.mock(SensorRepository.class);
        SharkSensorSerializer serializer = new SharkSensorSerializerImpl(new ObjectMapper());
        SharkSensorComponent component = new SharkSensorComponentImpl(repo,serializer, "testId");
        component.onStart(mockPeer);
        List<SensorData> sd = new ArrayList<>();
        SensorData data1 = new SensorData("testName", 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,1355267532.0);
        String msgString = serializer.serializeSensorData(data1);
        String APP_FORMAT = "shark/sensor";
        String URI = "sharkSensor://sensorData";
        sd.add(data1);
        doNothing().when(repo).updateEntries(sd);
        doNothing().when(mockPeer).sendASAPMessage(APP_FORMAT,URI,msgString.getBytes());
        component.sendSensorData(sd);
        Mockito.verify(repo, Mockito.times(1)).updateEntries(sd);
        Mockito.verify(mockPeer, Mockito.times(1)).sendASAPMessage(APP_FORMAT,URI,msgString.getBytes());
    }
    @Test
    public void sendSensorDataTestWithMultipleEntries() throws JsonProcessingException, ASAPException, SharkException {
        ASAPPeerFS mockPeer = Mockito.mock(ASAPPeerFS.class);
        SensorRepository repo = Mockito.mock(SensorRepository.class);
        SharkSensorSerializer serializer = new SharkSensorSerializerImpl(new ObjectMapper());
        SharkSensorComponent component = new SharkSensorComponentImpl(repo,serializer, "testId");
        component.onStart(mockPeer);
        List<SensorData> sd = new ArrayList<>();
        SensorData data1 = new SensorData("testName", 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,1355267532.0);
        SensorData data2 = new SensorData(ALICE_ID, 34.6,Unit.C,34.1,Unit.P,35.7,Unit.P,1.355297659E9);
        sd.add(data1);
        sd.add(data2);
        String APP_FORMAT = "shark/sensor";
        String URI = "sharkSensor://sensorData";
        doNothing().when(repo).updateEntries(sd);
        doNothing().when(mockPeer).sendASAPMessage(eq(APP_FORMAT),eq(URI),any(byte[].class));
        component.sendSensorData(sd);
        Mockito.verify(repo, Mockito.times(1)).updateEntries(sd);
        Mockito.verify(mockPeer, Mockito.times(2)).sendASAPMessage(eq(APP_FORMAT),eq(URI),any(byte[].class));
    }

    @Test
    public void sendSensorDataTestWhenListIsNull() throws JsonProcessingException, ASAPException, SharkException {
        ASAPPeerFS mockPeer = Mockito.mock(ASAPPeerFS.class);
        SensorRepository repo = Mockito.mock(SensorRepository.class);
        SharkSensorSerializer serializer = new SharkSensorSerializerImpl(new ObjectMapper());
        SharkSensorComponent component = new SharkSensorComponentImpl(repo,serializer, "testId");
        component.onStart(mockPeer);
        String APP_FORMAT = "shark/sensor";
        String URI = "sharkSensor://sensorData";
        component.sendSensorData(null);
        Mockito.verify(repo, Mockito.times(0)).updateEntries(anyList());
        Mockito.verify(mockPeer, Mockito.times(0)).sendASAPMessage(eq(APP_FORMAT),eq(URI),any(byte[].class));
    }
    @Test
    public void sendSensorDataTestWhenListIsEmpty() throws JsonProcessingException, ASAPException, SharkException {
        ASAPPeerFS mockPeer = Mockito.mock(ASAPPeerFS.class);
        SensorRepository repo = Mockito.mock(SensorRepository.class);
        SharkSensorSerializer serializer = new SharkSensorSerializerImpl(new ObjectMapper());
        SharkSensorComponent component = new SharkSensorComponentImpl(repo,serializer, "testId");
        component.onStart(mockPeer);
        String APP_FORMAT = "shark/sensor";
        String URI = "sharkSensor://sensorData";
        component.sendSensorData(new ArrayList<>());
        Mockito.verify(repo, Mockito.times(0)).updateEntries(anyList());
        Mockito.verify(mockPeer, Mockito.times(0)).sendASAPMessage(eq(APP_FORMAT),eq(URI),any(byte[].class));
    }

    /*This test case gets a database entry from a database mock*/
    @Test
    public void testDataExchangeFromAliceToBob() throws InterruptedException, SharkException, IOException {
        this.setUpAliceBobExchangeScenario();
        List<SensorData> sd = new ArrayList<>();
        SensorData data1 = new SensorData("testName", 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,1355267532.0);
        sd.add(data1);
        when(aliceRepo.selectEntriesWhereSentIsFalse(anyString())).thenReturn(sd);
        doNothing().when(aliceRepo).updateEntries(anyList());
        doNothing().when(this.bobRepo).insertNewEntries(sd);
        this.aliceComponent.checkForNewDataInDB();
        this.runEncounter(this.alicePeer,this.bobPeer, true);
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(this.bobRepo, Mockito.times(1)).insertNewEntries(argumentCaptor.capture());
        assertEquals(data1, argumentCaptor.getValue().get(0));
    }
    @Test
    public void checkForNewDataInDBWhenSingleEntryIsInDB() throws SharkException, IOException, InterruptedException {
        this.setUpAliceBobExchangeScenario();
        List<SensorData> sd = new ArrayList<>();
        SensorData data1 = new SensorData(ALICE_ID, 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,1.355267532E9);
        sd.add(data1);
        when(aliceRepo.selectEntriesWhereSentIsFalse(ALICE_ID)).thenReturn(sd);
        doNothing().when(aliceRepo).updateEntries(anyList());
        doNothing().when(this.bobRepo).insertNewEntries(anyList());
        this.aliceComponent.checkForNewDataInDB();
        this.runEncounter(this.alicePeer,this.bobPeer, true);
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(this.bobRepo,times(1)).insertNewEntries(argumentCaptor.capture());
        List<List> capturedList = argumentCaptor.<List<List<SensorData>>> getAllValues();

        assertEquals(data1,capturedList.get(0).get(0));
    }
    @Test
    public void checkForNewDataInDBWhenMultipleEntriesIsInDB() throws SharkException, IOException, InterruptedException {
        this.setUpAliceBobExchangeScenario();
        List<SensorData> sd = new ArrayList<>();
        SensorData data1 = new SensorData(ALICE_ID, 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,1.355267532E9);
        SensorData data2 = new SensorData(ALICE_ID, 34.6,Unit.C,34.1,Unit.P,35.7,Unit.P,1.355297659E9);
        sd.add(data1);
        sd.add(data2);
        when(aliceRepo.selectEntriesWhereSentIsFalse(ALICE_ID)).thenReturn(sd);
        doNothing().when(aliceRepo).updateEntries(anyList());
        doNothing().when(this.bobRepo).insertNewEntries(anyList());
        this.aliceComponent.checkForNewDataInDB();
        this.runEncounter(this.alicePeer,this.bobPeer, true);
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(this.bobRepo,times(2)).insertNewEntries(argumentCaptor.capture());
        List<List> capturedList = argumentCaptor.<List<List<SensorData>>> getAllValues();

        assertEquals(data1,capturedList.get(0).get(0));
        assertEquals(data2,capturedList.get(1).get(0));

    }
    @Test
    public void checkForNewDataInDBWhenNoEntryIsInDB() throws SharkException, ASAPException {
        this.setUpAliceBobExchangeScenario();
        when(aliceRepo.selectEntriesWhereSentIsFalse(ALICE_ID)).thenReturn(new ArrayList<SensorData>());
        this.aliceComponent.checkForNewDataInDB();
        ASAPPeer mockPeer = mock(ASAPPeerFS.class);
        SensorRepository repo = mock(SensorRepository.class);
        SharkSensorComponent component = new SharkSensorComponentImpl(repo, new SharkSensorSerializerImpl(new ObjectMapper()),ALICE_ID);
        component.onStart(mockPeer);
        verify(mockPeer,times(0)).sendASAPMessage(anyString(),anyString(),any());
    }

    @Test
    public void checkForNewDataInDBWhenEntriesIsNull() throws SharkException, ASAPException {
        this.setUpAliceBobExchangeScenario();
        when(aliceRepo.selectEntriesWhereSentIsFalse(ALICE_ID)).thenReturn(null);
        this.aliceComponent.checkForNewDataInDB();
        ASAPPeer mockPeer = mock(ASAPPeerFS.class);
        SensorRepository repo = mock(SensorRepository.class);
        SharkSensorComponent component = new SharkSensorComponentImpl(repo, new SharkSensorSerializerImpl(new ObjectMapper()),ALICE_ID);
        component.onStart(mockPeer);
        verify(mockPeer,times(0)).sendASAPMessage(anyString(),anyString(),any());
    }
    @Test
    public void addNewSensorDataReceivedListenerTest() throws SharkException {
        this.setUpAliceBobExchangeScenario();
        final boolean[] wasNotified = {false};
        this.aliceComponent.addNewSensorDataReceivedListener(new NewSensorDataReceivedListener() {
            @Override
            public void dataReceived() {
                wasNotified[0] = true;
            }
        });
        this.aliceComponent.notifyReceived();
        assertEquals(true,wasNotified[0]);
    }
    @Test
    public void removeSensorDataReceivedListenerTest() throws SharkException {
        this.setUpAliceBobExchangeScenario();
        final boolean[] wasNotified = {false};
        NewSensorDataReceivedListener listener = new NewSensorDataReceivedListener() {
            @Override
            public void dataReceived() {
                wasNotified[0] = true;
            }
        };
        this.aliceComponent.addNewSensorDataReceivedListener(listener);
        this.aliceComponent.removeSensorDataReceivedListener(listener);
        this.aliceComponent.notifyReceived();
        assertEquals(false,wasNotified[0]);
    }
    @Test
    public void receiveSensorDataTest() throws IOException {
        final boolean[] wasNotified = {false};

        SensorRepository repo = Mockito.mock(SensorRepository.class);
        SharkSensorSerializer serializer = new SharkSensorSerializerImpl(new ObjectMapper());
        SharkSensorComponent component = new SharkSensorComponentImpl(repo,serializer, "testId");
        List<SensorData> sd = new ArrayList<>();
        SensorData data1 = new SensorData("testName", 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,1355267532.0);
        SensorData data2 = new SensorData(ALICE_ID, 34.6,Unit.C,34.1,Unit.P,35.7,Unit.P,1.355297659E9);
        sd.add(data1);
        sd.add(data2);
        doNothing().when(repo).insertNewEntries(sd);
        component.addNewSensorDataReceivedListener(new NewSensorDataReceivedListener() {
            @Override
            public void dataReceived() {
                wasNotified[0] = true;

            }
        });
        component.receiveSensorData(sd);
        Mockito.verify(repo,Mockito.times(1)).insertNewEntries(sd);
        assertTrue(wasNotified[0]);
    }
    @Test
    public void receiveSensorDataTestWhenListIsNull() throws IOException {
        final boolean[] wasNotified = {false};
        SensorRepository repo = Mockito.mock(SensorRepository.class);
        SharkSensorSerializer serializer = new SharkSensorSerializerImpl(new ObjectMapper());
        SharkSensorComponent component = new SharkSensorComponentImpl(repo,serializer, "testId");
        component.addNewSensorDataReceivedListener(new NewSensorDataReceivedListener() {
            @Override
            public void dataReceived() {
                wasNotified[0] = true;
            }
        });
        component.receiveSensorData(null);
        Mockito.verify(repo,Mockito.times(0)).insertNewEntries(anyList());
        assertFalse(wasNotified[0]);
    }
    @Test
    public void receiveSensorDataTestWhenListIsEmpty() throws IOException {
        final boolean[] wasNotified = {false};
        SensorRepository repo = Mockito.mock(SensorRepository.class);
        SharkSensorSerializer serializer = new SharkSensorSerializerImpl(new ObjectMapper());
        SharkSensorComponent component = new SharkSensorComponentImpl(repo,serializer, "testId");
        component.addNewSensorDataReceivedListener(new NewSensorDataReceivedListener() {
            @Override
            public void dataReceived() {
                wasNotified[0] = true;
            }
        });
        component.receiveSensorData(new ArrayList<>());
        Mockito.verify(repo,Mockito.times(0)).insertNewEntries(anyList());
        assertFalse(wasNotified[0]);
    }
}
