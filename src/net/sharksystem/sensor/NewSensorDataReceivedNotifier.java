package net.sharksystem.sensor;

public interface NewSensorDataReceivedNotifier {
    public void addNewSensorDataReceivedListener(NewSensorDataReceivedListener listener);
    public void removeSensorDataReceivedListener(NewSensorDataReceivedListener listener);
}
