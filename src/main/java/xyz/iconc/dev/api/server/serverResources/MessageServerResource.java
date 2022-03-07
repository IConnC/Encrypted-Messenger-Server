package xyz.iconc.dev.api.server.serverResources;

import org.restlet.data.Status;
import xyz.iconc.dev.api.ServerAPI;
import xyz.iconc.dev.api.shared.utilities.Validation;
import xyz.iconc.dev.server.apiObjects.Message;
import xyz.iconc.dev.api.shared.objects.User;
import xyz.iconc.dev.api.shared.resources.MessageResource;

public class MessageServerResource extends ServerResourceAbstract implements MessageResource {

    private boolean identifierProvided = true;
    private volatile Message message;

    @Override
    public void doInit() {
        long rawIdentifier = Long.MIN_VALUE;
        try {
            String identifierAttribute = getAttribute("identifier");
            if (identifierAttribute == null)
            {
                identifierProvided = false;
                return;
            }
            rawIdentifier = Long.parseLong(identifierAttribute);

        } catch (NumberFormatException e) {
            doError(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        if (!Validation.ValidateIdentifier(rawIdentifier)) {
            doError(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }
        identifier = rawIdentifier;
        message = ServerAPI.getDatabaseManager().get_message(identifier);
    }

    @Override
    public Message retrieve() {
        if (!identifierProvided) {
            doError(Status.CLIENT_ERROR_FORBIDDEN);
            return null;
        }
        return message;
    }

    @Override
    public void store(Message message) {
        // Implement verifying information based on web client session identifier
        ServerAPI.getDatabaseManager().insert_message(message);
    }

    @Override
    public void remove() {

    }
}
