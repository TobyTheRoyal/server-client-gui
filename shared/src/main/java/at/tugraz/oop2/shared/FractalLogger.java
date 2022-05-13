package at.tugraz.oop2.shared;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


public class FractalLogger {

    private static Logger _logger;

    private static Logger logger() {
        if (_logger == null) {
            _logger = Logger.getLogger("FRACTALLOGGER");
            BasicConfigurator.configure();
        }
        return _logger;
    }

    /**
     * Should be called after a drag-gesture was detected
     *
     * @param x    the new x-parameter (center of affected fractal)
     * @param y    the new y-parameter (center of affected fractal)
     * @param type the type of fractal affected
     */
    public static void logDrag(double x, double y, FractalType type) {
        logger().debug("Dragged view to " + x + "/" + y + ", type: " + type);

    }

    /**
     * Log after changing the zoom level
     *
     * @param zoom the new zoom value
     * @param type the type of fractal affected
     */
    public static void logZoom(double zoom, FractalType type) {
        logger().debug("Zoomed view to" + zoom + ", type:" + type);
    }

    /**
     * Logged before calling an algorithm function
     *
     * @param options the options passed on
     */
    public static void logRenderCall(FractalRenderOptions options) {
        if (options instanceof JuliaRenderOptions) {
            logger().debug(String.format("Render call issued for type %s, center: %f/%f, dimensions: %d/%d, zoom: %f, iterations: %d, constant: %f/%f",
                    options.type, options.centerX, options.centerY, options.width, options.height, options.zoom, options.iterations,
                    ((JuliaRenderOptions) options).getConstantX(), ((JuliaRenderOptions) options).getConstantY()));
        } else {
            logger().debug(String.format("Render call issued for type %s, center: %f/%f, dimensions: %d/%d, zoom: %f, iterations: %d",
                    options.type, options.centerX, options.centerY, options.width, options.height, options.zoom, options.iterations));

        }
    }

    /**
     * logged **after** drawing to the screen!
     *
     * @param type the fractaltype which was updated
     */
    public static void logDrawDone(FractalType type) {
        logger().debug("Draw call issued:  " + type);


    }

    /**
     * logged after finishing a render call but before updating the canvas
     *
     * @param type  the type which was rendered
     * @param image the resulting image
     */
    public static void logRenderFinished(FractalType type, SimpleImage image) {
        short[] center_colour = new short[image.getDepth()];
        try {
            center_colour = image.getPixel(image.getWidth() / 2, image.getHeight() / 2);
        } catch (Exception e) {
        }
        logger().debug("Finished render: " + type + " center colour: rgb(" + center_colour[0] + "," + center_colour[1] + "," + center_colour[2] + ")");

    }

    /**
     * called directly after initializing all parameters
     * please make sure to pass real Properties (two-way binding!) to ensure proper functionality!
     *
     * @param mandelbrotX
     * @param mandelbrotY
     * @param mandelbrotZoom
     * @param iteration
     * @param juliaX
     * @param juliaY
     * @param juliaZoom
     * @param mode
     */
    public static void logArguments(DoubleProperty mandelbrotX, DoubleProperty mandelbrotY, DoubleProperty mandelbrotZoom, IntegerProperty iteration,
                                    DoubleProperty juliaX, DoubleProperty juliaY, DoubleProperty juliaZoom, Property<ColourModes> mode) {

        logger().debug("Set up arguments: mandelbrotX: " + mandelbrotX.get() + ", mandelbrotY: " + mandelbrotY.get() + ", mandelbrotZoom: " + mandelbrotZoom.get()
                + ", juliaX: " + juliaX.get() + ", juliaY: " + juliaY.get() + ", juliaZoom: " + juliaZoom.get() + ", iterations: " + iteration.get() + ", colourMode: " + mode.getValue()
        );
        mandelbrotX.addListener(observable -> {
            logger().debug("Changed mandelbrotX");
        });
        mandelbrotY.addListener(observable -> {
            logger().debug("Changed mandelbrotY");
        });
        mandelbrotZoom.addListener(observable -> {
            logger().debug("Changed mandelbrotZoom");
        });


        juliaX.addListener(observable -> {
            logger().debug("Changed juliaX");
        });
        juliaY.addListener(observable -> {
            logger().debug("Changed juliaY");
        });
        juliaZoom.addListener(observable -> {
            logger().debug("Changed juliaZoom");
        });

        iteration.addListener(observable -> {
            logger().debug("Changed iterations");
        });
        mode.addListener(observable -> {
            logger().debug("Changed colourMode");
        });


    }

