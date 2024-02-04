package ariefsyaifu.client;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/api/v1")
@RegisterRestClient
public interface TagClient {
    @Path("/internal/tag/")
    @GET
    public List<String> getTags(
            @QueryParam("userId") String userId);
}
