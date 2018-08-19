package de.anura.eisenbahn;

import com.sun.net.httpserver.HttpServer;
import de.anura.eisenbahn.devices.Device;
import de.anura.eisenbahn.handlers.LevelHandler;
import de.anura.eisenbahn.handlers.StateHandler;
import de.anura.eisenbahn.handlers.ToggleHandler;
import de.anura.eisenbahn.twitch.TwitchHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EisenbahnBackend {

    public static void main(String[] args) {
        try {
            ConfigReader.init();
            Device.init();
            createHttpServer();
        } catch (IOException ex) {
            Logger.getLogger(EisenbahnBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void createHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        server.setExecutor(pool);
        server.createContext("/state", new StateHandler());
        server.createContext("/toggle", new ToggleHandler());
        server.createContext("/level", new LevelHandler());
        server.createContext("/twitch", (he) -> {
            TwitchHandler.connect();

            String response = "Done";
            he.sendResponseHeaders(200, response.length());

            try (OutputStream os = he.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        server.createContext("/stop", (he) -> {
            String response = "Stopping";
            he.sendResponseHeaders(200, response.length());

            try (OutputStream os = he.getResponseBody()) {
                os.write(response.getBytes());
            }

            TwitchHandler.disconnect();

            server.stop(2);
            pool.shutdown();
            try {
                if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException ex) {
                pool.shutdownNow();
            }
        });
        server.start();
    }
}
