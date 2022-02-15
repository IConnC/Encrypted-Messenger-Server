package xyz.iconc.dev.api.shared.resources;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import xyz.iconc.dev.api.shared.objects.User;

public interface MessageResource extends BaseResource {
    @Get("json")
    public User retrieve();

    @Put("json")
    public void store(User contact);

    @Delete("json")
    public void remove();
}
