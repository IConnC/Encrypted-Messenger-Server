package xyz.iconc.dev.api.server.serverResources;

import org.restlet.resource.ServerResource;
import xyz.iconc.dev.objects.Channel;
import xyz.iconc.dev.api.shared.resources.PollResource;
import xyz.iconc.dev.objects.Message;
import xyz.iconc.dev.objects.User;
import xyz.iconc.dev.server.ResourceManager;
import xyz.iconc.dev.server.Server;

public class PollServerResource extends ServerResource implements PollResource {
    private volatile long clientIdentifier;
    private ResourceManager resourceManager;

    public PollServerResource() {

    }

    @Override
    public void doInit() {
        clientIdentifier = Long.parseLong(getClientInfo().getUser().getIdentifier());
        resourceManager = Server.getServerInstance().getResourceManager();
    }

    @Override
    public Message[] retrieve() {
        return resourceManager.pollUser(clientIdentifier);
    }
}
