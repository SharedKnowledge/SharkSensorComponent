package net.sharksystem.sensor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SensorRepositoryImpl implements SensorRepository {

    private String url;

    public SensorRepositoryImpl(String url){
        this.url = url;
    }

    private Connection connect(){
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(this.url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public List<SensorData> selectAll(){

        String selectSQL = "SELECT * FROM sensor_data;";
        List<SensorData> entryList = new ArrayList<>();
        try(Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectSQL)) {
            entryList = getObjectsFromResultSet(rs);

        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return entryList;
    }

    @Override
    public List<SensorData> selectAllForId(String sensorId) {
        String selectSQL = "SELECT * FROM sensor_data WHERE base_name = ?;";
        List<SensorData> entryList = new ArrayList<>();
        try(Connection conn = this.connect();
        PreparedStatement pstmt = conn.prepareStatement(selectSQL)){
            pstmt.setString(1, sensorId);
            ResultSet rs = pstmt.executeQuery();

            entryList = getObjectsFromResultSet(rs);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entryList;
    }

    private List<SensorData> getObjectsFromResultSet(ResultSet rs) throws SQLException {
        List<SensorData> entryList = new ArrayList<>();
        while(rs.next()){
            try{
                SensorData entry = new SensorData(
                    rs.getString("base_name"),
                    rs.getDouble("temp"),
                        Unit.valueOf(rs.getString("temp_unit")),
                    rs.getDouble("soil"),
                        Unit.valueOf(rs.getString("soil_unit")),
                    rs.getDouble("hum"),
                        Unit.valueOf(rs.getString("hum_unit")),
                    rs.getDouble("dt"));

                entryList.add(entry);
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
        return entryList;
    }

    @Override
    public SensorData selectSpecificEntry(String sensorId, double dt) {
        String selectSQL = "SELECT * FROM sensor_data WHERE base_name = ? AND dt = ?;";

        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(selectSQL)){
            pstmt.setString(1, sensorId);
            pstmt.setDouble(2, dt);
            ResultSet rs = pstmt.executeQuery();

            return new SensorData(
                    rs.getString("base_name"),
                    rs.getDouble("temp"),
                    Unit.valueOf(rs.getString("temp_unit")),
                    rs.getDouble("soil"),
                    Unit.valueOf(rs.getString("soil_unit")),
                    rs.getDouble("hum"),
                    Unit.valueOf(rs.getString("hum_unit")),
                    (rs.getDouble("dt")));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void insertNewEntries(List<SensorData> newList){
        List<SensorData> oldList = this.selectAll();
        newList.removeAll(oldList);
        String sql = "INSERT INTO sensor_data VALUES(?,?,?,?,?)";

        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            for(SensorData entry: newList){
            pstmt.setString(1, entry.getBn());
            pstmt.setDouble(2, entry.getDt());
            pstmt.setDouble(3,entry.getTemp());
            pstmt.setDouble(4, entry.getHum());
            pstmt.setDouble(5, entry.getSoil());
            pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //dt stores the seconds since begin of Linux time as float in db. If a value is greater than a given value,
    //that means, that it is newer because more time has passed since 1970
    @Override
    public List<SensorData> selectForIdNewerThan(double date, String sensorId) {
        String selectSQL = "SELECT * FROM sensor_data WHERE base_name = ? AND dt > ?);";
        List<SensorData> entryList = new ArrayList<>();
        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(selectSQL)){
            pstmt.setString(1, sensorId);
            pstmt.setDouble(2, date);
            ResultSet rs = pstmt.executeQuery();

            entryList = getObjectsFromResultSet(rs);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entryList;    }

}
