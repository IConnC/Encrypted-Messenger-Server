package xyz.iconc.dev.api.shared.resources;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import xyz.iconc.dev.api.shared.objects.User;

public interface UserResource extends BaseResource {
    @Get("json")
    public User retrieve();

    @Delete("json")
    public void remove();
}
