package net.sharksystem.sensor;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;


public class SharkSensorComponentFactory implements SharkComponentFactory {

    private final SharkSensorSerializer serializer;
    private final SensorRepository repo;

    public SharkSensorComponentFactory(SensorRepository repo, SharkSensorSerializer serializer){
        this.repo = repo;
        this.serializer = serializer;
    }

    @Override
    public SharkComponent getComponent() {
        return new SharkSensorComponentImpl(
                this.repo, this.serializer);
    }
}
