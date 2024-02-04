package ariefsyaifu.controller;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import ariefsyaifu.dto.error.ErrorOas;
import ariefsyaifu.dto.voucher.backoffice.ViewVoucherHistoryOas;
import ariefsyaifu.service.backoffice.BackOfficeVoucherHistoryService;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/backoffice/voucher")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecuritySchemes(value = {
        @SecurityScheme(securitySchemeName = "accessToken", type = SecuritySchemeType.HTTP, scheme = "bearer", apiKeyName = "Authorization: Bearer", bearerFormat = "jwt") })
public class BackofficeVoucherHistoryController {

    @Inject
    public BackofficeVoucherHistoryController(
            BackOfficeVoucherHistoryService backOfficeVoucherHistoryService) {
        this.backOfficeVoucherHistoryService = backOfficeVoucherHistoryService;
    }

    private BackOfficeVoucherHistoryService backOfficeVoucherHistoryService;

    @GET
    @Path("/{id}/history")
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "Get List Voucher History")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewVoucherHistoryOas[].class))),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
    })
    public Response list(
            @PathParam("id") String voucherId,
            @DefaultValue("") @QueryParam("search") String search,
            @DefaultValue("5") @QueryParam("limit") int limit,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("false") @QueryParam("isAscending") boolean isAscending,
            @DefaultValue("createdAt") @QueryParam("orderBy") String orderBy) throws ValidationException {
        return Response.ok(backOfficeVoucherHistoryService.list(
                voucherId,
                search,
                limit,
                offset,
                isAscending,
                orderBy)).build();
    }

}
