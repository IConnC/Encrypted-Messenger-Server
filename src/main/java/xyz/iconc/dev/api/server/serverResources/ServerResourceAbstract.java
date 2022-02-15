package xyz.iconc.dev.api.server.serverResources;

import org.restlet.data.Status;
import org.restlet.resource.ServerResource;
import xyz.iconc.dev.api.shared.objects.User;
import xyz.iconc.dev.api.shared.utilities.Validation;

public abstract class ServerResourceAbstract extends ServerResource {
    protected volatile long identifier;

    public ServerResourceAbstract() {

    }

    @Override
    public void doInit() {
        long rawIdentifier = Long.MIN_VALUE;
        try {
            rawIdentifier = Long.parseLong(getAttribute("identifier"));
        } catch (NumberFormatException e) {
            doError(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        if (!Validation.ValidateIdentifier(rawIdentifier)) {
            doError(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }
        identifier = rawIdentifier;
    }

}
