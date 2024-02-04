package ariefsyaifu.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.HttpException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class HttpExceptionHandler implements ExceptionMapper<HttpException> {
    private static final Logger logger = LoggerFactory.getLogger(HttpExceptionHandler.class);

    @Override
    public Response toResponse(HttpException ex) {
        logger.error("HttpExceptionHandler", ex);
        return Response.status(ex.getStatusCode())
                .entity(new JsonObject().put("message", ex.getPayload()))
                .build();
    }
}
