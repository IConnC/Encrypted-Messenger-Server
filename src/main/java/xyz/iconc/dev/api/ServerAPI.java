package xyz.iconc.dev.api;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.routing.VirtualHost;
import xyz.iconc.dev.api.server.UserServerResource;

public class ServerAPI  extends Application {
    private final Router router = new Router();

    public ServerAPI() throws Exception {


        Component component = new Component();


        VirtualHost host = component.getDefaultHost();


        host.attach("/users/123", UserServerResource.class);

        Server server = new Server(Protocol.HTTP, 80, component);


        System.out.println(router.getRoutes());

        server.start();
    }

    @Override
    public Restlet createInboundRoot() {
        router.setContext(getContext());
        return router;
    }

    public static void main(String[] args) throws Exception {
        new ServerAPI();
    }

}
