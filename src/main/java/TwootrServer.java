import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class TwootrServer extends WebSocketServer {

    public static final int STATIC_PORT = 8080;
    public static final int WEBSOCKET_PORT = 9000;

    private static final String USER_NAME = "hyunwoo";
    private static final String PASSWORD = "123456";
    private static final String OTHER_USER_NAME = "woohyun";

    public static void main(String[] args) throws Exception {

        InetSocketAddress websocketAddress = new InetSocketAddress("localhost", WEBSOCKET_PORT);
        TwootrServer twootrServer = new TwootrServer(websocketAddress);
        twootrServer.start();

        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setResourceBase(System.getProperty("user.dir") + "/src/main/webapp");
        context.setContextPath("/");

        ServletHolder staticContentServlet = new ServletHolder("staticContentServlet", DefaultServlet.class);
        staticContentServlet.setInitParameter("dirAllowed", "true");
        context.addServlet(staticContentServlet, "/");

        Server jettyServer = new Server(STATIC_PORT);
        jettyServer.setHandler(context);
        jettyServer.start();
        jettyServer.dumpStdErr();
        jettyServer.join();

    }

    private final TwootRepository twootRepository = new InMemoryTwootRepository();
    private final UserRepository userRepository = new InMemoryUserRepository();
    private final Twootr twootr = new Twootr(twootRepository, userRepository);
    private final Map<WebSocket,WebSocketEndPoint> socketToEndPoint = new HashMap<>();

    public TwootrServer(InetSocketAddress address) {
        super(address, 1);

        twootr.onRegisterUser(USER_NAME, PASSWORD);
        twootr.onRegisterUser(OTHER_USER_NAME,PASSWORD);
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        socketToEndPoint.put(conn, new WebSocketEndPoint(twootr, conn));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        socketToEndPoint.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        WebSocketEndPoint webSocketEndPoint = socketToEndPoint.get(conn);
        try {
            webSocketEndPoint.onMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {

    }
}
