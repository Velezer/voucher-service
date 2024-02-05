package ariefsyaifu.service.external;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ariefsyaifu.dto.voucher.external.RedeemVoucherRequestBody;
import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.Voucher.ModeType;
import ariefsyaifu.model.Voucher.Status;
import ariefsyaifu.model.Voucher.TransactionType;
import ariefsyaifu.model.Voucher.Type;
import ariefsyaifu.model.Voucher.UsedDayType;
import ariefsyaifu.model.VoucherHistory;
import ariefsyaifu.util.DateUtil;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;

@QuarkusTest
class ExternalVoucherServiceTest {

    String userId = RandomString.make();

    @Inject
    ExternalVoucherService externalVoucherService;

    @BeforeEach
    @Transactional
    void beforeEach() {
        Voucher v = new Voucher();
        v.prefixCode = RandomString.make();
        v.name = RandomString.make();
        v.type = Type.AMOUNT;
        v.amount = BigDecimal.valueOf(10_000);
        v.transactionType = TransactionType.TRANSACTION;
        v.quota = BigDecimal.valueOf(100_000);
        v.usedQuota = BigDecimal.ZERO;
        v.maxDiscount = BigDecimal.valueOf(10_000);
        v.modeType = ModeType.DINE_IN;
        v.minSubtotal = BigDecimal.valueOf(0);
        v.maxRedeemedCount = 1;
        v.usedDayType = UsedDayType.EVERYDAY;
        v.validFrom = DateUtil.now();
        v.validTo = DateUtil.now().plusDays(1);
        v.imageUrl = RandomString.make();
        v.detail = RandomString.make();
        v.qtyClaim = 1l;
        v.qtyClaimed = 1l;
        v.qtyRedeem = 1l;
        v.qtyRedeemed = 0l;
        v.extendValidToInDays = 0;
        v.status = Status.ACTIVE;
        v.persist();

        VoucherHistory vh = new VoucherHistory();
        vh.type = VoucherHistory.Type.CLAIMED;
        vh.voucher = v;
        vh.userId = userId;
        vh.voucherCode = RandomString.make();
        vh.persist();

    }

    @AfterEach
    @Transactional
    void afterEach() {
        VoucherHistory.deleteAll();
        Voucher.deleteAll();
    }

    @Test
    void testRedeem() {
        Assertions.assertEquals(1, VoucherHistory.count());
        VoucherHistory vh = VoucherHistory.find("userId = ?1 and type='CLAIMED'", userId).firstResult();

        RedeemVoucherRequestBody body = new RedeemVoucherRequestBody();
        body.subTotal = BigDecimal.valueOf(20_000);
        body.voucherCode = vh.voucherCode;
        body.userId = userId;
        body.outletId = RandomString.make();
        body.transactionId = RandomString.make();
        
        externalVoucherService.redeem(body);

        VoucherHistory.getEntityManager().clear();
        Assertions.assertEquals(1, VoucherHistory.count());
        vh = VoucherHistory.findAll().firstResult();
        Assertions.assertEquals(VoucherHistory.Type.REDEEMED, vh.type);
        Assertions.assertEquals(body.transactionId, vh.transactionId);
        Assertions.assertEquals(body.userId, vh.userId);
        Assertions.assertEquals(body.voucherCode, vh.voucherCode);

    }
}
