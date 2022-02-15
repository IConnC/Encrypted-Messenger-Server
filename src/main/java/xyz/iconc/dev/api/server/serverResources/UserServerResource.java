package xyz.iconc.dev.api.server.serverResources;

import org.restlet.data.Status;
import org.restlet.resource.ServerResource;
import xyz.iconc.dev.api.shared.objects.User;
import xyz.iconc.dev.api.shared.resources.UserResource;

public class UserServerResource extends ServerResource implements UserResource {

    private static volatile User user;

    @Override
    public void doInit() {
        long rawIdentifier = Long.MIN_VALUE;
        try {
            rawIdentifier = Long.parseLong(getAttribute("userIdentifier"));
        } catch (NumberFormatException e) {
            doError(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        user = new User(rawIdentifier);
    }

    @Override
    public User retrieve() {
        return user;
    }

    @Override
    public void store(User _user) {
        user = _user;
    }

    @Override
    public void remove() {
        user = null;
    }
}
