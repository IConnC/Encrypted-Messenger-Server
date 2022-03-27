package xyz.iconc.dev.api.server.serverResources;

import xyz.iconc.dev.api.shared.objects.User;
import xyz.iconc.dev.api.shared.resources.UserResource;
import xyz.iconc.dev.server.ResourceManager;
import xyz.iconc.dev.server.Server;

public class UserServerResource extends ServerResourceAbstract implements UserResource {
    long apiClientIdentifier;
    private static volatile User user;

    @Override
    public void doInit() {
        super.doInit();
        apiClientIdentifier = Long.parseLong(getClientInfo().getUser().getIdentifier());
        user = new User(identifier);
    }

    @Override
    public User retrieve() {
        if (identifier == apiClientIdentifier) {
            Server.getServerInstance().getResourceManager().getUser(identifier);
        }
        return user;
    }

    @Override
    public void remove() {
        user = null;
    }
}
