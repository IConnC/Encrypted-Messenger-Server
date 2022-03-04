package xyz.iconc.dev.api.shared.resources;

import org.restlet.resource.Get;
import xyz.iconc.dev.api.shared.objects.Message;

public interface BulkMessageResource extends BaseResource {
    @Get("json")
    public Message[] retrieve();
}
