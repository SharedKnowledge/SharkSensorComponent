package net.sharksystem.sensor;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;
import net.sharksystem.asap.ASAPMessages;

import java.io.IOException;
import java.util.List;

@ASAPFormats(formats = {SharkSensorComponent.APP_FORMAT})
public interface SharkSensorComponent extends SharkComponent, NewSensorDataReceivedNotifier {
    String APP_FORMAT = "shark/sensor";
    String URI = "sharkSensor://sensorData";

    List<SensorData> getSensorDataForId(String id);

    SensorData getSensorDataForIdAndTime(String id, double dt);

    void checkForNewDataInDB();

    void sendSensorData(List<SensorData> sensorData);

    void receiveSensorData(List<SensorData> sensorData) throws IOException;

    public List<String> getAllBaseNames();

}
