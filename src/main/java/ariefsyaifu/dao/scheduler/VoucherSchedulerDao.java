package ariefsyaifu.dao.scheduler;

import java.util.List;

import ariefsyaifu.model.Voucher;
import ariefsyaifu.util.DateUtil;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VoucherSchedulerDao {

    public VoucherSchedulerDao(
            EntityManager em) {
        this.em = em;
    }

    private EntityManager em;

    @Transactional
    public void inactiveExpiredVoucher() {
        StringBuilder sb = new StringBuilder();
        Parameters params = new Parameters();
        sb.append("""
                select 
                v.* 
                from voucher_schema.voucher v
                where true
                and v.deleted_at is null
                and v.status = 'ACTIVE'
                and :now > v.valid_to + make_interval(0,0,0, v.extend_valid_to_in_days)
                """);
        params.and("now", DateUtil.now());

        Query query = em.createNativeQuery(sb.toString(), Voucher.class);
        params.map().forEach(query::setParameter);

        List<Voucher> vouchers = query.getResultList();

        for (Voucher v : vouchers) {
            v.status = Voucher.Status.INACTIVE;
            v.persist();
        }
    }

}