    /**
     * logged after the GUI is ready to be used
     *
     * @param mainPane
     * @param primaryStage
     * @param leftCanvas
     * @param rightCanvas
     */
    public static void logGUIInitialized(Pane mainPane, Stage primaryStage, Canvas leftCanvas, Canvas rightCanvas) {
        logger().debug("Initialized UI");
    }


    /**
     * Log the new reactive properties (like above)
     *
     * @param mode
     * @param tasksPerWorker
     * @param connection     doesn't have to be reactive, just pass the correct value
     */
    public static void logDistributionArguments(Property<RenderMode> mode, IntegerProperty tasksPerWorker, StringProperty connection) {
        logger().debug("Logged distribution arguments: mode: " + mode.getValue() + ", tasksPerWorker: " + tasksPerWorker.getValue() +
                ", connection:" + connection.getValue());
        mode.addListener(observable -> {
            logger().debug("Changed RenderMode");
        });
        tasksPerWorker.addListener(observable -> {
            logger().debug("Changed tasksPerWorker");
        });
    }

    /**
     * Call when the worker (server) starts
     *
     * @param port
     */
    public static void logWorkerStart(int port) {
        logger().debug("Started worker on port " + port);
    }

    /**
     * Log after successfully establishing a connection to the worker
     *
     * @param targetAddress
     * @param targetPort
     */
    public static void logConnectionOpenedGUI(String targetAddress, int targetPort) {
        logger().debug("GUI connected to " + targetAddress + ":" + targetPort);
    }

    /**
     * Log after disconnecting from worker
     *
     * @param targetAddress
     * @param targetPort
     */
    public static void logConnectionLostGUI(String targetAddress, int targetPort) {
        logger().debug("GUI lost connection to " + targetAddress + ":" + targetPort);
    }

    /**
     * Log if the worker accepted a new connection
     */
    public static void logConnectionOpenedWorker() {
        logger().debug("Worker got a new connection!");
    }

    /**
     * Log after disconnecting from the GUI
     */
    public static void logConnectionLostWorker() {
        logger().debug("Worker lost a connection...");
    }

    /**
     * Log upon sending or re-issuing a package
     *
     * @param options the options to render
     * @param index   the index of the package within this render call
     * @param total   the total number of packages this render call will dispatch
     */
    public static void logSendPackageGUI(FractalRenderOptions options, int index, int total) {
        logger().debug(String.format("Sending a single package of type %s to the worker (%d/%d)", options.getType(), index+1, total));
    }

    /**
     * Log upon receiving a work package in the worker from the GUI
     *
     * @param options
     */
    public static void logReceivePackageWorker(FractalRenderOptions options) {
        logger().debug(String.format("Worker got a single package of type %s", options.getType()));
    }

    /**
     * Log upon sending a work package in the worker to the GUI
     *
     * @param options
     */
    public static void logSendingPackageWorker(FractalRenderOptions options) {
        logger().debug(String.format("Worker got a single package of type %s", options.getType()));
    }

    /**
     * Log upon receiving a finished work package in the GUI
     *
     * @param options
     */
    public static void logReceivePackageGUI(FractalRenderOptions options) {
        logger().debug(String.format("GUI got a finished package of type %s", options.getType()));
    }

    /**
     * Log upon a failed package (connection lost during rendering) in the GUI
     *
     * @param options
     */
    public static void logFailedPackageGUI(FractalRenderOptions options) {
        logger().debug(String.format("GUI got a single failed package of type back (should re-issue) %s", options.getType()));
    }

}
