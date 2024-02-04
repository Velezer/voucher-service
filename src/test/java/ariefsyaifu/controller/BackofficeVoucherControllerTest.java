package ariefsyaifu.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ariefsyaifu.dto.voucher.backoffice.ViewVoucherOas;
import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.Voucher.ModeType;
import ariefsyaifu.model.Voucher.Status;
import ariefsyaifu.model.Voucher.TransactionType;
import ariefsyaifu.model.Voucher.Type;
import ariefsyaifu.model.Voucher.UsedDayType;
import ariefsyaifu.util.DateUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;

@QuarkusTest
class BackofficeVoucherControllerTest {

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
    }

    @AfterEach
    @Transactional
    void afterEach() {
        Voucher.deleteAll();
    }

    @Test
    void testList() {
        Assertions.assertEquals(1, Voucher.count());
        Voucher v = Voucher.findAll().firstResult();

        Response r = RestAssured
                .get("/api/v1/backoffice/voucher")
                .andReturn();

        Assertions.assertEquals(200, r.getStatusCode());

        ViewVoucherOas[] ras = r.as(ViewVoucherOas[].class);
        Assertions.assertEquals(1, ras.length);

        Assertions.assertEquals(v.id, ras[0].id);
        Assertions.assertEquals(v.prefixCode, ras[0].prefixCode);
        Assertions.assertEquals(v.name, ras[0].name);
        Assertions.assertEquals(v.type, ras[0].type);
        Assertions.assertEquals(v.amount, ras[0].amount);
        Assertions.assertEquals(v.transactionType, ras[0].transactionType);
        Assertions.assertEquals(v.quota, ras[0].quota);
        Assertions.assertEquals(v.maxDiscount, ras[0].maxDiscount);
        Assertions.assertEquals(v.modeType, ras[0].modeType);
        Assertions.assertEquals(v.minSubtotal, ras[0].minSubtotal);
        Assertions.assertEquals(v.maxRedeemedCount, ras[0].maxRedeemedCount);
        Assertions.assertEquals(v.usedDayType, ras[0].usedDayType);
        Assertions.assertEquals(v.validFrom, ras[0].validFrom);
        Assertions.assertEquals(v.validTo, ras[0].validTo);
        Assertions.assertEquals(v.imageUrl, ras[0].imageUrl);
        Assertions.assertEquals(v.detail, ras[0].detail);
        Assertions.assertEquals(v.qtyClaim, ras[0].qtyClaim);
        Assertions.assertEquals(v.qtyRedeem, ras[0].qtyRedeem);
        Assertions.assertEquals(v.extendValidToInDays, ras[0].extendValidToInDays);
        Assertions.assertEquals(v.status, ras[0].status);

    }

    @Test
    void testDetail() {
        Assertions.assertEquals(1, Voucher.count());
        Voucher v = Voucher.findAll().firstResult();

        Response r = RestAssured
                .get("/api/v1/backoffice/voucher/" + v.id)
                .andReturn();

        Assertions.assertEquals(200, r.getStatusCode());

        ViewVoucherOas ras = r.as(ViewVoucherOas.class);

        Assertions.assertEquals(v.id, ras.id);
        Assertions.assertEquals(v.prefixCode, ras.prefixCode);
        Assertions.assertEquals(v.name, ras.name);
        Assertions.assertEquals(v.type, ras.type);
        Assertions.assertEquals(v.amount, ras.amount);
        Assertions.assertEquals(v.transactionType, ras.transactionType);
        Assertions.assertEquals(v.quota, ras.quota);
        Assertions.assertEquals(v.maxDiscount, ras.maxDiscount);
        Assertions.assertEquals(v.modeType, ras.modeType);
        Assertions.assertEquals(v.minSubtotal, ras.minSubtotal);
        Assertions.assertEquals(v.maxRedeemedCount, ras.maxRedeemedCount);
        Assertions.assertEquals(v.usedDayType, ras.usedDayType);
        Assertions.assertEquals(v.validFrom, ras.validFrom);
        Assertions.assertEquals(v.validTo, ras.validTo);
        Assertions.assertEquals(v.imageUrl, ras.imageUrl);
        Assertions.assertEquals(v.detail, ras.detail);
        Assertions.assertEquals(v.qtyClaim, ras.qtyClaim);
        Assertions.assertEquals(v.qtyRedeem, ras.qtyRedeem);
        Assertions.assertEquals(v.extendValidToInDays, ras.extendValidToInDays);
        Assertions.assertEquals(v.status, ras.status);

    }

}
