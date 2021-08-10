package net.sharksystem.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SharkSensorSerializerImplTest {

    private DateHelper helper;
    private SharkSensorSerializer serializer;

    @BeforeEach
    void setup(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        ObjectMapper mapper = new ObjectMapper();
        this.helper = new DateHelperImpl(dateFormat);
        this.serializer = new SharkSensorSerializerImpl(mapper,dateFormat);

    }

    @Test
    void serializeSensorData() throws ParseException, JsonProcessingException {
        SensorData data1 = new SensorData("testName", 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,helper.StringToDate("12-12-2012 12:12:12"));
        SensorData data2 = new SensorData("testName2", 34.6,Unit.C,34.1,Unit.P,35.7,Unit.P,helper.StringToDate("12-12-2012 08:34:19"));
        SensorData data3 = new SensorData("testName3", -21.6,Unit.F,87.9,Unit.P,52.6,Unit.P,helper.StringToDate("11-11-2011 04:20:42"));

        List<SensorData> dataList = new ArrayList<>();
        dataList.add(data1);
        dataList.add(data2);
        dataList.add(data3);

        String jsonString = serializer.serializeSensorData(dataList);

        assertEquals("[{\"bn\":\"testName\",\"bt\":\"12-12-2012 12:12:12\"," +
                "\"n\":\"temp\",\"u\":\"K\",\"v\":23.1}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":65.3}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":76.5}," +
                "{\"bn\":\"testName2\",\"bt\":\"12-12-2012 08:34:19\"," +
                "\"n\":\"temp\",\"u\":\"C\",\"v\":34.6}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":35.7}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":34.1}," +
                "{\"bn\":\"testName3\",\"bt\":\"11-11-2011 04:20:42\"," +
                "\"n\":\"temp\",\"u\":\"F\",\"v\":-21.6}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":52.6}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":87.9}]",jsonString);
    }

    @Test
    void serializeSensorData1() throws JsonProcessingException, ParseException {
        SensorData data1 = new SensorData("testName", 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,helper.StringToDate("12-12-2012 12:12:12"));
        String jsonString = serializer.serializeSensorData(data1);
        assertEquals("[{\"bn\":\"testName\",\"bt\":\"12-12-2012 12:12:12\"," +
                "\"n\":\"temp\",\"u\":\"K\",\"v\":23.1}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":65.3}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":76.5}]",jsonString);
    }
    @Test
    void serializeSensorDataWhenNoBnGiven(){
        assertThrows(JsonProcessingException.class, () ->  {

        SensorData data1 = new SensorData(null, 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,helper.StringToDate("12-12-2012 12:12:12"));
        serializer.serializeSensorData(data1);
        });
    }
    @Test
    void serializeSensorDataWithMissingUnit(){
        assertThrows(JsonProcessingException.class, () ->  {

            SensorData data1 = new SensorData("testName", 23.1,null,76.5,Unit.P,65.3,Unit.P,helper.StringToDate("12-12-2012 12:12:12"));
            serializer.serializeSensorData(data1);
        });
    }
    @Test
    void serializeSensorDataWithMissingTime(){
        assertThrows(JsonProcessingException.class, () ->  {

            SensorData data1 = new SensorData("testName", 23.1,Unit.K,76.5,Unit.P,65.3,Unit.P,null);
            serializer.serializeSensorData(data1);
        });
    }
    @Test
    void serializeWhenSensorDataIsNull(){
        assertThrows(NullPointerException.class, () ->  {
            serializer.serializeSensorData((SensorData) null);
        });
    }


    @Test
    void deserializeSingleSensorDataObject() throws ParseException {
        String json = "[{\"bn\":\"testName\",\"bt\":\"12-12-2012 12:12:12\"," +
                "\"n\":\"temp\",\"u\":\"K\",\"v\":23.1}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":65.3}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":76.5}]";
        List<SensorData> list = serializer.deserializeSensorData(json);
        SensorData obj = list.get(0);
        assertEquals("testName",obj.getBn());
        assertEquals(helper.StringToDate("12-12-2012 12:12:12"), obj.getDt());
        assertEquals(Unit.K, obj.getTempUnit());
        assertEquals(23.1, obj.getTemp());
        assertEquals(Unit.P, obj.getHumUnit());
        assertEquals(65.3, obj.getHum());
        assertEquals(Unit.P, obj.getSoilUnit());
        assertEquals(76.5, obj.getSoil());

    }
    @Test
    void deserializeNullString() throws ParseException {
        String json = null;
        assertThrows(NullPointerException.class, () -> {
            serializer.deserializeSensorData(json);
        });

    }
    @Test
    void deserializeEmptyString() throws ParseException {
        String json = "";
        assertThrows(NullPointerException.class, ()->{
            List<SensorData> list = serializer.deserializeSensorData(json);
        });

    }

    @Test
    void deserializeSingleSensorDataObjectWithMissingBn(){
        assertThrows(NullPointerException.class, ()->{
        String json = "[{\"bt\":\"12-12-2012 12:12:12\"," +
                "\"n\":\"temp\",\"u\":\"K\",\"v\":23.1}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":65.3}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":76.5}]";
        serializer.deserializeSensorData(json);
        });
    }

    @Test
    void deserializeSingleSensorDataObjectWithMissingTempUnit(){
        assertThrows(NullPointerException.class, ()->{
            String json = "[{\"bn\":\"testName\",\"bt\":\"12-12-2012 12:12:12\"," +
                    "\"n\":\"temp\",\"v\":23.1}," +
                    "{\"n\":\"humidity\",\"u\":\"P\",\"v\":65.3}," +
                    "{\"n\":\"soil\",\"u\":\"P\",\"v\":76.5}]";
            serializer.deserializeSensorData(json);
        });
    }

    @Test
    void deserializeSingleSensorDataObjectWithMissingHumUnit(){
        assertThrows(NullPointerException.class, ()->{
            String json = "[{\"bn\":\"testName\",\"bt\":\"12-12-2012 12:12:12\"," +
                    "\"n\":\"temp\",\"u\":\"K\",\"v\":23.1}," +
                    "{\"n\":\"humidity\",\"v\":65.3}," +
                    "{\"n\":\"soil\",\"u\":\"P\",\"v\":76.5}]";
            serializer.deserializeSensorData(json);
        });
    }

    @Test
    void deserializeSingleSensorDataObjectWithMissingSoilUnit(){
        assertThrows(NullPointerException.class, ()->{
            String json = "[{\"bn\":\"testName\",\"bt\":\"12-12-2012 12:12:12\"," +
                    "\"n\":\"temp\",\"u\":\"K\",\"v\":23.1}," +
                    "{\"n\":\"humidity\",\"u\":\"P\",\"v\":65.3}," +
                    "{\"n\":\"soil\",\"v\":76.5}]";
            serializer.deserializeSensorData(json);
        });
    }
    @Test
    void deserializeSingleSensorDataObjectWithMissingTemp() throws ParseException {
        String json = "[{\"bn\":\"testName\",\"bt\":\"12-12-2012 12:12:12\"," +
                "\"n\":\"temp\",\"u\":\"K\"}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":65.3}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":76.5}]";
        List<SensorData> list = serializer.deserializeSensorData(json);
        SensorData obj = list.get(0);
        assertEquals(0.0, obj.getTemp());
    }

    @Test
    void deserializeMultipleSensorDataObjects() {
        String json = "[{\"bn\":\"testName\",\"bt\":\"12-12-2012 12:12:12\"," +
                "\"n\":\"temp\",\"u\":\"K\",\"v\":23.1}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":65.3}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":76.5}," +
                "{\"bn\":\"testName2\",\"bt\":\"12-12-2012 08:34:19\"," +
                "\"n\":\"temp\",\"u\":\"C\",\"v\":34.6}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":35.7}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":34.1}," +
                "{\"bn\":\"testName3\",\"bt\":\"11-11-2011 04:20:42\"," +
                "\"n\":\"temp\",\"u\":\"F\",\"v\":-21.6}," +
                "{\"n\":\"humidity\",\"u\":\"P\",\"v\":52.6}," +
                "{\"n\":\"soil\",\"u\":\"P\",\"v\":87.9}]";
        List<SensorData> list = serializer.deserializeSensorData(json);
        assertEquals(3, list.size());
        assertEquals("testName", list.get(0).getBn());
        assertEquals("testName2", list.get(1).getBn());
        assertEquals("testName3", list.get(2).getBn());

    }
}