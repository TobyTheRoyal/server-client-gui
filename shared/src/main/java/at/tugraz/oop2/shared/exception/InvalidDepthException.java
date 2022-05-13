package at.tugraz.oop2.shared.exception;

public class InvalidDepthException extends Exception {
    public InvalidDepthException(){
        super("Depth must equal colour depth!");
    }
}
