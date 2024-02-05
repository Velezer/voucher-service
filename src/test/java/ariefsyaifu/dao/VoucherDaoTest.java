package ariefsyaifu.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.awaitility.Awaitility;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ariefsyaifu.dto.voucher.ViewVoucherRewardOas;
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
class VoucherDaoTest {

    @Inject
    VoucherDao voucherDao;

    @Inject
    ManagedExecutor me;

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
        v.qtyClaim = 2l;
        v.qtyClaimed = 0l;
        v.qtyRedeem = 2l;
        v.qtyRedeemed = 0l;
        v.extendValidToInDays = 0;
        v.status = Status.ACTIVE;
        v.persist();

        for (int i = 0; i < 2; i++) {
            VoucherHistory vh = new VoucherHistory();
            vh.type = VoucherHistory.Type.AVAILABLE;
            vh.voucher = v;
            vh.voucherCode = RandomString.make();
            vh.persist();
        }

    }

    @AfterEach
    @Transactional
    void afterEach() {
        VoucherHistory.deleteAll();
        Voucher.deleteAll();
    }

    @Test
    void testFindActiveVouchers(){
        List<ViewVoucherRewardOas> vouchers = voucherDao.findActiveVouchers(false, false, null, null, null);
        Assertions.assertEquals(1, vouchers.size());
        vouchers = voucherDao.findActiveVouchers(false, false, List.of(RandomString.make()), null, null);
        Assertions.assertEquals(1, vouchers.size());
        vouchers = voucherDao.findActiveVouchers(false, false, null, RandomString.make(), null);
        Assertions.assertEquals(1, vouchers.size());
        vouchers = voucherDao.findActiveVouchers(false, false, List.of(RandomString.make()), RandomString.make(), null);
        Assertions.assertEquals(1, vouchers.size());
    }

    @Test
    void testClaim_RaceCondition() {
        Assertions.assertEquals(1, Voucher.count());
        Assertions.assertEquals(2, VoucherHistory.count("type='AVAILABLE'"));
        Voucher v = Voucher.findAll().firstResult();
        Assertions.assertEquals(0, v.qtyClaimed);

        AtomicInteger count = new AtomicInteger();
        int attempt = 4;
        String vid = v.id;
        for (int i = 0; i < attempt; i++) {
            me.execute(() -> {
                voucherDao.claim(vid, userId, userName, null, null, false);
                count.incrementAndGet();
            });
        }

        Awaitility.await().until(() -> count.get() == attempt);

        Assertions.assertEquals(1, VoucherHistory.count("type='AVAILABLE'"));
        Assertions.assertEquals(1, VoucherHistory.count("type='CLAIMED'"));

        Voucher.getEntityManager().clear();
        Assertions.assertEquals(1, Voucher.count());
        v = Voucher.findAll().firstResult();
        Assertions.assertEquals(1, v.qtyClaimed);

    }
}
