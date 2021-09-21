package net.sharksystem.sensor;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;

/**The Code is based on:
 * https://github.com/SharedKnowledge/SharkCreditMoney/blob/master/tests/net/sharksystem/creditmoney/TestHelper.java
 * **/

public class TestHelper {

    static SharkSensorComponent setupComponent(
            SharkPeer sharkPeer,
            SensorRepository repo,
            SharkSensorSerializer serializer,
            DBUpdateSocket poller,
            String sensorId) throws SharkException {

        SharkSensorComponentFactory factory = new SharkSensorComponentFactory(repo,serializer,poller,sensorId);
        sharkPeer.addComponent(factory, SharkSensorComponent.class);
        return (SharkSensorComponent) sharkPeer.getComponent(SharkSensorComponent.class);
    }
}
