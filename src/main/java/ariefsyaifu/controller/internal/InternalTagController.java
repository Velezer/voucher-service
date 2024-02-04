package ariefsyaifu.controller.internal;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/internal/tag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InternalTagController {

    @GET
    @Path("/")
    public Response list(
            @QueryParam("userId") String userId) {
        return Response.ok(List.of("FEBRUARI", "FEB")).build();
    }
}
