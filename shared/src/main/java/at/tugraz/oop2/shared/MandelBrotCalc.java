package at.tugraz.oop2.shared;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.scene.canvas.Canvas;
import java.io.IOException;


public class MandelBrotCalc {

    public static FractalRenderResult MandelbrotCalculation(Canvas MandelCanvas, IntegerProperty IterationsMax, DoubleProperty MandelbrotX, DoubleProperty MandelbrotY, DoubleProperty MandelbrotZoom, Property<ColourModes> ColorMode) throws IOException, InterruptedException {
        MandelbrotRenderOptions mandelOptions = new MandelbrotRenderOptions(MandelbrotX.getValue(),
                MandelbrotY.getValue(), (int)MandelCanvas.getWidth(), (int)MandelCanvas.getHeight(),
                MandelbrotZoom.getValue(),IterationsMax.getValue(), ColorMode.getValue());
        FractalLogger.logRenderCall(mandelOptions);
        int maxIterations = IterationsMax.getValue();
        double canvas_width =  MandelCanvas.getWidth();
        double canvas_height =  MandelCanvas.getHeight();
        double aspect_ratio = canvas_height / canvas_width;
        mandelOptions.setZoom(4.0);
        SimpleImage image = new SimpleImage((int)canvas_width, (int)canvas_height);


        short[] black = {0,0,0}, white = {255,255,255}, blue = {0,0,255}, red = {255,0,0};

        for(double x = 0; x < canvas_width; x++) {
            for (double y = 0; y < canvas_height; y++){

                double width = Math.pow(2.0, 2.0 - MandelbrotZoom.getValue());
                double height = width * aspect_ratio;
                double xr = x / (canvas_width -1);
                double yr = y / (canvas_height-1);
                double cx = xr * width + MandelbrotX.getValue() - width/2.0;
                double cy = yr * height + MandelbrotY.getValue() - height/2.0;
                double zx = 0;
                double zy = 0;

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
                        if(mandelOptions.mode == ColourModes.BLACK_WHITE)
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
        return new FractalRenderResult(mandelOptions, 0, image);
    }

    public static FractalRenderResult MandelbrotCalculation(MandelbrotRenderOptions mandleRender) {
        FractalLogger.logRenderCall(mandleRender);
        int maxIterations = mandleRender.getIterations();
        double canvas_width =  mandleRender.getWidth();
        double canvas_height =  mandleRender.getHeight();
        double aspect_ratio = canvas_height / canvas_width;
        mandleRender.setZoom(4.0);
        SimpleImage image = new SimpleImage((int)canvas_width, (int)canvas_height);


        short[] black = {0,0,0}, white = {255,255,255}, blue = {0,0,255}, red = {255,0,0};

        for(double x = 0; x < canvas_width; x++) {
            for (double y = 0; y < canvas_height; y++){

                double width = Math.pow(2.0, 2.0 - mandleRender.getZoom());
                double height = width * aspect_ratio;
                double xr = x / (canvas_width -1);
                double yr = y / (canvas_height-1);
                double cx = xr * width + mandleRender.getCenterX() - width/2.0;
                double cy = yr * height + mandleRender.getCenterY() - height/2.0;
                double zx = 0;
                double zy = 0;

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
                        if(mandleRender.mode == ColourModes.BLACK_WHITE)
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
        return new FractalRenderResult(mandleRender, 0, image);
    }
}

