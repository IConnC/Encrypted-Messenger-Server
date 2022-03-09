package xyz.iconc.dev.api.shared.resources;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import xyz.iconc.dev.objects.PostMessage;
import xyz.iconc.dev.objects.Message;

public interface MessageResource extends BaseResource {
    @Get("json")
    public Message retrieve();

    @Put("json")
    public long store(PostMessage message);

    @Delete("json")
    public void remove();
}
