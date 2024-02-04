package ariefsyaifu.dao;

import java.math.BigDecimal;

import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.VoucherHistory;
import ariefsyaifu.util.DateUtil;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ExternalVoucherDao {

    @Transactional
    @TransactionConfiguration(timeout = 30)
    public VoucherHistory redeem(String voucherHistoryId, String userId, BigDecimal voucherAmount,
            String transactionId) {
        VoucherHistory vh = VoucherHistory.findById(voucherHistoryId, LockModeType.PESSIMISTIC_WRITE);
        if (vh == null) {
            throw new HttpException(404, "VOUCHER_NOT_FOUND");
        }
        if (!vh.type.equals(VoucherHistory.Type.CLAIMED)) {
            throw new HttpException(400, "VOUCHER_NOT_CLAIMED");
        }
        if (!vh.userId.equals(userId)) {
            throw new HttpException(400, "VOUCHER_CLAIMED_BY_ANOTHER_PERSON");
        }
        BigDecimal remainingVoucherQuota = vh.voucher.quota.subtract(vh.voucher.usedQuota);
        if (voucherAmount.compareTo(remainingVoucherQuota) > 0) {
            throw new HttpException(400, "QUOTA_RUNGGING_OUT");
        }

        if (vh.voucher.qtyRedeemed >= vh.voucher.qtyRedeem) {
            throw new HttpException(400, "QTY_REDEEM_RUNGGING_OUT");
        }

        Voucher v = Voucher.findById(vh.id, LockModeType.PESSIMISTIC_WRITE);
        v.qtyRedeemed += 1;
        v.persist();

        vh.type = VoucherHistory.Type.REDEEMED;
        vh.redeemedAt = DateUtil.now();
        vh.transactionId = transactionId;
        vh.voucherAmount = voucherAmount;
        vh.persist();
        return vh;
    }

}
