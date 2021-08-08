package net.sharksystem.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SharkSensorSerializerImpl implements SharkSensorSerializer {

    private ObjectMapper mapper;

    SharkSensorSerializerImpl(ObjectMapper mapper, SimpleDateFormat dateFormat){
        this.mapper = mapper;
        this.mapper.setDateFormat(dateFormat);
    }

    @Override
    public String serializeSensorData(List<SensorData> dataList) {
        List<SenMLObject> senMLList = new ArrayList<>();
        String jsonString = "";
        for(SensorData data: dataList){
            senMLList.addAll(sensorDataToSenMLList(data));
        }
        try {
            jsonString = this.mapper.writeValueAsString(senMLList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    @Override
    public String serializeSensorData(SensorData data) {

        String jsonString = "";
        List<SenMLObject> senMLList = sensorDataToSenMLList(data);

        try {
            jsonString = this.mapper.writeValueAsString(senMLList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    @Override
    public List<SensorData> deserializeSensorData(String jsonString) {
        List<SensorData> sensorDataList = new ArrayList<>();
        SensorData entry = new SensorData();
        try {
            List<SenMLObject> data = this.mapper.readValue(jsonString, new TypeReference<List<SenMLObject>>() {
            });
            //convert senMLObjects to SensorData
            for (SenMLObject senMLObject : data) {
                switch (senMLObject.n) {
                    case "temp":
                        if (senMLObject.bn != null) {
                            entry.setBn(senMLObject.bn);
                            entry.setDt(senMLObject.bt);
                            entry.setTempUnit(Unit.valueOf(senMLObject.u));
                            entry.setTemp(senMLObject.v);
                        }
                        break;
                    case "humidity":
                        entry.setHumUnit(Unit.valueOf(senMLObject.u));
                        entry.setHum(senMLObject.v);
                        break;
                    case "soil":
                        entry.setSoilUnit(Unit.valueOf(senMLObject.u));
                        entry.setSoil(senMLObject.v);
                        sensorDataList.add(entry);
                        entry = new SensorData();
                        break;
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return sensorDataList;
    }

    private List<SenMLObject> sensorDataToSenMLList(SensorData data){
        List<SenMLObject> senMLList = new ArrayList<>();

        senMLList.add(new SenMLObject(data.bn,data.dt,"temp",data.tempUnit.toString(), data.temp));
        senMLList.add(new SenMLObject("humidity",data.humUnit.toString(), data.hum));
        senMLList.add(new SenMLObject("soil",data.soilUnit.toString(), data.soil));

        return senMLList;
    }
}
