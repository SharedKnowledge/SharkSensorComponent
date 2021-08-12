package net.sharksystem.sensor;

import java.util.Date;
import java.util.List;

public interface SensorRepository {

    //Select all entries from database
    List<SensorData> selectAll();

    //Select all entries with a certain id
    List<SensorData> selectAllForId(String sensorId);

    //Select one specific entry with id and datetime
    SensorData selectSpecificEntry(String sensorId, Date dt);

    //Insert a list with dataEntries
    void insertNewEntries(List<SensorData> newList);

    //Select entries for sensorId that are newer than a given datetime
    List<SensorData> selectForIdNewerThan(Date date, String sensorId);
    }
