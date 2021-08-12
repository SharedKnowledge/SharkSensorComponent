package net.sharksystem.sensor;

public class DBUpdatePoller extends Thread {

    private SharkSensorComponent component;

    public void run(){
        checkForUpdates();
    }

    public void addSharkSensorComponent(SharkSensorComponent component) {
        this.component = component;
    }
    private void checkForUpdates(){
        while(true){
            this.component.checkForNewDataInDB();
            //Sleep for 5 Minutes because new sensor data is sent to DB every 5 minutes
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
