package ariefsyaifu.dao.backoffice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ariefsyaifu.dto.voucher.backoffice.CreateVoucherRequestBody;
import ariefsyaifu.dto.voucher.backoffice.UpdateVoucherRequestBody;
import ariefsyaifu.model.CombinationUniqueCode;
import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.VoucherHistory;
import ariefsyaifu.model.VoucherOutlet;
import ariefsyaifu.model.VoucherTag;
import ariefsyaifu.model.VoucherTier;
import ariefsyaifu.util.DateUtil;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import io.quarkus.panache.common.Parameters;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BackOfficeVoucherDao {

    @Inject
    public BackOfficeVoucherDao(EntityManager em) {
        this.em = em;
    }

    EntityManager em;

    @Transactional
    public Voucher create(CreateVoucherRequestBody request, String userId) {
        if (Voucher.find("prefixCode = ?1", request.prefixCode).firstResultOptional().isPresent()) {
            throw new HttpException(409, "DUPLICATED");
        }
        Voucher v = new Voucher();
        v.prefixCode = request.prefixCode;
        v.name = request.name;
        v.type = request.type;
        v.amount = request.amount;
        v.transactionType = request.transactionType;
        v.quota = request.quota;
        v.usedQuota = BigDecimal.ZERO;
        v.maxDiscount = request.maxDiscount;
        v.modeType = request.modeType;
        v.minSubtotal = request.minSubtotal;
        v.maxRedeemedCount = request.maxRedeemedCount;
        v.usedDayType = request.usedDayType;
        v.validFrom = request.validFrom;
        v.validTo = request.validTo;
        v.imageUrl = request.imageUrl;
        v.detail = request.detail;
        v.qtyClaim = request.qtyClaim;
        v.qtyRedeem = request.qtyRedeem;
        v.extendValidToInDays = request.extendValidToInDays;
        v.status = request.status;
        if (!Optional.ofNullable(request.outlets).orElse(List.of()).isEmpty()) {
            v.outlets = request.outlets.stream()
                    .map(dto -> {
                        VoucherOutlet vo = new VoucherOutlet();
                        vo.voucher = v;
                        vo.outletId = dto.id;
                        vo.outletName = dto.name;
                        return vo;
                    }).collect(Collectors.toList());
        }
        if (!Optional.ofNullable(request.tags).orElse(List.of()).isEmpty()) {
            v.tags = request.tags.stream()
                    .map(id -> {
                        VoucherTag vo = new VoucherTag();
                        vo.voucher = v;
                        vo.tagId = id;
                        return vo;
                    }).collect(Collectors.toList());
        }
        if (!Optional.ofNullable(request.tiers).orElse(List.of()).isEmpty()) {
            v.tiers = request.tiers.stream()
                    .map(id -> {
                        VoucherTier vo = new VoucherTier();
                        vo.voucher = v;
                        vo.tierId = id;
                        return vo;
                    }).collect(Collectors.toList());
        }

        v.createdBy = userId;
        v.persist();
        if (v.isActive()) {
            this.generateVoucherCodes(v.id, v.prefixCode, v.qtyClaim);
        }
        return v;
    }

    @Transactional
    public void update(String id, UpdateVoucherRequestBody request, String userId) {
        Voucher v = Voucher.findById(id);
        if (v == null) {
            throw new HttpException(404, "VOUCHER_NOT_FOUND");
        }

        VoucherTag.delete("voucher.id = ?1", id);
        VoucherOutlet.delete("voucher.id = ?1", id);
        VoucherTier.delete("voucher.id = ?1", id);

        v.name = request.name;
        v.type = request.type;
        v.amount = request.amount;
        v.transactionType = request.transactionType;
        v.quota = request.quota;
        v.usedQuota = BigDecimal.ZERO;
        v.maxDiscount = request.maxDiscount;
        v.modeType = request.modeType;
        v.minSubtotal = request.minSubtotal;
        v.maxRedeemedCount = request.maxRedeemedCount;
        v.usedDayType = request.usedDayType;
        v.validFrom = request.validFrom;
        v.validTo = request.validTo;
        v.imageUrl = request.imageUrl;
        v.detail = request.detail;
        v.extendValidToInDays = request.extendValidToInDays;
        if (v.isActive()) {
            this.generateVoucherCodes(v.id, v.prefixCode, v.qtyClaim);
        }

        v.status = request.status;
        if (!Optional.ofNullable(request.outlets).orElse(List.of()).isEmpty()) {
            v.outlets = request.outlets.stream()
                    .map(dto -> {
                        VoucherOutlet vo = new VoucherOutlet();
                        vo.voucher = v;
                        vo.outletId = dto.id;
                        vo.outletName = dto.name;
                        return vo;
                    }).collect(Collectors.toList());
        }
        if (!Optional.ofNullable(request.tags).orElse(List.of()).isEmpty()) {
            v.tags = request.tags.stream()
                    .map(tagId -> {
                        VoucherTag vo = new VoucherTag();
                        vo.voucher = v;
                        vo.tagId = tagId;
                        return vo;
                    }).collect(Collectors.toList());
        }
        if (!Optional.ofNullable(request.tiers).orElse(List.of()).isEmpty()) {
            v.tiers = request.tiers.stream()
                    .map(tierId -> {
                        VoucherTier vo = new VoucherTier();
                        vo.voucher = v;
                        vo.tierId = tierId;
                        return vo;
                    }).collect(Collectors.toList());
        }

        v.updatedBy = userId;
        v.persist();
    }

    public List<Voucher> list(
            String search,
            String orderBy,
            boolean isAscending,
            int offset,
            int limit) {
        Parameters params = new Parameters();
        StringBuilder sb = new StringBuilder("""
                    select
                    v.*
                    from voucher_schema.voucher v
                    where true
                    and v.deleted_at is null
                """);
        if (!search.isBlank()) {
            params.and("search", "%" + String.join("%", search.split(" ")) + "%");
            sb.append("""
                        and
                        coalesce(v.id, '') || ' ' ||
                        coalesce(v.name, '') || ' ' ||
                        coalesce(v.prefix_code, '')
                        ilike :search
                    """);
        }
        if (!orderBy.isBlank()) {
            if (!orderBy.isBlank()) {
                sb.append("order by ");
                sb.append(orderBy.equalsIgnoreCase("createdAt") ? "v.created_at" : "v.updated_at");
                sb.append(" ");
                sb.append(isAscending ? "asc" : "desc");
            }
        }

        Query q = em.createNativeQuery(sb.toString(), Voucher.class);
        params.map().forEach(q::setParameter);
        q.setFirstResult(offset);
        q.setMaxResults(limit);

        List<Voucher> resultList = q.getResultList();
        return resultList;
    }

    public BigInteger countList(String search) {
        Parameters params = new Parameters();
        StringBuilder sb = new StringBuilder("""
                    select
                    count(1)
                    from voucher_schema.voucher v
                    where true
                    and v.deleted_at is null
                """);
        if (!search.isBlank()) {
            params.and("search", "%" + search + "%");
            sb.append("""
                    and
                    coalesce(v.id, '') || ' ' ||
                    coalesce(v.name, '') || ' ' ||
                    coalesce(v.prefix_code, '')
                    ilike :search
                            """);
        }
        Query q = em.createNativeQuery(sb.toString(), BigInteger.class);
        params.map().forEach(q::setParameter);
        return (BigInteger) q.getSingleResult();
    }

    @Transactional
    public Voucher delete(String id, String userId) {
        Voucher v = Voucher.findById(id);
        if (v == null) {
            throw new HttpException(404, "VOUCHER_NOT_FOUND");
        }
        if (v.deletedAt != null) {
            throw new HttpException(404, "VOUCHER_NOT_FOUND");
        }
        v.deletedAt = DateUtil.now();
        v.deletedBy = userId;
        v.persist();
        return v;
    }

    public void generateVoucherCodes(String voucherId, String prefixCode, Long qtyClaim) {
        long countCuc = CombinationUniqueCode.count();
        if (countCuc < qtyClaim) {
            throw new HttpException(400, "COMBINATION_UNIQUE_CODE_LESS_THAN_QTY_CLAIM");
        }
        Long count = VoucherHistory.count("deletedAt is null and voucher.id = ?1", voucherId);
        Long qty = qtyClaim - count;
        if (qty <= 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(
                """
                            INSERT INTO voucher_schema.voucher_history
                            (id, created_at, type, voucher_id, voucher_code)
                            select gen_random_uuid(), now(), 'AVAILABLE', :voucherId, (:prefixCode || cuc.code)
                            from voucher_schema.combination_unique_code cuc
                            where cuc.code not in (
                                select vvh.voucher_code
                                from voucher_schema.voucher_history vvh
                                where vvh.voucher_id = :voucherId
                                )
                            order by random()
                            limit :qty
                        """);
        Query query = em.createNativeQuery(sb.toString())
                .setParameter("qty", qty)
                .setParameter("voucherId", voucherId)
                .setParameter("prefixCode", prefixCode);
        query.executeUpdate();
    }

    @Transactional
    @TransactionConfiguration(timeout = 300)
    public Long generateCombinationCodes() {

        // skip 1, o, 0
        // String text = "23456789abcdefghjklmnpqrstuvwxyz";
        // only number
        String text = "1234567890";
        int n = text.length();

        String s1 = null;
        String s2 = null;
        String s3 = null;
        String s4 = null;

        CombinationUniqueCode.deleteAll();

        for (int a = 0; a < n; a++) {
            s1 = String.valueOf(text.charAt(a));
            for (int b = 0; b < n; b++) {
                s2 = String.valueOf(text.charAt(b));
                for (int c = 0; c < n; c++) {
                    s3 = String.valueOf(text.charAt(c));
                    for (int d = 0; d < n; d++) {
                        s4 = String.valueOf(text.charAt(d));
                        CombinationUniqueCode code = new CombinationUniqueCode();
                        code.code = (s1 + s2 + s3 + s4).toUpperCase();
                        code.persist();
                    }
                }
            }
        }
        return CombinationUniqueCode.count();
    }

}
