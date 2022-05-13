package at.tugraz.oop2.gui;

import at.tugraz.oop2.inputhandle.Input;
import at.tugraz.oop2.shared.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class FractalApplication extends Application {

    private GridPane mainPane;
    private Canvas rightCanvas;
    private Canvas leftCanvas;
    private Timeline timeline;
    BooleanProperty completedProperty = new SimpleBooleanProperty(false);
    double originalWidth = 1080;
    double originalHeight = 720;


    private double width_drag,height_drag ,aspect_ratio ;


    @Getter
    private final DoubleProperty leftHeight = new SimpleDoubleProperty();
    @Getter
    private final DoubleProperty leftWidth = new SimpleDoubleProperty();
    @Getter
    private final DoubleProperty rightHeight = new SimpleDoubleProperty();
    @Getter
    private final DoubleProperty rightWidth = new SimpleDoubleProperty();

    protected static class  Converter extends NumberStringConverter {
        @Override
        public Number fromString(String string) {
            if (string == null) {
                return 0;
            } else {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException ex) {
                    return 0;
                }
            }
        }
    }

    private void serviceCheck(Service<Void> service, int check, AtomicReference<Boolean> scrollInProgress)
    {

        if (check == 0)
        {
            if(scrollInProgress.get() == Boolean.FALSE) {
                if (service.isRunning() || service.getState() == Worker.State.FAILED) {
                    service.cancel();
                }
                if (service.getState() == Worker.State.SUCCEEDED) {
                    service.reset();
                }
                service.restart();
            }
        }
        else
        {
            if(service.isRunning() ||service.getState() == Worker.State.FAILED)
            {
                service.cancel();
            }
            if(service.getState() == Worker.State.SUCCEEDED)
            {
                service.reset();
            }
            service.restart();
        }

    }

    private void updateSizes() {

        Bounds leftSize = mainPane.getCellBounds(0, 0);
        Bounds rightSize = mainPane.getCellBounds(1, 0);

        leftCanvas.widthProperty().set(leftSize.getWidth());
        leftCanvas.heightProperty().set(leftSize.getHeight());
        rightCanvas.widthProperty().set(rightSize.getWidth());
        rightCanvas.heightProperty().set(rightSize.getHeight());
    }

    public  void  draw(Input inp){
        Platform.runLater(this::updateSizes);

        GraphicsContext gui = leftCanvas.getGraphicsContext2D();
        GraphicsContext gui_julia = rightCanvas.getGraphicsContext2D();
        FractalRenderResult mandel = null;
        FractalRenderResult julia = null;
        try {
                mandel = MandelBrotCalc.MandelbrotCalculation(leftCanvas, inp.getIterations(), inp.getMandelbrotX(), inp.getMandelbrotY(), inp.getMandelbrotZoom(), inp.getColourMode());
                julia = JuliaCalc.JuliaCalculation(rightCanvas, inp.getIterations(), inp.getJuliaX(), inp.getJuliaY(), inp.getJuliaZoom(), inp.getMandelbrotX(), inp.getMandelbrotY(), inp.getColourMode());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        assert mandel != null;
        SimpleImage mandelImageData = mandel.getImageData();
         Platform.runLater(() -> {
             for(int y = 0; y < mandelImageData.getHeight(); y++)
             {
                 for (int x = 0; x < mandelImageData.getWidth(); x++) {
                     short[] pixel_color = new short[3];
                     try {
                         pixel_color = mandelImageData.getPixel(x, y);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     gui.getPixelWriter().setColor(x,y,Color.rgb(pixel_color[0], pixel_color[1], pixel_color[2]));

                 }


             }
         });
        FractalLogger.logRenderFinished(FractalType.MANDELBROT,mandelImageData);
        FractalLogger.logDrawDone(FractalType.MANDELBROT);
        assert julia != null;
        SimpleImage juliaImageData = julia.getImageData();

        Platform.runLater(() -> {
            for(int y = 0; y < juliaImageData.getHeight(); y++) {
                for (int x = 0; x < juliaImageData.getWidth(); x++) {
                    short[] pixel_color = new short[3];
                    try {
                        pixel_color = juliaImageData.getPixel(x, y);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    gui_julia.getPixelWriter().setColor(x,y,Color.rgb(pixel_color[0], pixel_color[1], pixel_color[2]));

                }
            }});

        FractalLogger.logRenderFinished(FractalType.JULIA,juliaImageData);
        FractalLogger.logDrawDone(FractalType.JULIA);
    }

    @Override
    public void start(Stage primaryStage) {
        AtomicReference<Boolean> scrollInProgress = new AtomicReference<>(false); //used to lock the zoom fields while  scrolling in progress

        //Get Input
        Input inp = new Input(getParameters().getNamed());
        FractalLogger.logArguments(inp.getMandelbrotX(),inp.getMandelbrotY(),inp.getMandelbrotZoom(),inp.getIterations(),inp.getJuliaX(),inp.getJuliaY(),inp.getJuliaZoom(),inp.getColourMode());
        FractalLogger.logDistributionArguments(inp.getRenderMode(),inp.getTasksPerWorker(),inp.getConnection_toLog());
        // -------------- Textfields Parameters
        TextField tf_iteration = new TextField(inp.getIterations().getName());
        TextField tf_juliaX = new TextField(inp.getJuliaX().getName());
        TextField tf_juliaY = new TextField(inp.getJuliaY().getName());
        TextField tf_mandelX = new TextField(inp.getMandelbrotX().getName());
        TextField tf_mandelY = new TextField(inp.getMandelbrotY().getName());
        TextField tf_juliaZoom = new TextField(inp.getJuliaZoom().getName());
        TextField tf_mandelZoom = new TextField(inp.getMandelbrotZoom().getName());
        ColourModes[] c_modes = {ColourModes.BLACK_WHITE, ColourModes.COLOUR_FADE};
        ComboBox<ColourModes> cb_colorMode = new ComboBox<>(FXCollections
                .observableArrayList(c_modes));
        cb_colorMode.getSelectionModel().selectFirst();
        RenderMode[] r_modes = {RenderMode.LOCAL, RenderMode.DISTRIBUTED};
        ComboBox<RenderMode> cb_rendermode = new ComboBox<>(FXCollections
                .observableArrayList(r_modes));
        cb_rendermode.getSelectionModel().selectFirst();
        TextField tf_tasksPerWorker = new TextField(inp.getTasksPerWorker().getName());

        //SERVICE
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        draw(inp);
                        return null;
                    }
                };


            }
        };
        //--------------- Bindings
        Converter conv = new Converter();

        tf_iteration.textProperty().bindBidirectional(inp.getIterations(), conv);
        tf_juliaX.textProperty().bindBidirectional(inp.getJuliaX(), conv);
        tf_juliaY.textProperty().bindBidirectional(inp.getJuliaY(), conv);
        tf_mandelX.textProperty().bindBidirectional(inp.getMandelbrotX(), conv);
        tf_mandelY.textProperty().bindBidirectional(inp.getMandelbrotY(), conv);
        tf_juliaZoom.textProperty().bindBidirectional(inp.getJuliaZoom(), conv);
        tf_mandelZoom.textProperty().bindBidirectional(inp.getMandelbrotZoom(), conv);
        cb_colorMode.valueProperty().bindBidirectional(inp.getColourMode());
        cb_rendermode.valueProperty().bindBidirectional(inp.getRenderMode());
        tf_tasksPerWorker.textProperty().bindBidirectional(inp.getTasksPerWorker(), conv);

        //----------------  DEFAULT VALUES
        inp.getMandelbrotZoom().set(0);


        // ---------------  Canvas
        mainPane = new GridPane();
        leftCanvas = new Canvas();
        leftCanvas.setCursor(Cursor.HAND);
        mainPane.setGridLinesVisible(true);
        mainPane.add(leftCanvas, 0, 0);
        rightCanvas = new Canvas();
        rightCanvas.setCursor(Cursor.HAND);
        mainPane.add(rightCanvas, 1, 0);


        ColumnConstraints cc1 =
                new ColumnConstraints(1, 1, -1, Priority.ALWAYS, HPos.CENTER, true);
        ColumnConstraints cc2 =
                new ColumnConstraints(1, 1, -1, Priority.ALWAYS, HPos.CENTER, true);
        ColumnConstraints cc3 =
                new ColumnConstraints(400, 400, 400, Priority.ALWAYS, HPos.CENTER, true);

        mainPane.getColumnConstraints().addAll(cc1, cc2, cc3);

        RowConstraints rc1 =
                new RowConstraints(400, 400, -1, Priority.ALWAYS, VPos.CENTER, true);

        mainPane.getRowConstraints().addAll(rc1);

        leftHeight.bind(leftCanvas.heightProperty());
        leftWidth.bind(leftCanvas.widthProperty());
        rightHeight.bind(rightCanvas.heightProperty());
        rightWidth.bind(rightCanvas.widthProperty());


        //--------------  CONTROL PANE
        GridPane controlPane = new GridPane();
        controlPane.add(new Label("Iterations:"),0,0);
        controlPane.add(tf_iteration,1,0);
        controlPane.add(new Label("Julia X:"),0,1);
        controlPane.add(tf_juliaX,1,1);
        controlPane.add(new Label("Julia Y:"),0,2);
        controlPane.add(tf_juliaY,1,2);
        controlPane.add(new Label("Julia Zoom:"),0,3);
        controlPane.add(tf_juliaZoom,1,3);
        controlPane.add(new Label("Mandel X:"),0,4);
        controlPane.add(tf_mandelX,1,4);
        controlPane.add(new Label("Mandel Y:"),0,5);
        controlPane.add(tf_mandelY,1,5);
        controlPane.add(new Label("Mandel Zoom:"),0,6);
        controlPane.add(tf_mandelZoom,1,6);
        controlPane.add(new Label("Color Mode:"),0,7);
        controlPane.add(cb_colorMode,1,7);
        controlPane.add(new Label("Render Mode:"),0, 8);
        controlPane.add(cb_rendermode,1,8);
        controlPane.add(new Label("Tasks per Worker"), 0, 9);
        controlPane.add(tf_tasksPerWorker, 1, 9);
        controlPane.add(new Label("Connection Editor"),0,10);
        Button testButton = new Button("Connection Editor");
        controlPane.add(testButton,1,10);
        controlPane.add(new Label("Connected Workers"), 0, 11);
        Label workers = new Label(String.valueOf(inp.getConnection().length));
        controlPane.add(workers, 1, 11);

        ColumnConstraints controlLabelColConstraint =
                new ColumnConstraints(195, 195, 200, Priority.ALWAYS, HPos.CENTER, true);
        ColumnConstraints controlControlColConstraint =
                new ColumnConstraints(195, 195, 195, Priority.ALWAYS, HPos.CENTER, true);
        controlPane.getColumnConstraints().addAll(controlLabelColConstraint, controlControlColConstraint);
        mainPane.add(controlPane, 2, 0);

        Scene scene = new Scene(mainPane);
        primaryStage.setTitle("Fractal Displayer");
        primaryStage.setScene(scene);

        leftCanvas.setOnMouseDragged(mouseEvent -> {
            aspect_ratio = leftCanvas.getHeight() / leftCanvas.getWidth();
            width_drag = Math.pow(2.0, 2.0 - inp.getMandelbrotZoom().getValue());
            height_drag = width_drag * aspect_ratio;
            double xr = mouseEvent.getSceneX() / (leftCanvas.widthProperty().get() - 1);
            double yr = mouseEvent.getSceneY() / (leftCanvas.getHeight() - 1);
            double result = ((xr * width_drag - width_drag / 2.0) * (-1));
            double result_y =  ((yr * height_drag - height_drag / 2.0) * (-1));
            BigDecimal bigDecimal_y = new BigDecimal(result_y).setScale(2, RoundingMode.HALF_DOWN);
            BigDecimal bigDecimal_x = new BigDecimal(result).setScale(2, RoundingMode.HALF_DOWN);
            if (inp.getMandelbrotX().get() != bigDecimal_x.doubleValue()) {
                inp.getMandelbrotX().set(bigDecimal_x.doubleValue());
                System.out.println(bigDecimal_x.doubleValue());
            }
            if (inp.getMandelbrotY().get() != bigDecimal_y.doubleValue()) {
                inp.getMandelbrotY().set(bigDecimal_y.doubleValue());
            }
            FractalLogger.logDrag(result, result_y, FractalType.MANDELBROT);
        });

        rightCanvas.setOnMouseDragged(mouseEvent -> {
            aspect_ratio = rightCanvas.getHeight() / rightCanvas.getWidth();
            width_drag = Math.pow(2.0, 2.0 - inp.getJuliaZoom().getValue());
            height_drag = width_drag * aspect_ratio;
            double xr = mouseEvent.getX() / (rightCanvas.widthProperty().get() - 1);
            double yr = mouseEvent.getSceneY() / (rightCanvas.getHeight() - 1);
            double result = ((xr * width_drag - width_drag / 2.0) * (-1));
            double result_y =  ((yr * height_drag - height_drag / 2.0) * (-1));
            BigDecimal bigDecimal_y = new BigDecimal(result_y).setScale(2, RoundingMode.HALF_DOWN);
            BigDecimal bigDecimal_x = new BigDecimal(result).setScale(2,RoundingMode.HALF_DOWN);
            if (inp.getJuliaX().get() != bigDecimal_x.doubleValue()) {
                inp.getJuliaX().set(bigDecimal_x.doubleValue());
                System.out.println(bigDecimal_x.doubleValue());
            }
            if (inp.getJuliaY().get() != bigDecimal_y.doubleValue()) {
                inp.getJuliaY().set(bigDecimal_y.doubleValue());
            }
            FractalLogger.logDrag(result, result_y, FractalType.JULIA);

        });

        //Quelle: https://stackoverflow.com/questions/44159794/get-single-stage-resize-event-when-user-releases-left-mouse-button
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e ->{
            originalWidth = scene.getWidth(); // record the new scene size
            originalHeight = scene.getHeight();
            serviceCheck(service, 1, null);
            completedProperty.setValue(false);
        }));

        ChangeListener<Number> changeListener= (observable, oldValue, newValue) ->{
            // once the size changed
            if(originalWidth-mainPane.getWidth()>1 || mainPane.getWidth()-originalWidth>1 ||
                    originalHeight-mainPane.getHeight()>1 || mainPane.getHeight()-originalHeight>1){

                completedProperty.set(true); // notify that completion should be considered
                timeline.play(); // and start counting the time
            }};

        mainPane.widthProperty().addListener(changeListener);
        mainPane.heightProperty().addListener(changeListener);

        completedProperty.addListener((observable, notComplete, complete) -> {
            if (complete) {
                mainPane.widthProperty().removeListener(changeListener);
                mainPane.heightProperty().removeListener(changeListener);
            }
            else{
                mainPane.widthProperty().addListener(changeListener);
                mainPane.heightProperty().addListener(changeListener);
            }
        });


        //------------- Listeners
        tf_mandelZoom.textProperty().addListener((observable, oldValue, newValue) -> serviceCheck(service,  0, scrollInProgress));
        tf_mandelX.textProperty().addListener((observable, oldValue, newValue) -> serviceCheck(service,  1, null));

        tf_mandelY.textProperty().addListener((observable, oldValue, newValue) -> serviceCheck(service, 1, null));
        tf_juliaZoom.textProperty().addListener((observable, oldValue, newValue) -> serviceCheck(service, 0, scrollInProgress));
        tf_juliaX.textProperty().addListener((observable, oldValue, newValue) -> serviceCheck(service,  1, null));

        tf_juliaY.textProperty().addListener((observable, oldValue, newValue) -> serviceCheck(service,  1, null));

        tf_iteration.textProperty().addListener((observable, oldValue, newValue) -> serviceCheck(service,  1, null));
        cb_colorMode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> serviceCheck(service,  1, null));

        cb_rendermode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if(cb_rendermode.getSelectionModel().getSelectedItem()  == RenderMode.DISTRIBUTED){
               workers.setText(String.valueOf(inp.getConnection().length));

               try {
                   int i = 0;
                   List<String> connections = new ArrayList<>();
                   for(var e : inp.getConnection())
                   {
                       connections.add(e.get());
                   }
                   int taskTosend = 0;
                   while(i < connections.size()) {
                       String InetAddr = (connections.get(i));
                       InetAddr = InetAddr.substring(0,InetAddr.indexOf(":"));
                       int portToConnect = Integer.parseInt(connections.get(i).substring(connections.get(i).indexOf(':')+1));
                       Socket clientSocket = new Socket(InetAddr, portToConnect);
                       clientSocket.setSoTimeout(10000);
                       FractalLogger.logConnectionOpenedGUI(InetAddr, portToConnect);
                       ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                       out.flush();
                       int counter = 0;
                       //FractalRenderResult mandel = null;
                       //FractalRenderResult julia = null;
                       FractalRenderOptions mandelToSend = new MandelbrotRenderOptions(inp.getMandelbrotX().get(), inp.getMandelbrotY().get(), getLeftWidth().intValue(), getLeftHeight().intValue(), inp.getMandelbrotZoomIncremtor(), inp.getIterations().get(), inp.getColourMode().getValue());
                       FractalRenderOptions juliaToSend = new JuliaRenderOptions(inp.getJuliaX().get(),inp.getJuliaY().get(),getRightWidth().intValue(),getRightHeight().intValue(),inp.getJuliaZoom().get(),inp.getIterations().get(),inp.getMandelbrotX().get(),inp.getMandelbrotY().get(),inp.getColourMode().getValue());

                       while(counter < inp.getTasksPerWorker().get())
                        {
                            {
                                out.writeObject(mandelToSend);
                                FractalLogger.logSendPackageGUI(mandelToSend, taskTosend, inp.getTasksPerWorker().get() * connections.size());
                                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                                Object firstResult = in.readObject();
                                FractalLogger.logReceivePackageGUI((MandelbrotRenderOptions)firstResult);
                                //mandel = (FractalRenderResult) firstResult;
                                taskTosend++;
                                counter++;
                            }
                            out.writeObject(juliaToSend);
                            FractalLogger.logSendPackageGUI(juliaToSend, taskTosend, inp.getTasksPerWorker().get() * connections.size());
                            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                            Object firstResult = in.readObject();
                            FractalLogger.logReceivePackageGUI((MandelbrotRenderOptions)firstResult);
                            //julia = (FractalRenderResult) firstResult;
                            taskTosend++;
                            counter++;

                            taskTosend++;
                            counter++;
                        }
                       i++;
                   }
               } catch(IOException | ClassNotFoundException e)
               {
                   e.printStackTrace();

               }
           }
        });

        //canvas listnerers
        ChangeListener<Number> rightCanvasSizeListener = (observable, oldValue, newValue) -> {
            System.out.println("RIGHT CANVAS ---- Height: " + rightCanvas.getHeight() + " Width: " + rightCanvas.getWidth());
            serviceCheck(service, 0, scrollInProgress);
        };
        ChangeListener<Number> leftCanvasSizeListener = (observable, oldValue, newValue) ->
        {
            System.out.println("LEFT CANVAS ---- Height: " + leftCanvas.getHeight() + " Width: " + leftCanvas.getWidth());
            serviceCheck(service, 0, scrollInProgress);
        };

        leftCanvas.widthProperty().addListener(leftCanvasSizeListener);
        leftCanvas.heightProperty().addListener(leftCanvasSizeListener);
        rightCanvas.widthProperty().addListener(rightCanvasSizeListener);
        rightCanvas.heightProperty().addListener(rightCanvasSizeListener);

        // If you want to Zoom with mousewheel you have to click into the MandelbrotZoom Textfield and then use the Mousewheel
        rightCanvas.setOnScroll(event ->  {
            scrollInProgress.set(true);
            if(event.getDeltaY() <  0){
                if(inp.getJuliaSetZoomIncremtor() <= 0){
                    scrollInProgress.set(false);
                    return;
                }
                inp.getJuliaZoom().set(inp.getJuliaZoom().get()-0.02);
                FractalLogger.logZoom(inp.getJuliaZoom().get(),FractalType.JULIA);
                serviceCheck(service,  1, null);
            }
            if(event.getDeltaY() > 0){
                inp.JuliaIncrementZoom(0.02);
                inp.getJuliaZoom().set(inp.getJuliaZoom().get()+0.02);
                FractalLogger.logZoom(inp.getJuliaZoom().get(),FractalType.JULIA);
                serviceCheck(service,  1, null);
            }
            scrollInProgress.set(false);
        });

        leftCanvas.setOnScroll(event ->  {
            scrollInProgress.set(true);
            if(event.getDeltaY() <  0){
                if(inp.getMandelbrotZoomIncremtor() <= 0){
                    scrollInProgress.set(false);
                    return;
                }
                inp.getMandelbrotZoom().set(inp.getMandelbrotZoom().get()-0.02);
                FractalLogger.logZoom(inp.getMandelbrotZoom().get(),FractalType.MANDELBROT);
                serviceCheck(service,  1, null);
            }
            if(event.getDeltaY() > 0){
                inp.MandelIncrementZoom(0.02);
                inp.getMandelbrotZoom().set(inp.getMandelbrotZoom().get()+0.02);
                FractalLogger.logZoom(inp.getMandelbrotZoom().get(),FractalType.MANDELBROT);
                serviceCheck(service,  1, null);
            }
            scrollInProgress.set(false);


        });
        testButton.setOnAction(p->{

            Label secLabel = new Label("Worker Connections:");
            secLabel.setAlignment(Pos.TOP_LEFT);

            ListView<String> list = new ListView<>();

            StringProperty[] connect = inp.getConnection();
            ArrayList<String> con = new ArrayList<>();
            for(StringProperty item : connect)
            {
                con.add(item.getValue());
            }
            final ObservableList<String>[] items = new ObservableList[]{FXCollections.observableArrayList(
                    con)};
            list.setItems(items[0]);
            Button addButton = new Button("+");
            addButton.setOnAction(event->{

                VBox vBox = new VBox();
                StackPane thirdPane = new StackPane();
                Label thirdLabel = new Label("Add Connection Worker:");
                TextField tf_add = new TextField();
                Button save_add = new Button();
                save_add.setText("OK");
                thirdPane.getChildren().add(tf_add);

                vBox.getChildren().add(thirdLabel);
                vBox.getChildren().add(thirdPane);
                vBox.getChildren().add(save_add);
                Scene thirdScene = new Scene(vBox, 150, 100);
                Stage thirdWindow = new Stage();
                thirdWindow.setTitle("Add");
                thirdWindow.setScene(thirdScene);
                thirdWindow.show();
                save_add.setOnMouseClicked(mouseEvent -> {
                    String textFieldValue = tf_add.getText();
                    con.add(textFieldValue);
                    items[0] =FXCollections.observableArrayList (
                            con);
                    list.setItems(items[0]);
                    thirdWindow.close();
                });
            });
            Button minusButton = new Button("-");
            minusButton.setOnAction(event->{

                VBox vBox = new VBox();
                StackPane thirdPane = new StackPane();
                Label thirdLabel = new Label("Remove Connection Worker:");

                Button ok_button = new Button();
                Button cancel_button = new Button();
                ok_button.setText("OK");
                cancel_button.setText("Cancel");
                ComboBox<String> tf_minus = new ComboBox<>(FXCollections.observableArrayList(items[0]));
                tf_minus.getSelectionModel().selectFirst();
                thirdPane.getChildren().add(tf_minus);
                vBox.getChildren().add(thirdLabel);
                vBox.getChildren().add(thirdPane);
                vBox.getChildren().add(ok_button);
                vBox.getChildren().add(cancel_button);
                Scene thirdScene = new Scene(vBox, 160, 100);
                Stage thirdWindow = new Stage();
                thirdWindow.setTitle("Remove");
                thirdWindow.setScene(thirdScene);
                thirdWindow.show();

                ok_button.setOnMouseClicked(mouseEvent -> {
                    String selected = tf_minus.getSelectionModel().getSelectedItem();
                    con.remove(selected);
                    items[0] =FXCollections.observableArrayList (con);
                    list.setItems(items[0]);
                    thirdWindow.close();

                });
                cancel_button.setOnMouseClicked(mouseEvent -> thirdWindow.close());
            });
            Button checkButton = new Button("Check connections");
            checkButton.setOnAction(event->{
                int checkConnected = con.size();
                for (String connection : con) {
                    if (!connection.contains(":")) {
                        checkConnected--;
                        continue;
                    }
                    String InedAddres = connection.substring(0, connection.indexOf(":"));
                    int port;
                    try {
                        port = Integer.parseInt(connection.substring(connection.indexOf(":") + 1));
                    } catch (NumberFormatException e) {
                        checkConnected--;
                        continue;
                    }
                    try {

                        Socket clientSocket = new Socket(InedAddres, port);
                        clientSocket.setSoTimeout(10000);

                    } catch (IOException e) {
                        checkConnected--;
                    }

                }
                StackPane thirdPane = new StackPane();
                Label thirdLabel = new Label(String.format("%d out of %d Connections work.",checkConnected,con.size()));
                thirdPane.getChildren().add(thirdLabel);
                Scene thridScene = new Scene(thirdPane, 160, 50);
                Stage thirdWindow = new Stage();
                thirdWindow.setTitle("Check");
                thirdWindow.setScene(thridScene);
                thirdWindow.show();
            });

            Button cancelButton = new Button("Cancel");
            Button saveButton = new Button("Save");

            Button changeButton = new Button("Change");
            changeButton.setOnAction(event->{

                VBox vBox = new VBox();
                StackPane thirdPane = new StackPane();
                StackPane fourthPane = new StackPane();
                Label thirdLabel = new Label("Which Connection Worker to Change:");
                Label fourthLabel = new Label("Change Connection Worker to:");
                Button ok_button = new Button();
                Button cancel_button = new Button();
                ok_button.setText("OK");
                cancel_button.setText("Cancel");
                ComboBox<String> tf_minus = new ComboBox<>(FXCollections.observableArrayList(items[0]));
                tf_minus.getSelectionModel().selectFirst();

                TextField tf_change_v2 = new TextField();
                fourthPane.getChildren().add(tf_change_v2);
                tf_change_v2.setText(tf_minus.getSelectionModel().getSelectedItem());
                vBox.getChildren().add(thirdLabel);
                vBox.getChildren().add(tf_minus);
                vBox.getChildren().add(thirdPane);
                vBox.getChildren().add(fourthLabel);
                vBox.getChildren().add(fourthPane);
                vBox.getChildren().add(ok_button);
                vBox.getChildren().add(cancel_button);
                Scene thirdScene = new Scene(vBox, 220, 150);
                Stage thirdWindow = new Stage();
                thirdWindow.setTitle("Change");
                thirdWindow.setScene(thirdScene);
                thirdWindow.show();

                ok_button.setOnMouseClicked(mouseEvent -> {
                    String selected = tf_minus.getSelectionModel().getSelectedItem();
                    con.set(con.indexOf(selected),tf_change_v2.getText());
                    items[0] =FXCollections.observableArrayList (con);
                    list.setItems(items[0]);
                    thirdWindow.close();

                });
                cancel_button.setOnMouseClicked(mouseEvent -> thirdWindow.close());
                tf_minus.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> tf_change_v2.setText(tf_minus.getSelectionModel().getSelectedItem()));
            });

            HBox firstHBox = new HBox();
            HBox secondHBox = new HBox();
            HBox thirdHBox = new HBox();
            firstHBox.getChildren().addAll(addButton, checkButton);
            secondHBox.getChildren().addAll(cancelButton, saveButton);
            thirdHBox.getChildren().addAll(minusButton, changeButton);

            VBox secPane = new VBox();
            secPane.getChildren().add(secLabel);
            secPane.getChildren().add(list);
            secPane.getChildren().add(firstHBox);
            secPane.getChildren().add(secondHBox);
            secPane.getChildren().add(thirdHBox);

            Scene secScene = new Scene(secPane, 250, 500);
            Stage secWindow = new Stage();
            secWindow.setTitle("Connection Editor");
            secWindow.setScene(secScene);
            secWindow.show();

            cancelButton.setOnAction(event -> secWindow.close());
            saveButton.setOnAction(event -> {
                int checkConnected = con.size();
                for (String connection : con) {
                    if (!connection.contains(":")) {
                        checkConnected--;
                        continue;
                    }
                    String InedAddres = connection.substring(0, connection.indexOf(":"));
                    int port;
                    try {
                        port = Integer.parseInt(connection.substring(connection.indexOf(":") + 1));
                    } catch (NumberFormatException e) {
                        checkConnected--;
                        continue;
                    }
                    try {

                        Socket clientSocket = new Socket(InedAddres, port);
                        clientSocket.setSoTimeout(10000);

                    } catch (IOException e) {
                        checkConnected--;
                    }

                }
                StringProperty[] connect_toAddBacktoInput = new SimpleStringProperty[con.size()];
                int i = 0;
                for (String con_contains: con
                ) {

                    connect_toAddBacktoInput[i++] =  new SimpleStringProperty(con_contains);
                }
                inp.setConnection(connect_toAddBacktoInput);
                workers.setText(String.valueOf(checkConnected));
                secWindow.close();
            });
        });

        primaryStage.setWidth(originalWidth);
        primaryStage.setHeight(originalHeight);
        primaryStage.show();
        service.restart();
        FractalLogger.logGUIInitialized(mainPane,primaryStage,leftCanvas,rightCanvas);
    }
}
