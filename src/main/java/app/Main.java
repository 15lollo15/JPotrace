package app;

import com.formdev.flatlaf.FlatLightLaf;
import gui.Controller;

public class Main {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        Controller controller = Controller.getInstance();
        controller.showWindow();
    }

}
