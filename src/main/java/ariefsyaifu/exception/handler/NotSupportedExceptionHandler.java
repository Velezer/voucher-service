package ariefsyaifu.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotSupportedExceptionHandler implements ExceptionMapper<NotSupportedException> {
    private static final Logger logger = LoggerFactory.getLogger(NotSupportedExceptionHandler.class);

    @Override
    public Response toResponse(NotSupportedException exception) {
        logger.error("NotSupportedExceptionHandler", exception);
        Response response = exception.getResponse();
        return Response.status(response.getStatus()).entity(response.getEntity()).build();
    }
}
