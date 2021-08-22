package net.sharksystem.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPPeer;
import net.sharksystem.asap.ASAPPeerFS;
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
    private DBUpdatePoller alicePoller;

    static final CharSequence BOB = "Bob";
    static final String BOB_ID = "Bob"+"ID";
    static final CharSequence BOB_ROOTFOLDER = ROOTFOLDER + "/" + BOB;
    private SharkTestPeerFS bobPeer;
    private SharkSensorComponent bobComponent;
    private SensorRepository bobRepo;
    private DBUpdatePoller bobPoller;
    private static int portNumber = 5000;

    private int getPortNumber() {
        return SharkSensorComponentTests.portNumber++;
    }


    private void setUpAliceBobExchangeScenario() throws SharkException {
        SharkTestPeerFS.removeFolder(ALICE_ROOTFOLDER);
        this.alicePeer = new SharkTestPeerFS(ALICE,ALICE_ROOTFOLDER);
        this.aliceRepo = mock(SensorRepository.class);
        this.alicePoller = mock(DBUpdatePoller.class);
        this.aliceComponent = TestHelper.setupComponent(
                this.alicePeer,aliceRepo,new SharkSensorSerializerImpl(new ObjectMapper()),alicePoller,ALICE_ID);
        this.alicePeer.start();

        SharkTestPeerFS.removeFolder(BOB_ROOTFOLDER);
        this.bobPeer = new SharkTestPeerFS(BOB,BOB_ROOTFOLDER);
        this.bobRepo = mock(SensorRepository.class);
        this.bobPoller = mock(DBUpdatePoller.class);
        this.bobComponent = TestHelper.setupComponent(
                this.bobPeer,bobRepo,new SharkSensorSerializerImpl(new ObjectMapper()),bobPoller,BOB_ID.toString());
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


    /*This test case gets a database entry from a database mock*/
    @Test
    public void testDataExchangeFromAliceToBob() throws InterruptedException, SharkException, IOException {
        this.setUpAliceBobExchangeScenario();
        List<SensorData> sd = new ArrayList<>();
        SensorData data1 = new SensorData("testName", 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,1355267532.0);
        sd.add(data1);
        when(aliceRepo.selectAllForId(anyString())).thenReturn(sd);
        doNothing().when(this.alicePoller).run();
        doNothing().when(this.bobRepo).insertNewEntries(sd);
        this.aliceComponent.checkForNewDataInDB();
        this.runEncounter(this.alicePeer,this.bobPeer, true);
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(this.bobRepo, Mockito.times(1)).insertNewEntries(argumentCaptor.capture());
        assertEquals(data1, argumentCaptor.getValue().get(0));
        assertEquals(data1,this.aliceComponent.getNewestEntry());
    }
    @Test
    public void checkForNewDataInDBWhenNewestEntryIsNull(){

    }
    @Test
    public void checkForNewDataInDBWhenSingleEntryIsInDB(){

    }
    @Test
    public void checkForNewDataInDBWhenMultipleEntriesIsInDB() throws SharkException, IOException, InterruptedException {
        this.setUpAliceBobExchangeScenario();
        List<SensorData> sd = new ArrayList<>();
        SensorData data1 = new SensorData(ALICE_ID, 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,1.355267532E9);
        SensorData data2 = new SensorData(ALICE_ID, 34.6,Unit.C,34.1,Unit.P,35.7,Unit.P,1.355297659E9);
        sd.add(data1);
        sd.add(data2);
        when(aliceRepo.selectAllForId(ALICE_ID)).thenReturn(sd);
        doNothing().when(this.alicePoller).run();
        doNothing().when(this.bobPoller).run();
        doNothing().when(this.bobRepo).insertNewEntries(anyList());
        this.aliceComponent.checkForNewDataInDB();
        this.runEncounter(this.alicePeer,this.bobPeer, true);
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(this.bobRepo,times(2)).insertNewEntries(argumentCaptor.capture());
        List<List> capturedList = argumentCaptor.<List<List<SensorData>>> getAllValues();

        assertEquals(data1,capturedList.get(0).get(0));
        assertEquals(data2,capturedList.get(1).get(0));

        assertEquals(data2,this.aliceComponent.getNewestEntry());

    }
    @Test
    public void checkForNewDataInDBWhenNoEntryIsInDB() throws SharkException, ASAPException {
        this.setUpAliceBobExchangeScenario();
        when(aliceRepo.selectAllForId(ALICE_ID)).thenReturn(null);
        this.aliceComponent.checkForNewDataInDB();
        ASAPPeer mockPeer = mock(ASAPPeerFS.class);
        SensorRepository repo = mock(SensorRepository.class);
        SharkSensorComponent component = new SharkSensorComponentImpl(repo, new SharkSensorSerializerImpl(new ObjectMapper()),ALICE_ID);
        component.onStart(mockPeer);
        verify(mockPeer,times(0)).sendASAPMessage(anyString(),anyString(),any());
        assertNull(aliceComponent.getNewestEntry());
    }
    @Test
    public void getNewestFromList(){
        List<SensorData> list = new ArrayList<>();
        SensorData data1 = new SensorData(ALICE_ID, 293.1,Unit.K,76.5,Unit.P,65.3,Unit.P,1.155267532E9);
        SensorData data2 = new SensorData(BOB_ID, 34.6,Unit.C,34.1,Unit.P,35.7,Unit.P,1.155297659E9);
        SensorData data3 = new SensorData(ALICE_ID, 92.1,Unit.F,25.5,Unit.P,78.3,Unit.P,1.255267532E9);
        SensorData data4 = new SensorData("TestId", -12.6,Unit.C,70.1,Unit.P,42.7,Unit.P,1.345297659E9);
        list.add(data1);
        list.add(data2);
        list.add(data3);
        list.add(data4);
        SensorRepository repo = mock(SensorRepository.class);
        SharkSensorComponentImpl component = new SharkSensorComponentImpl(repo, new SharkSensorSerializerImpl(new ObjectMapper()),ALICE_ID);
        assertEquals(data4,component.getNewestFromList(list));
    }
    @Test
    public void getNewestFromListWhenListIsNull(){
        SensorRepository repo = mock(SensorRepository.class);
        SharkSensorComponentImpl component = new SharkSensorComponentImpl(repo, new SharkSensorSerializerImpl(new ObjectMapper()),ALICE_ID);
        assertNull(component.getNewestFromList(null));
    }
    @Test
    public void getNewestFromListWhenNoEntry(){
        SensorRepository repo = mock(SensorRepository.class);
        SharkSensorComponentImpl component = new SharkSensorComponentImpl(repo, new SharkSensorSerializerImpl(new ObjectMapper()),ALICE_ID);
        assertNull(component.getNewestFromList(new ArrayList<>()));
    }
    @Test
    public void getNewestFromListWhenSingleEntry(){
        List<SensorData> list = new ArrayList<>();
        SensorData data = new SensorData("TestId", -12.6,Unit.C,70.1,Unit.P,42.7,Unit.P,1.345297659E9);

        list.add(data);
        SensorRepository repo = mock(SensorRepository.class);
        SharkSensorComponentImpl component = new SharkSensorComponentImpl(repo, new SharkSensorSerializerImpl(new ObjectMapper()),ALICE_ID);
        assertEquals(data,component.getNewestFromList(list));
    }
}
