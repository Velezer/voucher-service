package ariefsyaifu.controller.external;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
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
import ariefsyaifu.dto.voucher.external.RedeemVoucherRequestBody;
import ariefsyaifu.dto.voucher.external.ViewClaimedVoucherExternalOas;
import ariefsyaifu.service.external.ExternalVoucherService;
import ariefsyaifu.service.external.ViewRedeemedVoucherOas;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/external/voucher")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecuritySchemes(value = {
		@SecurityScheme(securitySchemeName = "accessTokenApiKey", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, apiKeyName = "api-key") })
public class ExternalVoucherController {

	@Inject
	public ExternalVoucherController(
			ExternalVoucherService externalVoucherService) {
		this.externalVoucherService = externalVoucherService;
	}

	private ExternalVoucherService externalVoucherService;

	@Path("/")
	@GET
	@SecurityRequirements(value = { @SecurityRequirement(name = "accessTokenApiKey") })
	@Operation(summary = "Get List Claimed Voucher")
	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewClaimedVoucherExternalOas[].class))),
			@APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
			@APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
	})
	public Response list(
			@QueryParam("userId") String userId,
			@QueryParam("outletId") String outletId) {

		return Response.ok(externalVoucherService.list(userId, outletId)).build();
	}

	@Path("/redeem")
	@POST
	@SecurityRequirements(value = { @SecurityRequirement(name = "accessTokenApiKey") })
	@Operation(summary = "Redeem Voucher Code")
	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ViewRedeemedVoucherOas.class))),
			@APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
			@APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
	})
	public Response redeem(
			@Valid RedeemVoucherRequestBody params) {
		return Response.ok(externalVoucherService.redeem(params)).build();
	}

}
