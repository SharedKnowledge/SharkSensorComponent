package net.sharksystem.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class DBUpdateSocket extends Thread {

    private int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private boolean readingSocket;


    private SharkSensorComponent component;

    public DBUpdateSocket(int port){
        this.port = port;
        readingSocket = true;
    }

    public void run(){
        checkForUpdates();
    }

    public void addSharkSensorComponent(SharkSensorComponent component) {
        this.component = component;
    }


    private void checkForUpdates(){
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while (readingSocket) {
            System.out.println("try to read");
            String msg = in.readLine();
            System.out.println("Received msg: " + msg);
            if(msg == null){
                System.err.println("Connection to python program was lost. Try to restart both programs");
                this.stopSocket();
                readingSocket=false;
                System.exit(1);
            }
            else{
                this.component.checkForNewDataInDB();
            }
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopSocket() throws IOException {
        readingSocket = false;
        System.out.println("Shutting down dbNotificationSocket");
        if(!clientSocket.isClosed()){
            clientSocket.close();
        }
        if(!serverSocket.isClosed()) {
            serverSocket.close();
        }
        in.close();
    }
}
