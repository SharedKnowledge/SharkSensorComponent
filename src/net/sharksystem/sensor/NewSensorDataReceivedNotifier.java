package net.sharksystem.sensor;

public interface NewSensorDataReceivedNotifier {
    void addNewSensorDataReceivedListener(NewSensorDataReceivedListener listener);
    void removeSensorDataReceivedListener(NewSensorDataReceivedListener listener);
    void notifyReceived();
}
