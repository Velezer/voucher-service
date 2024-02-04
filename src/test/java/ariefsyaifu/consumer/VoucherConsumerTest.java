package ariefsyaifu.consumer;

import java.math.BigDecimal;

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
// @QuarkusTestResource(KafkaCompanionResource.class)
class VoucherConsumerTest {

    // @ConfigProperty(name = "mp.messaging.incoming.claim-voucher-in.topic")
    // String topicClaimVoucher;

    // @InjectKafkaCompanion
    // KafkaCompanion kafkaCompanion;

    @Inject
    VoucherConsumer voucherConsumer;

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
        v.qtyClaimed = 0l;
        v.qtyRedeem = 1l;
        v.qtyRedeemed = 0l;
        v.extendValidToInDays = 0;
        v.status = Status.ACTIVE;
        v.persist();

        VoucherHistory vh = new VoucherHistory();
        vh.type = VoucherHistory.Type.AVAILABLE;
        vh.voucher = v;
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
    void testClaimVoucher() {
        Assertions.assertEquals(1, Voucher.count());
        Assertions.assertEquals(1, VoucherHistory.count());

        VoucherHistory vh = VoucherHistory.findAll().firstResult();
        Assertions.assertEquals(VoucherHistory.Type.AVAILABLE, vh.type);
        Assertions.assertNotEquals(vh.userId, userId);
        Assertions.assertNull(vh.claimedAt);
        Assertions.assertNull(vh.userName);
        Assertions.assertNull(vh.userId);

        Assertions.assertEquals(0, vh.voucher.qtyClaimed);

        voucherConsumer.claimVoucher(new JsonObject()
                .put("voucherId", vh.voucher.id)
                .put("userId", userId)
                .put("userName", userName)
                .encode());

        VoucherHistory.getEntityManager().clear();
        vh = VoucherHistory.findAll().firstResult();
        Assertions.assertEquals(VoucherHistory.Type.CLAIMED, vh.type);
        Assertions.assertEquals(vh.userId, userId);
        Assertions.assertEquals(vh.userName, userName);
        Assertions.assertNotNull(vh.claimedAt);

        Assertions.assertEquals(1, vh.voucher.qtyClaimed);

    }

    // @Test
    // void testClaimVoucher_WithKafkaCompanion() {
    //     Assertions.assertEquals(1, Voucher.count());
    //     Assertions.assertEquals(1, VoucherHistory.count());

    //     VoucherHistory vh = VoucherHistory.findAll().firstResult();
    //     Assertions.assertEquals(VoucherHistory.Type.AVAILABLE, vh.type);
    //     Assertions.assertNotEquals(vh.userId, userId);
    //     Assertions.assertNull(vh.claimedAt);
    //     Assertions.assertNull(vh.userName);

    //     Assertions.assertEquals(0, vh.voucher.qtyClaimed);

    //     ProducerTask producerTask = kafkaCompanion.produceStrings()
    //             .fromRecords(new ProducerRecord<>(topicClaimVoucher, new JsonObject()
    //                     .put("voucherId", vh.voucher.id)
    //                     .put("userId", userId)
    //                     .put("userName", RandomString.make())
    //                     .encode()));
    //     producerTask.awaitCompletion();

    //     ConsumerTask<String, String> consuming = kafkaCompanion.consumeStrings().fromTopics(topicClaimVoucher, 1);
    //     consuming.awaitCompletion();
    //     Assertions.assertEquals(1, consuming.count());

    //     VoucherHistory.getEntityManager().clear();
    //     vh = VoucherHistory.findById(vh.id);

    //     Assertions.assertEquals(VoucherHistory.Type.CLAIMED, vh.type);
    //     Assertions.assertEquals(vh.userId, userId);
    //     Assertions.assertEquals(vh.userName, userName);
    //     Assertions.assertNotNull(vh.claimedAt);

    //     Assertions.assertEquals(1, vh.voucher.qtyClaimed);

    // }

}
