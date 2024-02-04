package ariefsyaifu.dao.backoffice;

import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.math.BigInteger;
import java.util.List;

import ariefsyaifu.model.VoucherHistory;

@ApplicationScoped
public class BackOfficeVoucherHistoryDao {

    @Inject
    public BackOfficeVoucherHistoryDao(
            EntityManager em) {
        this.em = em;
    }

    private EntityManager em;

    public List<VoucherHistory> list(
            String voucherId,
            String search,
            String orderBy,
            boolean isAscending,
            int offset,
            int limit) {
        Parameters params = new Parameters();
        params.and("voucherId", voucherId);
        StringBuilder sb = new StringBuilder("""
                    select
                    vh.*
                    from voucher_schema.voucher_history vh
                    where true
                    and vh.deleted_at is null
                    and vh.voucher_id = :voucherId
                """);
        if (!search.isBlank()) {
            params.and("search", "%" + String.join("%", search.split(" ")) + "%");
            sb.append("""
                        and
                        coalesce(vh.id, '') || ' ' ||
                        coalesce(vh.user_id, '') || ' ' ||
                        coalesce(vh.user_code, '') || ' ' ||
                        coalesce(vh.user_name, '') || ' ' ||
                        coalesce(vh.transaction_id, '') || ' ' ||
                        coalesce(vh.voucher_code, '')
                        ilike :search
                    """);
        }
        if (!orderBy.isBlank()) {
            if (!orderBy.isBlank()) {
                sb.append("order by ");
                sb.append(orderBy.equalsIgnoreCase("createdAt") ? "vh.created_at" : "vh.updated_at");
                sb.append(" ");
                sb.append(isAscending ? "asc" : "desc");
            }
        }

        Query q = em.createNativeQuery(sb.toString(), VoucherHistory.class);
        params.map().forEach(q::setParameter);
        q.setFirstResult(offset);
        q.setMaxResults(limit);

        List<VoucherHistory> resultList = q.getResultList();
        return resultList;
    }

    public BigInteger countList(String voucherId, String search) {
        Parameters params = new Parameters();
        params.and("voucherId", voucherId);
        StringBuilder sb = new StringBuilder("""
                    select
                    count(1)
                    from voucher_schema.voucher v
                    where true
                    and vh.deleted_at is null
                    and vh.voucher_id = :voucherId
                """);
        if (!search.isBlank()) {
            params.and("search", "%" + search + "%");
            sb.append("""
                    and
                    coalesce(vh.id, '') || ' ' ||
                    coalesce(vh.user_id, '') || ' ' ||
                    coalesce(vh.user_code, '') || ' ' ||
                    coalesce(vh.user_name, '') || ' ' ||
                    coalesce(vh.transaction_id, '') || ' ' ||
                    coalesce(vh.voucher_code, '')
                    ilike :search
                                """);
        }
        Query q = em.createNativeQuery(sb.toString(), BigInteger.class);
        params.map().forEach(q::setParameter);
        return (BigInteger) q.getSingleResult();
    }

}
