package at.tugraz.oop2.shared;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FractalRenderResult {
    FractalRenderOptions options;
    float time;
    SimpleImage imageData;
}
