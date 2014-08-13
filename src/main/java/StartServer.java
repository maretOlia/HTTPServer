import serverBoot.HTTPServer;

/**
 * Created by W on 11.08.2014.
 */
public class StartServer {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        new HTTPServer(port).start();
    }
}
