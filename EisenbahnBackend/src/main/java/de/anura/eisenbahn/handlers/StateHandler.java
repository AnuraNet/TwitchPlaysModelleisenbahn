package de.anura.eisenbahn.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.anura.eisenbahn.devices.Device;
import java.io.IOException;
import java.io.OutputStream;

public class StateHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        he.getResponseHeaders().add("Content-type", "text/plain");
        String response = "";
        for (Device dev : Device.values()) {
            Device.DataType type = dev.getType();
            Object payload = "";
            switch (type) {
                case BOOLEAN:
                    payload = dev.isOn();
                    break;
                case INT:
                    payload = dev.getLevel();
                    break;
            }
            response += dev.name() + ":" + type.ordinal() + ":" + payload + "\n";
        }
        he.sendResponseHeaders(200, response.length());

        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
