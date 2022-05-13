package at.tugraz.oop2.shared;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.canvas.Canvas;
import lombok.Getter;
import lombok.Setter;

public interface FractalAlgorithm<T extends FractalRenderOptions> {
    FractalRenderResult render(T options);
}
