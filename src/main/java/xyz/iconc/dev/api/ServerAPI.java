package xyz.iconc.dev.api;
//https://github.com/restlet/restlet-tutorial/blob/8a334e9b4d7a9dced807a3dd64e5e718c8160afc/modules/org.restlet.tutorial.webapi/src/main/java/org/restlet/tutorial/WebApiTutorial.java#L49
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.routing.VirtualHost;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MemoryRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.iconc.dev.api.server.serverResources.MessageServerResource;
import xyz.iconc.dev.api.server.serverResources.UserServerResource;

public class ServerAPI  extends Application {
    private static Logger LOGGER = LoggerFactory.getLogger(ServerAPI.class);
    private final Router router = new Router();

    public ServerAPI() {
        super();
        LOGGER.info("Contacts application starting...");


        // Attach application to http://localhost:9000/v1
        Component c = new Component();
        c.getServers().add(Protocol.HTTP, 9000);



        c.getDefaultHost().attach("/v1", this);


        try {
            c.start();
        } catch (Exception e) {
            LOGGER.error(e.toString());
            System.exit(1);
        }

        LOGGER.info("Sample Web API started");
        LOGGER.info("URL: http://localhost:9000/v1");
    }



    private ChallengeAuthenticator createApiGuard() {

        ChallengeAuthenticator apiGuard = new ChallengeAuthenticator(
                getContext(), ChallengeScheme.HTTP_BASIC, "realm");
        DatabaseVerifier databaseVerifier = new DatabaseVerifier();

        // Create in-memory users and roles.
        MemoryRealm realm = new MemoryRealm();


        // - Verifier : checks authentication
        // - Enroler : to check authorization (roles)
        apiGuard.setVerifier(databaseVerifier);
        apiGuard.setEnroler(realm.getEnroler());

        // Provide your own authentication checks by extending SecretVerifier or
        // LocalVerifier classes
        // Extend the Enroler class in order to assign roles for an
        // authenticated user

        return apiGuard;
    }

    private Router createApiRouter() {

        // Attach server resources to the given URL template.
        // For instance, CompanyListServerResource is attached
        // to http://localhost:9000/v1/companies
        // and to http://localhost:9000/v1/companies/
        Router router = new Router(getContext());

        router.attach("/users/{identifier}", UserServerResource.class);

        return router;
    }

    public Router publicResources() {
        Router router = new Router();

        //router.attach("/ping", PingServerResource.class);

        return router;
    }

    @Override
    public Restlet createInboundRoot() {
        Router publicRouter = publicResources();

        // Create the api router, protected by a guard
        ChallengeAuthenticator apiGuard = createApiGuard();
        Router apiRouter = createApiRouter();
        apiGuard.setNext(apiRouter);

        publicRouter.attachDefault(apiGuard);

        return publicRouter;
    }

    public static void main(String[] args) throws Exception {
        ServerAPI server = new ServerAPI();
    }

}
