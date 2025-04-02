package goryachev.research.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Server.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        int port = 8000;
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/test", new ServerHttpHandler());
        server.setExecutor(executor);
        server.start();
        p("Server started on port " + port);
    }

    private static void p(Object x) {
        System.out.println(x);
    }

    private static class ServerHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange xch) throws IOException {
            String requestParamValue = null;
            String method = xch.getRequestMethod();
            if ("GET".equals(method)) {
                requestParamValue = handleGetRequest(xch);
            } else if ("POST".equals(method)) {
                requestParamValue = handlePostRequest(xch);
            }
            handleResponse(xch, requestParamValue);
        }

        private String handleGetRequest(HttpExchange xch) {
            return xch.getRequestURI().toString();
        }

        private String handlePostRequest(HttpExchange xch) {
            return handleGetRequest(xch);
        }

        private void handleResponse(HttpExchange xch, String param) throws IOException {
            try (OutputStream out = xch.getResponseBody()) {
                String rsp = "<html><body><h1>Yo " + param + "</h1></body></html>";
                xch.sendResponseHeaders(200, rsp.length());
                out.write(rsp.getBytes());
            }
        }
    }
}
