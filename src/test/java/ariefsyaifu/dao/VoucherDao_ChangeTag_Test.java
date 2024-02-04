package ariefsyaifu.dao;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.microprofile.context.ManagedExecutor;
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
import ariefsyaifu.model.VoucherTag;
import ariefsyaifu.util.DateUtil;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;

@QuarkusTest
class VoucherDao_ChangeTag_Test {

    @Inject
    VoucherDao voucherDao;

    @Inject
    ManagedExecutor me;

    String userId = RandomString.make();
    String userName = RandomString.make();

    String tagId0 = RandomString.make();
    String tagId1 = RandomString.make();

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

        VoucherTag vt = new VoucherTag();
        vt.voucher = v;
        vt.tagId = tagId0;
        vt.persist();

    }

    @AfterEach
    @Transactional
    void afterEach() {
        VoucherTag.deleteAll();
        VoucherHistory.deleteAll();
        Voucher.deleteAll();
    }

    @Test
    void testClaim_ChangeTier() {
        Assertions.assertEquals(1, Voucher.count());
        Assertions.assertEquals(2, VoucherHistory.count("type='AVAILABLE'"));
        Voucher v = Voucher.findAll().firstResult();
        Assertions.assertEquals(0, v.qtyClaimed);

        VoucherHistory vh0 = voucherDao.claim(v.id, userId, userName, null, List.of(tagId0));
        VoucherHistory vh1 = voucherDao.claim(v.id, userId, userName, null, List.of(tagId1));
        VoucherHistory vh2 = voucherDao.claim(v.id, userId, userName, null, List.of());
        VoucherHistory vh3 = voucherDao.claim(v.id, userId, userName, null, null);

        Assertions.assertEquals(vh0.id, vh1.id);
        Assertions.assertEquals(vh0.voucherCode, vh1.voucherCode);

        Assertions.assertEquals(vh0.id, vh2.id);
        Assertions.assertEquals(vh0.voucherCode, vh2.voucherCode);

        Assertions.assertEquals(vh0.id, vh3.id);
        Assertions.assertEquals(vh0.voucherCode, vh3.voucherCode);

        Assertions.assertEquals(1, VoucherHistory.count("type='AVAILABLE'"));
        Assertions.assertEquals(1, VoucherHistory.count("type='CLAIMED'"));

        Voucher.getEntityManager().clear();
        Assertions.assertEquals(1, Voucher.count());
        v = Voucher.findAll().firstResult();
        Assertions.assertEquals(1, v.qtyClaimed);

    }
}
