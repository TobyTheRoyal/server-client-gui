package at.tugraz.oop2.shared;

import java.io.Serializable;
import at.tugraz.oop2.shared.exception.InvalidDepthException;
import lombok.Getter;

public class SimpleImage implements Serializable {
    @Getter
    private short[] data;
    @Getter
    private int depth;
    @Getter
    private int width;
    @Getter
    private int height;

    public SimpleImage(int width, int height) {
        this(3, width, height);
    }

    public SimpleImage(int depth, int width, int height) {
        this.depth = depth;
        this.width = width;
        this.height = height;
        this.data = new short[width * height * depth];
    }

    public byte[] getByteData() {
        byte[] arr = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            arr[i] = (byte) data[i];
        }
        return arr;
    }

    public void setPixel(int x, int y, short[] data) throws InvalidDepthException {
        if (data.length != depth) {
            throw new InvalidDepthException();
        }

        for (int i = 0; i < depth; i++) {
            try {
                this.data[width * y * depth + x * depth + i] = data[i];}
            catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
            }
        }

    public short[] getPixel(int x, int y) {
        short[] pixel = new short[depth];
        for (int i = 0; i < depth; i++) {
            pixel[i] = this.data[width * y * depth + x * depth + i];
        }
        return pixel;
    }
}
