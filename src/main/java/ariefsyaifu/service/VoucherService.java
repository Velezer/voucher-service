package ariefsyaifu.service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ariefsyaifu.dao.VoucherDao;
import ariefsyaifu.dto.voucher.ViewClaimVoucherOas;
import ariefsyaifu.dto.voucher.ViewVoucherRewardOas;
import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.VoucherHistory;
import ariefsyaifu.model.VoucherOutlet;
import ariefsyaifu.util.DateUtil;
import ariefsyaifu.util.FormatUtil;
import io.quarkus.panache.common.Parameters;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@ApplicationScoped
public class VoucherService {
    private static final Logger logger = LoggerFactory.getLogger(VoucherService.class);

    @Inject
    public VoucherService(
            VoucherDao voucherDAO,
            TagService tagService,
            EntityManager em

    ) {
        this.voucherDAO = voucherDAO;
        this.tagService = tagService;
        this.em = em;
    }

    private TagService tagService;
    private VoucherDao voucherDAO;
    private EntityManager em;

    public List<ViewVoucherRewardOas> list(JsonObject joCustomId, String search) throws ParseException {
        String userId = joCustomId.getString("userId");
        String dob = joCustomId.getString("dob"); // yyyy-MM-dd
        String tierId = joCustomId.getString("tierId");

        boolean isWithinBirthday = this.isWithinBirthday(dob, DateUtil.now());
        boolean hasRedeemedAnyVoucher = voucherDAO.hasRedeemedAnyVoucher(userId);

        List<String> tagIds = tagService.getTagIds(userId);

        List<ViewVoucherRewardOas> vouchers = voucherDAO.findActiveVouchers(
                hasRedeemedAnyVoucher,
                isWithinBirthday,
                tagIds,
                tierId,
                search);

        vouchers = vouchers.stream()
                .filter(v -> v.usedQuota.compareTo(v.quota) >= 0)
                .collect(Collectors.toList());

        List<String> filteredVoucherIds = vouchers.stream().map(v -> v.id).collect(Collectors.toList());

        List<VoucherHistory> vhs = VoucherHistory.find("userId = ?1 and voucher.id in ?2", userId, filteredVoucherIds)
                .list();

        Map<String, Integer> mapClaimed = vhs.stream()
                .filter(vh -> vh.isClaimed())
                .map(o -> o.voucher.id)
                .collect(Collectors.toMap(o -> o, o -> 1, (v1, v2) -> v1 + v2));
        Map<String, Integer> mapRedeemed = vhs.stream()
                .filter(vh -> vh.isRedeemed())
                .map(o -> o.voucher.id)
                .collect(Collectors.toMap(o -> o, o -> 1, (v1, v2) -> v1 + v2));

        vouchers = vouchers.stream()
                .filter(v -> {
                    int countRedeemed = Optional.ofNullable(mapRedeemed.get(v.id)).orElse(0);
                    return countRedeemed < v.maxRedeemedCount;
                })
                .filter(v -> {
                    int countClaimed = Optional.ofNullable(mapClaimed.get(v.id)).orElse(0);
                    if (countClaimed > 0) {
                        v.validTo = v.validTo.plusDays(v.extendValidToInDays);
                    }
                    if (v.qtyClaim <= 0) {
                        return countClaimed < 1;
                    }
                    return true;
                })
                .sorted((v1, v2) -> { // reverse (big max discount first)
                    if (v1.maxDiscount == null && v2.maxDiscount == null) {
                        return 0;
                    }
                    if (v1.maxDiscount != null && v2.maxDiscount == null) {
                        return -1;
                    }
                    if (v1.maxDiscount == null && v2.maxDiscount != null) {
                        return 1;
                    }
                    return v2.maxDiscount.compareTo(v1.maxDiscount);
                })
                .distinct()
                .collect(Collectors.toList());

        List<VoucherOutlet> vos = VoucherOutlet.find("voucher.id in ?1", filteredVoucherIds).list();
        Map<String, List<VoucherOutlet>> mapVos = vos.stream()
                .collect(Collectors.groupingBy(
                        vo -> vo.voucher.id));

        return ViewVoucherRewardOas.valueOf(vouchers, mapVos);
    }

    public ViewClaimVoucherOas claim(String voucherId, JsonObject joCustomId) {
        String userId = Optional.ofNullable(joCustomId.getString("userId")).orElse("");
        String userName = joCustomId.getString("userName");
        String tierId = joCustomId.getString("tierId");

        if (userId.isBlank()) {
            throw new HttpException(403, "FORBIDDEN");
        }

        List<String> tagIds = tagService.getTagIds(userId);

        VoucherHistory vh = voucherDAO.claim(voucherId, userId, userName, tierId, tagIds);
        return ViewClaimVoucherOas.valueOf(vh);
    }

    public Boolean isWithinBirthday(String sDob, LocalDateTime now) throws ParseException {
        if (sDob == null) {
            return false;
        }
        if (sDob.isBlank()) {
            return false;
        }
        if (now == null) {
            now = DateUtil.now();
        }
        LocalDateTime dobUser = DateUtil.toLocalDateTime(sDob, "yyyy-MM-dd");

        LocalDateTime minNow = now.minusDays(7);
        LocalDateTime maxNow = now.plusDays(6);

        if (maxNow.getYear() > minNow.getYear() && dobUser.getMonthValue() == 1) {
            dobUser = dobUser.withYear(maxNow.getYear());
        } else {
            dobUser = dobUser.withYear(minNow.getYear());
        }

        return minNow.isBefore(dobUser) && dobUser.isBefore(maxNow);
    }

}
