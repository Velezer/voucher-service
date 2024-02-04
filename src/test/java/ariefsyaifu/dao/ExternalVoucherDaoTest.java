package ariefsyaifu.dao;

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
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;

@QuarkusTest
class ExternalVoucherDaoTest {

    @Inject
    ExternalVoucherDao externalVoucherDao;

    String userId = RandomString.make();
    String transactionId = RandomString.make();

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
        VoucherHistory vh = VoucherHistory.findAll().firstResult();
        Assertions.assertEquals(VoucherHistory.Type.CLAIMED, vh.type);
        Assertions.assertEquals(userId, vh.userId);
        Assertions.assertEquals(0, vh.voucher.qtyRedeemed);
        
        externalVoucherDao.redeem(vh.id, userId, vh.voucher.amount, transactionId);
        
        VoucherHistory.getEntityManager().clear();
        Assertions.assertEquals(1, VoucherHistory.count());
        vh = VoucherHistory.findAll().firstResult();
        Assertions.assertEquals(VoucherHistory.Type.REDEEMED, vh.type);
        Assertions.assertEquals(userId, vh.userId);
        Assertions.assertEquals(transactionId, vh.transactionId);
        Assertions.assertEquals(vh.voucher.amount, vh.voucherAmount);
        Assertions.assertEquals(1, vh.voucher.qtyRedeemed);

    }
}
