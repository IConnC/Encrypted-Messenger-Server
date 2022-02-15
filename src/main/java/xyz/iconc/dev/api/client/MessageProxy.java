package xyz.iconc.dev.api.client;

import org.restlet.resource.*;
import xyz.iconc.dev.api.shared.objects.User;

public interface MessageProxy extends ClientProxy {
    @Get
    public void retrieve(Result callback);

    @Put
    public void store(User contact, Result callback);

    @Delete
    public void remove(Result callback);
}
