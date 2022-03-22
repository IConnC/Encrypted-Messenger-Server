package xyz.iconc.dev.api;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import xyz.iconc.dev.api.shared.resources.MessageResource;
import xyz.iconc.dev.objects.Message;

import java.io.IOException;

public class ClientAPI {
    private final long clientIdentifier;
    private final String clientPassword;

    private ClientResource generateMessageClientResourceGET(long identifier) {
        return generateClientResource("http://localhost:9000/v1/message/" + identifier);
    }
    private ClientResource generateMessageClientResourcePOST() {
        return generateClientResource("http://localhost:9000/v1/message/");
    }

    private ClientResource generateClientResource(String uri) {
        ClientResource clientResource = new ClientResource(uri);

        clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "" + clientIdentifier, clientPassword);
        return clientResource;
    }

    public ClientAPI(long identifier, String password) {
        clientIdentifier = identifier;
        clientPassword = password;
    }


    public static void main(String[] args) throws IOException {
        //ClientAPI clientAPI = new ClientAPI();
        /*
        ClientResource clientResource = generateMessageClientResourceGET(123L, "123");

        clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "999", "secret");

        MessageResource messageResource = clientResource.wrap(MessageResource.class);


        // Retrieve the JSON value
        Message result = messageResource.retrieve();

        if (result != null) {
            System.out.println(result);
        }
         */
    }
}
