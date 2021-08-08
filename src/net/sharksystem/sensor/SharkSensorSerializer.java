package net.sharksystem.sensor;

import java.util.List;

public interface SharkSensorSerializer {

    String serializeSensorData(List<SensorData> dataList);

    String serializeSensorData(SensorData data);

    List<SensorData> deserializeSensorData(String jsonString);
}
