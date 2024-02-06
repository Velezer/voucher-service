package ariefsyaifu.consumer;

import java.math.BigDecimal;

import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.Voucher.ModeType;
import ariefsyaifu.model.Voucher.Status;
import ariefsyaifu.model.Voucher.TransactionType;
import ariefsyaifu.model.Voucher.Type;
import ariefsyaifu.model.Voucher.UsedDayType;
import ariefsyaifu.model.VoucherHistory;
import ariefsyaifu.util.DateUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;

@QuarkusTest
class VoucherConsumer_Redeem_Test {

    @Inject
    VoucherConsumer voucherConsumer;

    String transactionId = RandomString.make();
    String outletId = RandomString.make();
    String userId = RandomString.make();
    String userName = RandomString.make();

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
        vh.userId = userId;
        vh.userName = userName;
        vh.voucher = v;
        vh.voucherCode = RandomString.make();
        vh.claimedAt = DateUtil.now();
        vh.persist();
    }

    @AfterEach
    @Transactional
    void afterEach() {
        VoucherHistory.deleteAll();
        Voucher.deleteAll();
    }

    @Test
    void testRedeemVoucher() {
        Assertions.assertEquals(1, Voucher.count());
        Assertions.assertEquals(1, VoucherHistory.count());

        VoucherHistory vh = VoucherHistory.findAll().firstResult();
        Assertions.assertEquals(VoucherHistory.Type.CLAIMED, vh.type);
        Assertions.assertNotNull(vh.userId);
        Assertions.assertEquals(vh.userId, userId);
        Assertions.assertNotNull(vh.claimedAt);
        Assertions.assertNotNull(vh.voucherCode);
        Assertions.assertEquals(userName, vh.userName);

        Assertions.assertEquals(1, vh.voucher.qtyClaimed);
        Assertions.assertEquals(0, vh.voucher.qtyRedeemed);

        voucherConsumer.redeemVoucher(new JsonObject()
                .put("subTotal", 100_000)
                .put("voucherCode", vh.voucherCode)
                .put("userId", userId)
                .put("outletId", outletId)
                .put("transactionId", transactionId)
                .encode());

        VoucherHistory.getEntityManager().clear();
        vh = VoucherHistory.findAll().firstResult();
        Assertions.assertEquals(VoucherHistory.Type.REDEEMED, vh.type);
        Assertions.assertEquals(vh.userId, userId);
        Assertions.assertEquals(vh.userName, userName);
        Assertions.assertNotNull(vh.claimedAt);

        Assertions.assertEquals(1, vh.voucher.qtyClaimed);
        Assertions.assertEquals(1, vh.voucher.qtyRedeemed);

    }

    @Test
    void testRedeemVoucher_KafkaException() {
        Assertions.assertEquals(1, Voucher.count());
        Assertions.assertEquals(1, VoucherHistory.count());

        VoucherHistory vh = VoucherHistory.findAll().firstResult();
        Assertions.assertEquals(VoucherHistory.Type.CLAIMED, vh.type);
        Assertions.assertNotNull(vh.userId);
        Assertions.assertEquals(vh.userId, userId);
        Assertions.assertNotNull(vh.claimedAt);
        Assertions.assertNotNull(vh.voucherCode);
        Assertions.assertEquals(userName, vh.userName);

        Assertions.assertEquals(1, vh.voucher.qtyClaimed);
        Assertions.assertEquals(0, vh.voucher.qtyRedeemed);

        KafkaException exception = Assertions.assertThrows(KafkaException.class, () -> {
            voucherConsumer.redeemVoucher(new JsonObject()
                    .put("subTotal", 100_000)
                    .put("userId", userId)
                    .put("outletId", outletId)
                    .put("transactionId", transactionId)
                    .encode());
        });

        Assertions.assertTrue(exception.getMessage().contains("voucherCode"));

        VoucherHistory.getEntityManager().clear();
        vh = VoucherHistory.findAll().firstResult();
        Assertions.assertEquals(VoucherHistory.Type.CLAIMED, vh.type);
        Assertions.assertNotNull(vh.userId);
        Assertions.assertEquals(vh.userId, userId);
        Assertions.assertNotNull(vh.claimedAt);
        Assertions.assertNotNull(vh.voucherCode);
        Assertions.assertEquals(userName, vh.userName);

        Assertions.assertEquals(1, vh.voucher.qtyClaimed);
        Assertions.assertEquals(0, vh.voucher.qtyRedeemed);
    }

}
