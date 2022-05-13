package at.tugraz.oop2.shared;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JuliaRenderOptions extends FractalRenderOptions {
    private double constantX;
    private double constantY;

    public JuliaRenderOptions(double centerX, double centerY, int width, int height, double zoom, int iterations, double constantX, double constantY, ColourModes mode) {
        super(centerX, centerY, width, height, zoom, iterations, FractalType.JULIA, mode);
        this.constantX = constantX;
        this.constantY = constantY;
    }


}
