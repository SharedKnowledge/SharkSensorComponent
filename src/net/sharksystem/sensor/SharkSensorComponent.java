package net.sharksystem.sensor;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;

import java.io.File;

@ASAPFormats(formats = {SharkSensorComponent.APP_FORMAT})
public interface SharkSensorComponent extends SharkComponent {
    String APP_FORMAT = "myApp://formatA";

    void readSensorDataFromFile(File dataFile);
}
