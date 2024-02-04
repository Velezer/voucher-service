package ariefsyaifu.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ariefsyaifu.dao.scheduler.VoucherSchedulerDao;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VoucherScheduler {
	private static final Logger logger = LoggerFactory.getLogger(VoucherScheduler.class);

	@Inject
	public VoucherScheduler(
			VoucherSchedulerDao voucherSchedulerDao) {
		this.voucherSchedulerDao = voucherSchedulerDao;
	}

	private VoucherSchedulerDao voucherSchedulerDao;

	@Scheduled(every = "1h", delay = 1)
	public void inactiveExpiredVouchers() {
		voucherSchedulerDao.inactiveExpiredVoucher();
	}

}
