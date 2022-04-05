package xyz.iconc.dev.api.shared.resources;

import org.restlet.resource.Get;
import xyz.iconc.dev.objects.Channel;
import xyz.iconc.dev.objects.Message;

public interface PollResource {
    @Get("json")
    public Message[] retrieve();
}
