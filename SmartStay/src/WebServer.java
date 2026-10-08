import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Lightweight Built-in Web Server for SmartStay Hotel Reservation System.
 * Serves the modern HTML5/CSS/JavaScript web interface on http://localhost:8080
 * Requires zero external dependencies (uses standard JDK com.sun.net.httpserver).
 */
public class WebServer {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/", new StaticFileHandler());
            server.setExecutor(null); // default executor
            server.start();

            String url = "http://localhost:" + PORT;
            System.out.println("=================================================");
            System.out.println("  ✦ SMARTSTAY LUXURY HOTEL - WEB SERVER ACTIVE  ✦");
            System.out.println("=================================================");
            System.out.println("  Local Web Portal : " + url);
            System.out.println("  Serving Files From: web/");
            System.out.println("  Press Ctrl+C to stop the server.");
            System.out.println("=================================================");

            // Automatically open browser on Windows
            tryOpenBrowser(url);

        } catch (IOException e) {
            System.err.println("Could not start WebServer on port " + PORT + ": " + e.getMessage());
            // Retry on port 8081 if 8080 is busy
            try {
                HttpServer backup = HttpServer.create(new InetSocketAddress(8081), 0);
                backup.createContext("/", new StaticFileHandler());
                backup.setExecutor(null);
                backup.start();
                String url = "http://localhost:8081";
                System.out.println("Started on alternative port: " + url);
                tryOpenBrowser(url);
            } catch (Exception ex) {
                System.err.println("Failed to start backup server: " + ex.getMessage());
            }
        }
    }

    private static void tryOpenBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                new ProcessBuilder("cmd", "/c", "start", url).start();
            }
        } catch (Exception ignored) {
            // If browser launch fails, URL is already printed
        }
    }

    /** Serves files from web/ or SmartStay/web/ */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            if (requestPath.equals("/") || requestPath.isEmpty()) {
                requestPath = "/index.html";
            }

            // Look in current dir web/, or SmartStay/web/
            Path filePath = findFile(requestPath.substring(1));

            if (filePath == null || !Files.exists(filePath) || Files.isDirectory(filePath)) {
                String response = "404 Not Found: " + requestPath;
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            String mimeType = getMimeType(filePath.toString());
            exchange.getResponseHeaders().set("Content-Type", mimeType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            byte[] bytes = Files.readAllBytes(filePath);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private Path findFile(String relative) {
            Path p1 = Paths.get("web", relative);
            if (Files.exists(p1)) return p1;

            Path p2 = Paths.get("SmartStay", "web", relative);
            if (Files.exists(p2)) return p2;

            Path p3 = Paths.get("..", "web", relative);
            if (Files.exists(p3)) return p3;

            return p1;
        }

        private String getMimeType(String filename) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".html")) return "text/html; charset=utf-8";
            if (lower.endsWith(".css")) return "text/css; charset=utf-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=utf-8";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".json")) return "application/json";
            return "application/octet-stream";
        }
    }
}
