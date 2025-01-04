package org.apache.storm.metric.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.storm.metric.micrometer.persister.StormMetricsPersister;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

public abstract class AbstractMetricsServerCollector implements MetricsServerCollector {
    private HttpServer server;

    @Override
    public void prepare(Map<String, Object> daemonConf) {
        // Initialize configurations if needed
    }

    @Override
    public void prepare(StormMetricsPersister stormMetricsPersister, Map<String, Object> daemonConf) {
        // Initialize configurations if needed
        this.prepare(daemonConf);
    }


    @Override
    public void start() {
        //TODO: Determine port and start server.
        startServer(2121);
    }

    @Override
    public void stop() {
        stopServer();
    }

    @Override
    public void startServer(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/metrics", new MetricsHandler());
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start the metrics server", e);
        }
    }

    @Override
    public void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    protected abstract String getMetricsAsText();

    private class MetricsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder response = new StringBuilder();
            response.append(getMetricsAsText());
            exchange.sendResponseHeaders(200, response.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }
}
