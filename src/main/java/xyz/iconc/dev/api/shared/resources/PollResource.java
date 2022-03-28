package xyz.iconc.dev.api.shared.resources;

import org.restlet.resource.Get;
import xyz.iconc.dev.api.shared.objects.PollObject;

public interface PollResource {
    @Get("json")
    public PollObject retrieve();
}
