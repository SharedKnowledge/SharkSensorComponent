package net.sharksystem.sensor;

import java.util.Date;
import java.util.List;

public interface SensorRepository {
    List<SensorData> selectAll();
    List<SensorData> selectAllForId(String sensorId);
    SensorData selectSpecificEntry(String sensorId, Date dt);
    void insertNewEntries(List<SensorData> newList);

    }
