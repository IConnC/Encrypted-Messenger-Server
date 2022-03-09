package xyz.iconc.dev.api.shared.resources;

import org.restlet.resource.Get;
import xyz.iconc.dev.objects.PostMessage;

public interface BulkMessageResource extends BaseResource {
    @Get("json")
    public PostMessage[] retrieve();
}
