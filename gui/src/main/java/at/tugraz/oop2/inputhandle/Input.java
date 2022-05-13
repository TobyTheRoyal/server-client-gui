package at.tugraz.oop2.inputhandle;

import at.tugraz.oop2.shared.ColourModes;
import at.tugraz.oop2.shared.RenderMode;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class Input{
    @Getter @Setter
    private StringProperty[] connection;
    @Getter @Setter
    private double MandelbrotZoomIncremtor = 0.0;
    @Getter @Setter
    private StringProperty connection_toLog;
    @Getter @Setter
    private double JuliaSetZoomIncremtor = 0.0;
    @Getter @Setter
    private IntegerProperty iterations;
    @Getter @Setter
    private IntegerProperty tasksPerWorker;
    @Getter @Setter
    private DoubleProperty MandelbrotX;
    @Getter @Setter
    private DoubleProperty MandelbrotY;
    @Getter @Setter
    private DoubleProperty MandelbrotZoom;
    @Getter @Setter
    private DoubleProperty JuliaX;
    @Getter @Setter
    private DoubleProperty JuliaY;
    @Getter @Setter
    private DoubleProperty JuliaZoom;
     @Getter @Setter
    private Property<ColourModes> colourMode = new Property<>() {
                 ColourModes cm = ColourModes.BLACK_WHITE;
                 @Override
                 public void bind(ObservableValue<? extends ColourModes> observableValue) {

                 }

                 @Override
                 public void unbind() {

                 }

                 @Override
                 public boolean isBound() {
                     return false;
                 }

                 @Override
                 public void bindBidirectional(Property<ColourModes> property) {
                     Bindings.bindBidirectional(this, property);
                 }

                 @Override
                 public void unbindBidirectional(Property<ColourModes> property) {

                 }


                 @Override
                 public Object getBean() {
                     return null;
                 }

                 @Override
                 public String getName() {
                     return null;
                 }

                 @Override
                 public void addListener(ChangeListener<? super ColourModes> changeListener) {

                 }

                 @Override
                 public void removeListener(ChangeListener<? super ColourModes> changeListener) {

                 }

                 @Override
                 public ColourModes getValue() {
                     return cm;
                 }

                 @Override
                 public void addListener(InvalidationListener invalidationListener) {

                 }

                 @Override
                 public void removeListener(InvalidationListener invalidationListener) {

                 }

                 @Override
                 public void setValue(ColourModes colourModes) {
                     this.cm = colourModes;
                 }
             };
        @Getter @Setter
    private Property<RenderMode> renderMode =new Property<>() {
            RenderMode cm = RenderMode.LOCAL;
                 @Override
                 public void bind(ObservableValue<? extends RenderMode> observableValue) {

                 }

                 @Override
                 public void unbind() {

                 }

                 @Override
                 public boolean isBound() {
                     return false;
                 }

                 @Override
                 public void bindBidirectional(Property<RenderMode> property) {
                     Bindings.bindBidirectional(this, property);
                 }

                 @Override
                 public void unbindBidirectional(Property<RenderMode> property) {

                 }


                 @Override
                 public Object getBean() {
                     return null;
                 }

                 @Override
                 public String getName() {
                     return null;
                 }

                 @Override
                 public void addListener(ChangeListener<? super RenderMode> changeListener) {

                 }

                 @Override
                 public void removeListener(ChangeListener<? super RenderMode> changeListener) {

                 }

                 @Override
                 public RenderMode getValue() {
                     return cm;
                 }

                 @Override
                 public void addListener(InvalidationListener invalidationListener) {

                 }

                 @Override
                 public void removeListener(InvalidationListener invalidationListener) {

                 }

                 @Override
                 public void setValue(RenderMode colourModes) {
                     this.cm = colourModes;
                 }
             };



     public void MandelIncrementZoom(double increment){
         MandelbrotZoomIncremtor = MandelbrotZoomIncremtor + increment;
     }

    public void JuliaIncrementZoom(double increment){
        JuliaSetZoomIncremtor = JuliaSetZoomIncremtor + increment;
    }

    public Input(Map<String, String> args)
    {

        for(String nameOfKey : args.keySet())
        {
            switch (nameOfKey)
            {
                case "iterations":
                    iterations = new SimpleIntegerProperty(Integer.parseInt(args.get("iterations")));
                    break;
                case "MandelbrotX":
                    MandelbrotX = new SimpleDoubleProperty(Float.parseFloat(args.get("MandelbrotX")));
                    break;
                case "MandelbrotY":
                    MandelbrotY= new SimpleDoubleProperty(Float.parseFloat(args.get("MandelbrotY")));
                    break;
                case "MandelbrotZoom":
                    MandelbrotZoom = new SimpleDoubleProperty (Float.parseFloat(args.get("MandelbrotZoom")));
                    break;
                case "JuliaX":
                    JuliaX = new SimpleDoubleProperty (Float.parseFloat(args.get("JuliaX")));
                    break;
                case "JuliaY":
                    JuliaY = new SimpleDoubleProperty (Float.parseFloat(args.get("JuliaY")));
                    break;
                case "JuliaZoom":
                    JuliaZoom = new SimpleDoubleProperty (Float.parseFloat(args.get("JuliaZoom")));
                    break;
                case "colourMode":
                    colourMode.setValue(ColourModes.valueOf(args.get("colourMode")));
                    break;
                case "tasksPerWorker":
                    tasksPerWorker = new SimpleIntegerProperty(Integer.parseInt(args.get("tasksPerWorker")));
                    break;
                case "renderMode":
                    renderMode.setValue(RenderMode.valueOf((args.get("renderMode"))));
                    break;
                case "connection":
                    String connectionSplit = (args.get("connection"));
                    connection_toLog = new SimpleStringProperty(args.get("connection"));
                    if(connectionSplit.contains(","))
                    {
                        int count = (int) connectionSplit.chars().filter(a -> a == ',').count();
                        connection = new SimpleStringProperty[count+1];
                        int i = 0;
                        while(i <=count)
                        {

                            String actualConnection = connectionSplit.substring(0,connectionSplit.indexOf(',') == -1?connectionSplit.length():connectionSplit.indexOf(','));
                            connection[i] = new SimpleStringProperty(actualConnection);
                            i++;
                            connectionSplit = connectionSplit.substring(0,connectionSplit.indexOf(',') == -1?connectionSplit.length():connectionSplit.indexOf(','));
                        }
                    }else
                    {
                        connection = null;
                    }

                    break;
                    default:
                    break;
            }
        }
        if(getMandelbrotX() == null)
        {
            MandelbrotX = new SimpleDoubleProperty(0.0f);

        }
        if(getJuliaX() == null)
        {
            JuliaX = new SimpleDoubleProperty(0.0f);
        }
        if(getJuliaY() == null)
        {
            JuliaY = new SimpleDoubleProperty(0.0f);
        }
        if(getMandelbrotY() == null)
        {
            MandelbrotY = new SimpleDoubleProperty(0.0f);

        }
        if(getMandelbrotZoom() == null)
        {
            MandelbrotZoom = new SimpleDoubleProperty(0.0f);

        }
        if(getJuliaZoom() == null)
        {
            JuliaZoom = new SimpleDoubleProperty(0.0f);

        }
    if(getIterations() == null)
    {
        iterations = new SimpleIntegerProperty(128);

    }
    if(getColourMode() == null)
    {
        colourMode.setValue(ColourModes.BLACK_WHITE);

    }
        if(getTasksPerWorker() == null)
        {
            tasksPerWorker = new SimpleIntegerProperty(5);

        }
        if(getRenderMode() == null)
        {
            renderMode.setValue(RenderMode.LOCAL);

        }
        if(getConnection() == null)
        {
            connection = new SimpleStringProperty[1];
            connection[0] = new SimpleStringProperty("localhost:8010");
            connection_toLog = new SimpleStringProperty("localhost:8010");
        }



    }


}
