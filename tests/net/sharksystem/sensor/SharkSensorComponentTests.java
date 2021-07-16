package net.sharksystem.sensor;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.SharkTestPeerFS;
import org.junit.Test;

public class SharkSensorComponentTests {
    static final CharSequence ALICE = "Alice";
    static final CharSequence ROOTFOLDER = "sharkComponent";
    static final CharSequence ALICE_ROOTFOLDER = ROOTFOLDER + "/" + ALICE;

    @Test
    public void simple() throws SharkException {
        SharkTestPeerFS.removeFolder(ALICE_ROOTFOLDER); // clean previous version before

        SharkPeerFS sPeer = new SharkPeerFS(ALICE, ALICE_ROOTFOLDER);
        SharkSensorComponentFactory factory = new SharkSensorComponentFactory();
        Class facadeClass = SharkSensorComponent.class;
        sPeer.addComponent(factory, facadeClass);

        sPeer.getComponent(SharkSensorComponent.class);
        sPeer.start();

        // read file...
    }
}
