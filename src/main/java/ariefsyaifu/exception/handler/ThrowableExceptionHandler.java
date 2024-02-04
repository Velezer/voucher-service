package ariefsyaifu.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ThrowableExceptionHandler implements ExceptionMapper<Throwable> {
    private static final Logger logger = LoggerFactory.getLogger(ThrowableExceptionHandler.class);

    @Override
    public Response toResponse(Throwable ex) {
        logger.error("ThrowableExceptionHandler", ex);
        return Response.serverError().entity(new JsonObject().put("message", "INTERNAL_SERVER_ERROR")).build();
    }
}
