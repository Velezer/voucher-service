package ariefsyaifu.controller;

import java.text.ParseException;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import ariefsyaifu.dto.error.ErrorOas;
import ariefsyaifu.dto.voucher.ViewClaimVoucherOas;
import ariefsyaifu.dto.voucher.ViewVoucherRewardOas;
import ariefsyaifu.service.VoucherService;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/voucher")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecuritySchemes(value = {
        @SecurityScheme(securitySchemeName = "accessToken", type = SecuritySchemeType.HTTP, scheme = "bearer", apiKeyName = "Authorization: Bearer", bearerFormat = "jwt") })
public class VoucherController {

    @Inject
    public VoucherController(
            VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    private VoucherService voucherService;

    @GET
    @Path("/")
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "Get List Voucher")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewVoucherRewardOas[].class))),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
    })
    public Response list(
            @Parameter(hidden = false) @HeaderParam("X-Consumer-Custom-ID") JsonObject customId,
            @QueryParam("search") String search) throws ParseException {
        return Response.ok(voucherService.list(customId, search)).build();
    }

    @POST
    @Path("/{id}/claim")
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "Claim And Get The Voucher Code")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewClaimVoucherOas.class))),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
    })
    public Response claim(
            @Parameter(hidden = false) @HeaderParam("X-Consumer-Custom-ID") JsonObject customId,
            @PathParam("id") String id) {
        return Response.ok(voucherService.claim(id, customId)).build();
    }

}
