package ariefsyaifu.service.backoffice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ariefsyaifu.dao.backoffice.BackOfficeVoucherHistoryDao;
import ariefsyaifu.dto.voucher.backoffice.ViewVoucherHistoryOas;
import ariefsyaifu.model.VoucherHistory;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BackOfficeVoucherHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(BackOfficeVoucherHistoryService.class);

    @Inject
    public BackOfficeVoucherHistoryService(
            BackOfficeVoucherHistoryDao backOfficeVoucherHistoryDao) {
        this.backOfficeVoucherHistoryDao = backOfficeVoucherHistoryDao;
    }

    private BackOfficeVoucherHistoryDao backOfficeVoucherHistoryDao;

    public List<ViewVoucherHistoryOas> list(
            String voucherId,
            String search,
            int limit,
            int offset,
            boolean isAscending,
            String orderBy) {
        if (limit < 1) {
            throw new HttpException(404, "LIMIT_INVALID");
        }
        if (limit > 1000) {
            throw new HttpException(404, "LIMIT_INVALID");
        }
        List<VoucherHistory> vhs = backOfficeVoucherHistoryDao.list(voucherId, search, orderBy, isAscending, offset, limit);
        return vhs.stream().map(vh -> ViewVoucherHistoryOas.valueOf(vh)).toList();
    }

}
