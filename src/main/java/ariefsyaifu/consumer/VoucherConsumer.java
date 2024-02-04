package ariefsyaifu.consumer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import ariefsyaifu.dao.VoucherDao;
import ariefsyaifu.dto.voucher.external.RedeemVoucherRequestBody;
import ariefsyaifu.exception.AbortException;
import ariefsyaifu.service.external.ExternalVoucherService;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ApplicationScoped
public class VoucherConsumer {

	@Inject
	public VoucherConsumer(
			Validator validator,
			ExternalVoucherService externalVoucherService,
			VoucherDao voucherDao) {
		this.validator = validator;
		this.externalVoucherService = externalVoucherService;
		this.voucherDao = voucherDao;
	}

	private VoucherDao voucherDao;
	private ExternalVoucherService externalVoucherService;
	private Validator validator;

	@Incoming("claim-voucher-in")
	@Blocking
	@Retry(delay = 1000, abortOn = AbortException.class)
	public void claimVoucher(String message) {
		JsonObject payload = new JsonObject(message);
		String voucherId = payload.getString("voucherId");
		String userId = payload.getString("userId");
		String userName = payload.getString("userName");
		String tierId = payload.getString("tierId");
		List<String> tagIds = Optional
				.ofNullable(payload.getJsonArray("tagIds"))
				.orElse(new JsonArray())
				.getList();
		voucherDao.claim(voucherId, userId, userName, tierId, tagIds);
	}

	@Incoming("redeem-voucher-in")
	@Blocking
	@Retry(delay = 1000, abortOn = AbortException.class)
	public void redeemVoucher(String message) {
		JsonObject payload = new JsonObject(message);
		RedeemVoucherRequestBody rb = payload.mapTo(RedeemVoucherRequestBody.class);
		Set<ConstraintViolation<RedeemVoucherRequestBody>> violations = validator.validate(rb);
		for (ConstraintViolation<RedeemVoucherRequestBody> violation : violations) {
			throw new AbortException(violation.getMessage());
		}
		externalVoucherService.redeem(rb);
	}
}
