package org.apache.storm.metric.server;

public interface MetricsServer {
    /**
     * Start the metrics server to expose collected metrics.
     *
     * @param port the port on which the server should run
     */
    void startServer(int port);

    /**
     * Stop the metrics server and release resources.
     */
    void stopServer();
}
