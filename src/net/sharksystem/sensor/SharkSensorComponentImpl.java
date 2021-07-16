package net.sharksystem.sensor;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkException;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPPeer;

import java.io.File;
import java.io.IOException;

public class SharkSensorComponentImpl implements SharkSensorComponent {
    @Override
    public void onStart(ASAPPeer asapPeer) throws SharkException {
        // TODO

    }

    @Override
    public void setBehaviour(String s, boolean b) throws SharkUnknownBehaviourException, ASAPException, IOException {
        // IGNORE
    }

    public void readSensorDataFromFile(File dataFile) {
        // TODO
    }
}
