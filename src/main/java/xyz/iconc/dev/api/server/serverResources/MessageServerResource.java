package xyz.iconc.dev.api.server.serverResources;

import org.restlet.data.Status;
import xyz.iconc.dev.api.ServerAPI;
import xyz.iconc.dev.objects.PostMessage;
import xyz.iconc.dev.api.shared.utilities.Validation;
import xyz.iconc.dev.objects.Message;
import xyz.iconc.dev.api.shared.resources.MessageResource;
import xyz.iconc.dev.objects.UUID;

public class MessageServerResource extends ServerResourceAbstract implements MessageResource {

    private boolean identifierProvided = true;
    private volatile Message message;

    @Override
    public void doInit() {
        long rawIdentifier = Long.MIN_VALUE;
        try {
            String identifierAttribute = getAttribute("identifier");
            if (identifierAttribute == null) // If no identifier is provided don't allow retrieve method, allow post
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
        message = ServerAPI.getResourceManager().getMessage(identifier);
        System.out.println(message);
    }

    @Override
    public Message retrieve() {
        if (!identifierProvided) {
            doError(Status.CLIENT_ERROR_FORBIDDEN);
            return null;
        }

        if (message == null) {
            doError(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }

        return message;
    }


    /**
     * Inserts new message into the database with input validation
     *
     * @param message The initial limited message constraints.
     *                Contains Channel sent and Encrypted Message Contents
     * @return Identifier which message has been assigned
     */
    @Override
    public long store(PostMessage message) {
        long senderIdentifier = Long.parseLong(getClientInfo().getUser().getIdentifier());
        if (!UUID.ValidateUUID(senderIdentifier)) {
            doError(Status.CLIENT_ERROR_FORBIDDEN);
            return 0L;
        }


        // Creates context filled message with provided information and information from authentication to
        //      prevent malicious data from being entered.
        Message fullMessage = new Message(senderIdentifier, message.getChannelIdentifier(),
                message.getMessageContents());

        ServerAPI.getResourceManager().addMessage(fullMessage); // Inserts fully formed message into database

        return fullMessage.getMessageIdentifier(); // Returns the final UUID of the message
    }

    @Override
    public void remove() {

    }
}
