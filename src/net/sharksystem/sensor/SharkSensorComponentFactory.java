package net.sharksystem.sensor;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;

public class SharkSensorComponentFactory implements SharkComponentFactory {
    @Override
    public SharkComponent getComponent() {
        return new SharkSensorComponentImpl();
    }
}
