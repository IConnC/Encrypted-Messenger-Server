package xyz.iconc.dev.api.server.serverResources;

import org.restlet.resource.ServerResource;
import xyz.iconc.dev.api.shared.objects.PollObject;
import xyz.iconc.dev.api.shared.resources.PollResource;

public class PollServerResource extends ServerResource implements PollResource {
    private volatile long clientIdentifier;

    public PollServerResource() {

    }

    @Override
    public void doInit() {
        clientIdentifier = Long.parseLong(getClientInfo().getUser().getIdentifier());
    }

    @Override
    public PollObject retrieve() {
        return null;
    }
}
