package xyz.iconc.dev.api.server.serverResources;

import org.restlet.data.Status;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.iconc.dev.api.shared.objects.Message;
import xyz.iconc.dev.api.shared.resources.BulkMessageResource;
import xyz.iconc.dev.api.shared.utilities.Validation;

public class BulkMessageServerResource extends ServerResource implements BulkMessageResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkMessageServerResource.class);


    /**
     * If messageRange and lastMessage are both defined, messageRange is prioritized
     */
    @Override
    public void doInit() {
        // Which channel to look in (identifier)
        long channelIdentifier = Long.parseLong(getQueryValue("channelIdentifier"));

        // Message range such as 7-5 with lower values being more recent
        String tempRange = getQueryValue("messageRange");

        // Last messages integer - for instance "5" would return the last 5 messages
        int lastMessage = Integer.parseInt(getQueryValue("lastMessages"));

        if (!Validation.ValidateIdentifier(channelIdentifier)) return;

        if (tempRange == null && lastMessage == 0) {
            doError(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }



    }

    @Override
    public Message[] retrieve() {
        return new Message[0];
    }
}
