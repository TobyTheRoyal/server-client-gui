package at.tugraz.oop2.worker;

import at.tugraz.oop2.shared.FractalLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    public static void main(String[] args) {
        Input input = new Input(args);
            try{
                ServerSocket servSock = new ServerSocket(input.getPort().get());
                FractalLogger.logWorkerStart(input.getPort().get());
                while(true) {
                    Socket clientSocket = servSock.accept();
                    new SplitWorkout(clientSocket).start(); // do calculations
                }
         } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
