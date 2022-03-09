package xyz.iconc.dev.api;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import xyz.iconc.dev.api.shared.resources.MessageResource;
import xyz.iconc.dev.objects.Message;

import java.io.IOException;

public class ClientAPI {

    private static ClientResource generateMessageClientResourceGET(long identifier) {
        return generateClientResource("http://localhost:9000/v1/message/" + identifier);
    }
    private static ClientResource generateMessageClientResourcePOST() {
        return generateClientResource("http://localhost:9000/v1/message/");
    }

    private static ClientResource generateClientResource(String uri) {
        ClientResource clientResource = new ClientResource(
                uri);

        clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "999", "secret");
        return clientResource;
    }

    public static void main(String[] args) throws IOException {
        ClientResource clientResource = generateMessageClientResourceGET(123L);

        clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "999", "secret");

        MessageResource messageResource = clientResource.wrap(MessageResource.class);


        // Retrieve the JSON value
        Message result = messageResource.retrieve();

        if (result != null) {
            System.out.println(result);
        }
    }
}
