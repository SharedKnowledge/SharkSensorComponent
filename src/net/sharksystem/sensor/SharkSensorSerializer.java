package net.sharksystem.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface SharkSensorSerializer {

    String serializeSensorData(List<SensorData> dataList) throws JsonProcessingException, NullPointerException;

    String serializeSensorData(SensorData data) throws JsonProcessingException, NullPointerException;

    List<SensorData> deserializeSensorData(String jsonString);
}
