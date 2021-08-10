package net.sharksystem.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SharkSensorSerializerImpl implements SharkSensorSerializer {

    private ObjectMapper mapper;

    SharkSensorSerializerImpl(ObjectMapper mapper, SimpleDateFormat dateFormat){
        this.mapper = mapper;
        this.mapper.setDateFormat(dateFormat);
    }

    @Override
    public String serializeSensorData(List<SensorData> dataList) throws JsonProcessingException, NullPointerException{
        List<SenMLObject> senMLList = new ArrayList<>();
        String jsonString;
        for(SensorData data: dataList){
            List<SenMLObject> tempSenMLList = sensorDataToSenMLList(data);
            //some entries in the list might be null, but that's okay

            if(tempSenMLList!=null)
                senMLList.addAll(tempSenMLList);


        }
        //All entries are null, no point in serializing an empty list
        if(senMLList.isEmpty()){
            throw new NullPointerException();
        }
        jsonString = this.mapper.writeValueAsString(senMLList);

        return jsonString;
    }

    @Override
    public String serializeSensorData(SensorData data) throws JsonProcessingException, NullPointerException {
        return this.serializeSensorData(Collections.singletonList(data));
    }

    @Override
    public List<SensorData> deserializeSensorData(String jsonString) throws NullPointerException {
        if(jsonString==null){
            throw new NullPointerException();
        }
        if(jsonString==""){
            throw new NullPointerException();
        }
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
                        else{
                            throw new NullPointerException();
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
        }
        return sensorDataList;
    }

    private List<SenMLObject> sensorDataToSenMLList(SensorData data) throws JsonProcessingException{
        List<SenMLObject> senMLList = new ArrayList<>();
        if(data == null){
            return null;
        }
        if(data.getBn() != null && data.getDt() != null
                && data.getHumUnit() != null && data.getSoilUnit() != null && data.getTempUnit() != null) {
            senMLList.add(new SenMLObject(data.getBn(), data.getDt(), "temp", data.getTempUnit().toString(), data.getTemp()));
            senMLList.add(new SenMLObject("humidity", data.getHumUnit().toString(), data.getHum()));
            senMLList.add(new SenMLObject("soil", data.getSoilUnit().toString(), data.getSoil()));
        }
        else{
            throw new JsonProcessingException("Some field is missing in your sensorData"){};
        }
        return senMLList;
    }
}
