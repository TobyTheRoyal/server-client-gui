package at.tugraz.oop2.worker;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;
import lombok.Setter;

public class Input {
    @Getter
    @Setter
    private IntegerProperty port;
    public Input(String[] args) {
    if(args.length == 1)
    {
        port = new SimpleIntegerProperty(Integer.parseInt(args[0].substring(args[0].indexOf("=")+1)));
    }
        if(getPort() == null)
        {
            port = new SimpleIntegerProperty(8010);
        }
    }
}
