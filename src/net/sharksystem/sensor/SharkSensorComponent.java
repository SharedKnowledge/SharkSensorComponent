package net.sharksystem.sensor;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;

import java.util.Date;
import java.util.List;

@ASAPFormats(formats = {SharkSensorComponent.APP_FORMAT})
public interface SharkSensorComponent extends SharkComponent {
    String APP_FORMAT = "shark/sensor";
    String URI = "sharkSensor://sensorData";


    List<SensorData> getSensorDataForId(String id);

    SensorData getSensorDataForIdAndTime(String id, Date dt);

    void checkForNewDataInDB();

    void sendNewSensorDataToASAPPeer(SensorData sensorData);



    }
