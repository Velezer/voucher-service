package ariefsyaifu.producer;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import ariefsyaifu.model.VoucherHistory;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VoucherProducer {

    @Inject
    @Channel("redeemed-voucher-out")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10000)
    Emitter<String> emitterRedeemedVoucher;

    public void redeemedVoucher(VoucherHistory vh) {
        if (vh == null) {
            return;
        }

        emitterRedeemedVoucher.send(new JsonObject()
                .put("voucherCode", vh.voucherCode)
                .put("userId", vh.userId)
                .put("voucherId", vh.voucher.id)
                .put("voucherAmount", vh.voucherAmount)
                .put("transactionId ", vh.transactionId)
                .encode());
    }
}
