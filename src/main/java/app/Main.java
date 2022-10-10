package app;

import com.formdev.flatlaf.FlatLightLaf;
import gui.Controller;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        FlatLightLaf.setup();
        Controller controller = Controller.getInstance();
        controller.showWindow();
    }

}
