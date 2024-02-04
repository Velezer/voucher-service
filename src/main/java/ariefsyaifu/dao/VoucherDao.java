package ariefsyaifu.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ariefsyaifu.dto.voucher.ViewVoucherRewardOas;
import ariefsyaifu.dto.voucher.backoffice.ViewVoucherOas;
import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.Voucher.UsedDayType;
import ariefsyaifu.model.VoucherHistory;
import ariefsyaifu.util.DateUtil;
import ariefsyaifu.util.FormatUtil;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import io.quarkus.panache.common.Parameters;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VoucherDao {

    public VoucherDao(
            EntityManager em) {
        this.em = em;
    }

    private EntityManager em;

    public boolean hasRedeemedAnyVoucher(String userId) {
        if (Optional.ofNullable(userId).orElse("").isBlank()) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select
                1
                from voucher_schema.voucher_history vh
                where true
                and vh.deleted_at is null
                and vh.user_id = :userId
                and vh.type = 'REDEEMED'
                limit 1
                        """);
        Query query = em.createNativeQuery(sb.toString())
                .setParameter("userId", userId);
        return query.getResultList().size() == 1;
    }

    @Transactional
    @TransactionConfiguration(timeout = 30)
    public VoucherHistory claim(String vid, String userId, String userName) {
        Voucher v = Voucher.findById(vid, LockModeType.PESSIMISTIC_WRITE);
        if (v == null) {
            throw new HttpException(400, "VOUCHER_NOT_FOUND");
        }
        if (!v.isActive()) {
            throw new HttpException(400, "VOUCHER_NOT_ACTIVE");
        }
        if (v.qtyClaimed >= v.qtyClaim) {
            throw new HttpException(400, "VOUCHER_RUNNING_OUT");
        }

        VoucherHistory vhClaimed = VoucherHistory
                .find("voucher.id = ?1 and type='CLAIMED'", v.id)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
        if (vhClaimed != null) {
            return vhClaimed;
        }

        VoucherHistory vh = VoucherHistory
                .find("voucher.id = ?1 and type='AVAILABLE'", v.id)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
        if (vh == null) {
            throw new HttpException(400, "RUNNING_OUT");
        }

        long count = VoucherHistory.find("""
                deletedAt is null
                and voucher.id = ?1
                and type in ('REDEEMED','CLAIMED')
                and userId = ?2
                    """, v.id, userId)
                .count();
        if (count >= v.maxRedeemedCount) {
            throw new HttpException(400, "CANNOT_CLAIM_ANYMORE");
        }
        v.qtyClaimed += 1;
        v.persist();

        vh.userId = userId;
        vh.userName = userName;
        vh.type = VoucherHistory.Type.CLAIMED;
        vh.claimedAt = DateUtil.now();
        vh.persist();
        return vh;

    }

    public List<ViewVoucherRewardOas> findActiveVouchers(
            boolean hasRedeemedAnyVoucher,
            boolean isWithinBirthday,
            List<String> tagIds,
            String tierId,
            String search) {
        StringBuilder sb = new StringBuilder();
        Parameters params = new Parameters();
        sb.append("""
                select
                v.id,
                v.prefix_code,
                v.name,
                v.type,
                v.amount,
                v.transaction_type,
                v.quota,
                v.used_quota,
                v.max_discount,
                v.mode_type,
                v.min_subtotal,
                v.max_redeemed_count,
                v.used_day_type,
                v.valid_from,
                v.valid_to,
                v.image_url,
                v.detail,
                v.extend_valid_to_in_days,
                v.qty_claim
                from voucher_schema.voucher v
                left join voucher_schema.voucher_tier vt on vt.voucher_id = v.id
                left join voucher_schema.voucher_tag vtg on vtg.voucher_id = v.id
                where true
                and v.deleted_at is null
                and status = 'ACTIVE'
                and :now  between v.valid_from and (v.valid_to + MAKE_INTERVAL(0,0,0, v.extend_valid_to_in_days))
                """);
        params.and("now", DateUtil.now());
        if (hasRedeemedAnyVoucher) {
            sb.append("and v.transaction_type in ('TRANSACTION') ");
        }
        if (!isWithinBirthday) {
            sb.append("and v.used_day_type in ('EVERYDAY') ");
        }
        sb.append("""
                and (vt is null or vt.tier_id = :tierId)
                and (vtg is null or vtg.tag_id in :tagIds)
                """);

        params.and("tierId", tierId);
        params.and("tagIds", tagIds);

        if (FormatUtil.isSearchInput(search, false)) {
            sb.append("""
                    and (
                        v.name ilike :search
                        )
                            """);
            params.and("search", "%" + search + "%");
        }

        Query query = em.createNativeQuery(sb.toString());
        params.map().forEach(query::setParameter);

        List<Object[]> resuList = query.getResultList();

        return resuList.stream().map(o -> {
            ViewVoucherRewardOas oas = new ViewVoucherRewardOas();
            oas.id = (String) o[0];
            oas.prefixCode = (String) o[1];
            oas.name = (String) o[2];
            oas.type = o[3] == null ? null : Voucher.Type.valueOf((String) o[3]);
            oas.amount = (BigDecimal) o[4];
            oas.transactionType = o[5] == null ? null : Voucher.TransactionType.valueOf((String) o[5]);
            oas.quota = (BigDecimal) o[6];
            oas.usedQuota = (BigDecimal) o[7];
            oas.maxDiscount = (BigDecimal) o[8];
            oas.modeType = o[9] == null ? null : Voucher.ModeType.valueOf((String) o[9]);
            oas.minSubtotal = (BigDecimal) o[10];
            oas.maxRedeemedCount = o[11] == null ? null : ((BigInteger) o[11]).intValue();
            oas.usedDayType = o[12] == null ? null : Voucher.UsedDayType.valueOf((String) o[12]);
            oas.validFrom = o[13] == null ? null : ((Timestamp) o[13]).toLocalDateTime();
            oas.validTo = o[14] == null ? null : ((Timestamp) o[14]).toLocalDateTime();
            oas.imageUrl = (String) o[15];
            oas.detail = (String) o[16];
            oas.extendValidToInDays = o[17] == null ? null : ((BigInteger) o[17]).intValue();
            oas.qtyClaim = o[18] == null ? null : ((BigInteger) o[18]).longValue();
            return oas;
        }).toList();
    }

}
