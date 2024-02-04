package ariefsyaifu.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.VoucherHistory;
import ariefsyaifu.model.Voucher.ModeType;
import ariefsyaifu.model.Voucher.Status;
import ariefsyaifu.model.Voucher.TransactionType;
import ariefsyaifu.model.Voucher.Type;
import ariefsyaifu.model.Voucher.UsedDayType;
import ariefsyaifu.util.DateUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.vertx.core.json.JsonObject;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;

@QuarkusTest
class VoucherController_MaxRedeemedCount_Test {

    String userId = RandomString.make();

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
        v.qtyClaim = 2l;
        v.qtyClaimed = 0l;
        v.qtyRedeem = 2l;
        v.qtyRedeemed = 0l;
        v.extendValidToInDays = 0;
        v.status = Status.ACTIVE;
        v.persist();

        VoucherHistory vhRedeemed = new VoucherHistory();
        vhRedeemed.type = VoucherHistory.Type.REDEEMED;
        vhRedeemed.userId = userId;
        vhRedeemed.voucher = v;
        vhRedeemed.voucherCode = RandomString.make();
        vhRedeemed.persist();

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
    void testClaim_MaxRedeemedCount() {
        Assertions.assertEquals(1, Voucher.count());
        Assertions.assertEquals(2, VoucherHistory.count());

        Voucher v = Voucher.findAll().firstResult();
        Assertions.assertEquals(1, v.maxRedeemedCount);

        Response rd = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("X-Consumer-Custom-ID", new JsonObject().put("userId", userId).encode())
                .post("/api/v1/voucher/{id}/claim", v.id)
                .andReturn();

        Assertions.assertEquals(400, rd.getStatusCode());
        JsonObject rdas = new JsonObject(rd.asString());

        Assertions.assertEquals("CANNOT_CLAIM_ANYMORE", rdas.getString("message"));
    }

}
