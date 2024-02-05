package ariefsyaifu.service.external;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ariefsyaifu.dao.ExternalVoucherDao;
import ariefsyaifu.dto.voucher.external.RedeemVoucherRequestBody;
import ariefsyaifu.dto.voucher.external.ViewClaimedVoucherExternalOas;
import ariefsyaifu.model.VoucherHistory;
import ariefsyaifu.model.VoucherOutlet;
import ariefsyaifu.producer.VoucherProducer;
import ariefsyaifu.util.DateUtil;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExternalVoucherService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalVoucherService.class);

    public ExternalVoucherService(
            ExternalVoucherDao externalVoucherDao,
            VoucherProducer voucherProducer) {
        this.externalVoucherDao = externalVoucherDao;
        this.voucherProducer = voucherProducer;
    }

    private VoucherProducer voucherProducer;
    private ExternalVoucherDao externalVoucherDao;

    public List<ViewClaimedVoucherExternalOas> list(String userId, String outletId) {

        List<VoucherHistory> vhs = VoucherHistory.find("""
                userId = ?1
                and type = 'CLAIMED'
                and voucher.status = 'ACTIVE'
                and voucher.modeType in ('DINE_IN','PICK_UP')
                    """,
                userId)
                .list();

        return vhs.stream()
                .map(vh -> {
                    vh.voucher.validTo = vh.voucher.validTo.plusDays(vh.voucher.extendValidToInDays);
                    return vh;
                })
                .filter(vh -> vh.voucher.validTo.isAfter(DateUtil.now())) // filter expired
                .filter(vh -> {
                    List<VoucherOutlet> outlets = Optional.ofNullable(vh.voucher.outlets).orElse(List.of());
                    return outlets.isEmpty() ||
                            outlets.stream()
                                    .map(o -> o.outletId) // map to outletId
                                    .anyMatch(oid -> oid.equalsIgnoreCase(outletId));
                })
                .map(vh -> ViewClaimedVoucherExternalOas.valueOf(vh))
                .toList();

    }

    public ViewRedeemedVoucherOas redeem(RedeemVoucherRequestBody params) {
        VoucherHistory vh = VoucherHistory
                .find("""
                        deletedAt is null
                        and voucherCode = ?1
                            """,
                        params.voucherCode)
                .firstResult();
        if (vh == null) {
            throw new HttpException(400, "VOUCHER_NOT_CLAIMED");
        }
        if (!vh.voucher.isActive()) {
            throw new HttpException(400, "VOUCHER_NOT_ACTIVE");
        }
        if (vh.isRedeemed()) {
            throw new HttpException(400, "VOUCHER_HAS_BEEN_REDEEMED");
        }
        if (!vh.isClaimed()) {
            throw new HttpException(400, "VOUCHER_NOT_CLAIMED");
        }
        if (!vh.userId.equalsIgnoreCase(params.userId)) {
            throw new HttpException(400, "VOUCHER_CLAIMED_BY_ANOTHER_PERSON");
        }

        if (params.subTotal.compareTo(vh.voucher.minSubtotal) < 0) {
            throw new HttpException(400, "MIN_SUBTOTAL_NOT_SATISFIED");
        }

        LocalDateTime validTo = vh.voucher.validTo.plusDays(vh.voucher.extendValidToInDays);
        if (DateUtil.now().isAfter(validTo)) { // isExpired
            throw new HttpException(400, "EXPIRED");
        }

        BigDecimal baseRp = params.subTotal;
        BigDecimal voucherAmount = vh.voucher.amount;
        if (vh.voucher.isPercentage()) {
            voucherAmount = baseRp.multiply(voucherAmount).divide(BigDecimal.valueOf(100));
            if (voucherAmount.compareTo(vh.voucher.maxDiscount) > 0) {
                voucherAmount = vh.voucher.maxDiscount;
            }
        }
        if (voucherAmount.compareTo(baseRp) > 0) {
            voucherAmount = baseRp;
        }
        BigDecimal remainingVoucherQuota = vh.voucher.quota.subtract(vh.voucher.usedQuota);
        if (voucherAmount.compareTo(remainingVoucherQuota) > 0) {
            throw new HttpException(400, "QUOTA_RUNNING_OUT");
        }

        List<VoucherOutlet> outlets = Optional.ofNullable(vh.voucher.outlets).orElse(List.of());
        if (!outlets.isEmpty()) {
            boolean isOutletRight = outlets.stream()
                    .map(o -> o.outletId) // map to outletId
                    .anyMatch(oid -> oid.equalsIgnoreCase(params.outletId));

            if (!isOutletRight) {
                throw new HttpException(400, "VOUCHER_NOT_ELIGIBLE_FOR_THIS_OUTLET");
            }
        }

        VoucherHistory redeemed = externalVoucherDao.redeem(
                vh.id,
                params.userId,
                voucherAmount,
                params.transactionId);

        voucherProducer.redeemedVoucher(redeemed);

        return ViewRedeemedVoucherOas.valueOf(redeemed);
    }

}
