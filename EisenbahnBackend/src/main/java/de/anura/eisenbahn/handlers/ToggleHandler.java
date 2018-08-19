package de.anura.eisenbahn.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.anura.eisenbahn.devices.Device;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class ToggleHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(he.getRequestBody(), Charset.forName("UTF-8")));
        String response;
        String de = reader.readLine();
        if (de == null) {
            response = "no_device";
        } else {
            try {
                Device dev = Device.valueOf(de);
                if (dev.getType() == Device.DataType.INT) response = "use_level";
                else if (dev.toggle()) {
                    response = "ok\n" + dev.isOn();
                } else {
                    response = "failed";
                }
            } catch (IllegalArgumentException ex) {
                response = "device_not_found";
            }
        }

        he.sendResponseHeaders(200, response.length());

        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
