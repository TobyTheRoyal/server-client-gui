package at.tugraz.oop2.shared;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MandelbrotRenderOptions extends FractalRenderOptions {


    public MandelbrotRenderOptions(double centerX, double centerY, int width, int height, double zoom, int iterations,
                                   ColourModes mode) {
        super(centerX, centerY, width, height, zoom, iterations, FractalType.MANDELBROT, mode);
    }



}
