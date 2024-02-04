package ariefsyaifu.service.backoffice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ariefsyaifu.dao.backoffice.BackOfficeVoucherDao;
import ariefsyaifu.dto.ViewCountOas;
import ariefsyaifu.dto.voucher.backoffice.CreateVoucherRequestBody;
import ariefsyaifu.dto.voucher.backoffice.UpdateVoucherRequestBody;
import ariefsyaifu.dto.voucher.backoffice.ViewVoucherIdOas;
import ariefsyaifu.dto.voucher.backoffice.ViewVoucherOas;
import ariefsyaifu.model.CombinationUniqueCode;
import ariefsyaifu.model.Voucher;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BackOfficeVoucherService {
    private static final Logger logger = LoggerFactory.getLogger(BackOfficeVoucherService.class);

    @Inject
    public BackOfficeVoucherService(
            BackOfficeVoucherDao backOfficeVoucherDAO) {
        this.backOfficeVoucherDAO = backOfficeVoucherDAO;
    }

    private BackOfficeVoucherDao backOfficeVoucherDAO;

    public ViewVoucherIdOas create(CreateVoucherRequestBody request, String userId) {
        if (!request.prefixCode.equals(request.prefixCode.toUpperCase())) {
            throw new HttpException(400, "PREFIX_CODE_MUST_BE_UPPER_CASE");
        }
        Voucher v = backOfficeVoucherDAO.create(request, userId);
        return ViewVoucherIdOas.valueOf(v);
    }

    public void update(String id, UpdateVoucherRequestBody request, String userId) {
        backOfficeVoucherDAO.update(id, request, userId);
    }

    public ViewVoucherOas getById(String id) {
        Voucher c = Voucher.findById(id);
        if (c == null) {
            throw new HttpException(404, "VOUCHER_NOT_FOUND");
        }
        if (c.deletedAt != null) {
            throw new HttpException(404, "VOUCHER_NOT_FOUND");
        }
        return ViewVoucherOas.valueOf(c);
    }

    public List<ViewVoucherOas> list(
            String search,
            String orderBy,
            boolean isAscending,
            int offset,
            int limit) {
        if (limit < 1) {
            throw new HttpException(404, "LIMIT_INVALID");
        }
        if (limit > 1000) {
            throw new HttpException(404, "LIMIT_INVALID");
        }

        List<Voucher> vouchers = backOfficeVoucherDAO.list(search, orderBy, isAscending, offset, limit);
        return vouchers.stream().map(v -> ViewVoucherOas.valueOf(v)).toList();
    }

    public ViewCountOas count(String search) {
        long count = backOfficeVoucherDAO.countList(search).longValue();
        return ViewCountOas.valueOf(count);
    }

    public void deleteById(String id, String userId) {
        backOfficeVoucherDAO.delete(id, userId);
    }

    public ViewCountOas generateCombinationCodes() {
        return ViewCountOas.valueOf(backOfficeVoucherDAO.generateCombinationCodes());
    }

    public ViewCountOas countCombinationCodes() {
        return ViewCountOas.valueOf(CombinationUniqueCode.count());
    }

}
