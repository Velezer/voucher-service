package ariefsyaifu.controller;

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

import ariefsyaifu.dto.ViewCountOas;
import ariefsyaifu.dto.error.ErrorOas;
import ariefsyaifu.dto.voucher.backoffice.CreateVoucherRequestBody;
import ariefsyaifu.dto.voucher.backoffice.UpdateVoucherRequestBody;
import ariefsyaifu.dto.voucher.backoffice.ViewVoucherIdOas;
import ariefsyaifu.dto.voucher.backoffice.ViewVoucherOas;
import ariefsyaifu.service.backoffice.BackOfficeVoucherService;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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
public class BackofficeVoucherController {

    @Inject
    public BackofficeVoucherController(
            BackOfficeVoucherService backOfficeVoucherService) {
        this.backOfficeVoucherService = backOfficeVoucherService;
    }

    private BackOfficeVoucherService backOfficeVoucherService;

    @GET
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "List Voucher")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewVoucherOas[].class))),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
    })
    public Response list(
            @DefaultValue("") @QueryParam("search") String search,
            @DefaultValue("5") @QueryParam("limit") int limit,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("false") @QueryParam("isAscending") boolean isAscending,
            @DefaultValue("createdAt") @QueryParam("orderBy") String orderBy) {
        return Response.ok(backOfficeVoucherService.list(search, orderBy, isAscending, offset, limit)).build();
    }
    @GET
    @Path("/count")
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "Count Voucher")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewCountOas.class))),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
    })
    public Response count(
            @DefaultValue("") @QueryParam("search") String search) {
        return Response.ok(backOfficeVoucherService.count(search)).build();
    }

    @GET
    @Path("/{id}")
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "Detail Voucher")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewVoucherOas.class))),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
    })
    public Response detail(
            @Parameter(required = true) @PathParam("id") String id) {
        return Response.ok().entity(backOfficeVoucherService.getById(id)).build();
    }

    @POST
    @Path("/")
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "Add Voucher")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewVoucherIdOas.class))),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
    })
    public Response create(
            @Parameter(hidden = false) @HeaderParam("X-Consumer-Custom-ID") JsonObject customId,
            @Valid CreateVoucherRequestBody request) {
        String userId = customId.getString("userId");
        return Response.ok().entity(backOfficeVoucherService.create(request, userId)).build();
    }

    @PUT
    @Path("/{id}")
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "Update Voucher")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "NO CONTENT"),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
    })
    public Response update(
            @Parameter(hidden = true) @HeaderParam("X-Consumer-Custom-ID") JsonObject customId,
            @Parameter(required = true) @PathParam("id") String id,
            UpdateVoucherRequestBody request) {
        String userId = customId.getString("userId");
        backOfficeVoucherService.update(id, request, userId);
        return Response.noContent().build();
    }

    @POST
    @Path("/combinationCode/generate")
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "Generate Code Combination")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewCountOas.class)))
    })
    public Response generateCombinationCodes() {
        return Response.ok(backOfficeVoucherService.generateCombinationCodes()).build();
    }
    @GET
    @Path("/combinationCode/count")
    @SecurityRequirements(value = { @SecurityRequirement(name = "accessToken") })
    @Operation(summary = "Generate Code Combination")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewCountOas.class)))
    })
    public Response countCombinationCodes() {
        return Response.ok(backOfficeVoucherService.countCombinationCodes()).build();
    }

}