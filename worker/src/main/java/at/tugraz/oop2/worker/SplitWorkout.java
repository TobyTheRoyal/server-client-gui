package at.tugraz.oop2.worker;

import at.tugraz.oop2.shared.*;

import java.io.*;
import java.net.Socket;

public class SplitWorkout extends Thread {
        protected Socket socket;
        protected MandelbrotRenderOptions mandleRender;
        protected JuliaRenderOptions juliaRe;
        public SplitWorkout(Socket clientSocket) {
            this.socket = clientSocket;
                    }

        public void run() {
            try {
            InputStream inputToServer = this.socket.getInputStream();
            ObjectInputStream in = new ObjectInputStream((inputToServer));
                    Object test = in.readObject();
                    if (test instanceof MandelbrotRenderOptions) {
                        this.mandleRender = (MandelbrotRenderOptions) test;
                        FractalLogger.logReceivePackageWorker(this.mandleRender);
                        ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
                        out.flush();
                        FractalRenderResult mandel;
                        mandel = MandelBrotCalc.MandelbrotCalculation(this.mandleRender);
                        ObjectOutputStream out_mandel = new ObjectOutputStream(this.socket.getOutputStream());
                        out_mandel.flush();
                        out_mandel.writeObject(mandel);

                    } else {
                        this.juliaRe = (JuliaRenderOptions) test;
                        FractalLogger.logReceivePackageWorker(this.juliaRe);
                        FractalRenderResult julia;
                        julia = JuliaCalc.JuliaCalculation(this.juliaRe);
                        ObjectOutputStream out_julia = new ObjectOutputStream(this.socket.getOutputStream());
                        out_julia.flush();
                        out_julia.writeObject(julia);
                    }

            }catch (IOException | ClassNotFoundException | InterruptedException e)
            {
              e.printStackTrace();
            }
        }
    }
