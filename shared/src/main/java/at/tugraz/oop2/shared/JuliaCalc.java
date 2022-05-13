package at.tugraz.oop2.shared;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.scene.canvas.Canvas;
import java.io.IOException;



public class JuliaCalc {

    public static FractalRenderResult JuliaCalculation(Canvas JuliaCanvas, IntegerProperty IterationsMax,
                                                       DoubleProperty JuliaX, DoubleProperty JuliaY, DoubleProperty JuliaZoom,
                                                       DoubleProperty MandelBrotX, DoubleProperty MandelBrotY,
                                                       Property<ColourModes> ColorMode) throws IOException, InterruptedException
    {
        JuliaRenderOptions JuliaOptions = new JuliaRenderOptions(JuliaX.getValue(),
                JuliaY.getValue(), (int)JuliaCanvas.getWidth(), (int)JuliaCanvas.getHeight(),
                JuliaZoom.getValue(),IterationsMax.getValue(), MandelBrotX.getValue(), MandelBrotY.getValue(), ColorMode.getValue());

        FractalLogger.logRenderCall(JuliaOptions);
        int maxIterations = IterationsMax.getValue();
        double canvas_width =  JuliaCanvas.getWidth();
        double canvas_height =  JuliaCanvas.getHeight();
        double aspect_ratio = canvas_height / canvas_width;
        JuliaOptions.setZoom(4.0);


        System.out.println("Canvas width: " + JuliaCanvas.getWidth() + " -------- Canvas height: " + JuliaCanvas.getHeight());


        SimpleImage image = new SimpleImage((int)canvas_width, (int)canvas_height);


        short[] black = {0,0,0}, white = {255,255,255}, blue = {0,0,255}, red = {255,0,0};

        for(double x = 0; x < canvas_width; x++) {
            for (double y = 0; y < canvas_height; y++){

                double width = Math.pow(2.0, 2.0 - JuliaZoom.getValue());
                double height = width * aspect_ratio;
                double xr = x / (canvas_width -1);
                double yr = y / (canvas_height-1);
                double cx = JuliaOptions.getConstantX();
                double cy = JuliaOptions.getConstantY();
                double zx = xr * width + JuliaX.getValue() - width/2.0;
                double zy = yr * height + JuliaY.getValue() - height/2.0;

                int iteration = 0;
                while( iteration < maxIterations && zx * zx + zy * zy <= 2){

                    double temp = zx * zx - zy * zy + cx;
                    zy = 2 * zx * zy + cy;
                    zx = temp;
                    iteration++;
                }
                double t = (double)iteration / maxIterations;


                short[] color_pixel = {0,0,0};
                color_pixel[0] = (short) (red[0] * t + blue[0]*(1-t));
                color_pixel[1] = (short) (red[1] * t + blue[1]*(1-t));
                color_pixel[2] = (short) (red[2] * t + blue[2]*(1-t));

                if( iteration >= maxIterations - 1) {
                    try {
                        image.setPixel((int)x, (int)y, black);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        if(JuliaOptions.mode == ColourModes.BLACK_WHITE)
                        {
                            image.setPixel((int)x, (int) y, white);
                        }
                        else {
                            image.setPixel((int) x, (int) y, color_pixel);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        FractalRenderResult Julia = new FractalRenderResult(JuliaOptions, 0, image);
        System.out.println("Done with Image Julia");
        return Julia;
    }
    public static FractalRenderResult JuliaCalculation(JuliaRenderOptions re) throws IOException, InterruptedException
    {
        FractalLogger.logRenderCall(re);
        int maxIterations = re.iterations;
        double canvas_width =  re.width;
        double canvas_height =  re.height;
        double aspect_ratio = canvas_height / canvas_width;
        re.setZoom(4.0);


        System.out.println("Canvas width: " + re.getWidth()+ " -------- Canvas height: " + re.getHeight());


        SimpleImage image = new SimpleImage((int)canvas_width, (int)canvas_height);


        short[] black = {0,0,0}, white = {255,255,255}, blue = {0,0,255}, red = {255,0,0};

        for(double x = 0; x < canvas_width; x++) {
            for (double y = 0; y < canvas_height; y++){

                double width = Math.pow(2.0, 2.0 - re.getZoom());
                double height = width * aspect_ratio;
                double xr = x / (canvas_width -1);
                double yr = y / (canvas_height-1);
                double cx = re.getConstantX();
                double cy = re.getConstantY();
                double zx = xr * width + re.getCenterX() - width/2.0;
                double zy = yr * height + re.getCenterY() - height/2.0;

                int iteration = 0;
                while( iteration < maxIterations && zx * zx + zy * zy <= 2){

                    double temp = zx * zx - zy * zy + cx;
                    zy = 2 * zx * zy + cy;
                    zx = temp;
                    iteration++;
                }
                double t = (double)iteration / maxIterations;


                short[] color_pixel = {0,0,0};
                color_pixel[0] = (short) (red[0] * t + blue[0]*(1-t));
                color_pixel[1] = (short) (red[1] * t + blue[1]*(1-t));
                color_pixel[2] = (short) (red[2] * t + blue[2]*(1-t));

                if( iteration >= maxIterations - 1) {
                    try {
                        image.setPixel((int)x, (int)y, black);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        if(re.mode == ColourModes.BLACK_WHITE)
                        {
                            image.setPixel((int)x, (int) y, white);
                        }
                        else {
                            image.setPixel((int) x, (int) y, color_pixel);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return new FractalRenderResult(re, 0, image);
    }

}
