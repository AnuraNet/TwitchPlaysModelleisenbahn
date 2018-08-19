package de.anura.eisenbahn.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.anura.eisenbahn.devices.Device;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;

public class LevelHandler implements HttpHandler {

    private final ReentrantLock lock = new ReentrantLock();

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
                int level = Integer.parseInt(reader.readLine());
                if (dev.getType() == Device.DataType.BOOLEAN) response = "use_toggle";
                else {
                    lock.lock();
                    if (dev.setLevel(level)) {
                        response = "ok\n" + dev.getLevel();
                    } else {
                        response = "failed";
                    }
                }
            } catch (NumberFormatException ex) {
                response = "wrong_level";
            } catch (IllegalArgumentException ex) {
                response = "device_not_found";
            } finally {
                lock.unlock();
            }
        }

        he.sendResponseHeaders(200, response.length());

        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
