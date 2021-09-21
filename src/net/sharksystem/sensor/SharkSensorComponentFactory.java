package net.sharksystem.sensor;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;


public class SharkSensorComponentFactory implements SharkComponentFactory {

    private final SharkSensorSerializer serializer;
    private final SensorRepository repo;
    private final DBUpdateSocket dbUpdateListener;
    private final String sensorId;

    public SharkSensorComponentFactory(
            SensorRepository repo,
            SharkSensorSerializer serializer,
            DBUpdateSocket dbUpdatePoller,
            String sensorId){
        this.repo = repo;
        this.serializer = serializer;
        this.dbUpdateListener = dbUpdatePoller;
        this.sensorId = sensorId;
    }

    @Override
    public SharkComponent getComponent() {
        SharkSensorComponent component = new SharkSensorComponentImpl(
                this.repo, this.serializer, this.sensorId);
        if(dbUpdateListener!=null) {
            dbUpdateListener.addSharkSensorComponent(component);
            dbUpdateListener.setDaemon(true);
            dbUpdateListener.start();
        }
        return component;
    }
}
