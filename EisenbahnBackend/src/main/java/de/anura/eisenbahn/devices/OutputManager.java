package de.anura.eisenbahn.devices;

import de.anura.eisenbahn.ConfigReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputManager {

    public static boolean switchPin(int pin, boolean on) {
        return execute(String.format(ConfigReader.getScriptFolder() + "/gpio/eisenbahn %d switch %s", pin, on ? "on" : "off"));
    }

    public static boolean pulsePin(int pin) {
        return execute(String.format(ConfigReader.getScriptFolder() + "/gpio/eisenbahn %d pulse", pin));
    }

    private static boolean execute(String action) {
        try {
            Process p = Runtime.getRuntime().exec(action);
            while (p.isAlive()) {
                Thread.sleep(50);
            }
            if (p.exitValue() != 0) {
                return false;
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(OutputManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public static boolean setLevel(char output, int level) {
        return execute(String.format(ConfigReader.getScriptFolder() + "/remote.py %c %d", output, level));
    }

    public static void initPin(int pin) {
        execute(String.format(ConfigReader.getScriptFolder() + "/gpio/eisenbahn %d init", pin));
    }
}
