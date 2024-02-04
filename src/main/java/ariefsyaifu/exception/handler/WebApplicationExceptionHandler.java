package ariefsyaifu.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.HttpException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionHandler implements ExceptionMapper<WebApplicationException> {
    private static final Logger logger = LoggerFactory.getLogger(WebApplicationExceptionHandler.class);

    @Override
    public Response toResponse(WebApplicationException exception) {
        logger.error("WebApplicationExceptionHandler", exception);
        return Response.status(exception.getResponse().getStatus())
                .entity(new JsonObject().put("message", exception.getResponse()))
                .build();
    }
}
