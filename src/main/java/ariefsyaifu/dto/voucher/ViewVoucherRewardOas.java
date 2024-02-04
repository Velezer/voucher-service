package ariefsyaifu.dto.voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ariefsyaifu.dto.OutletDto;
import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.VoucherOutlet;

public class ViewVoucherRewardOas {

    public String id;

    public String prefixCode;

    public String name;

    public Voucher.Type type;

    public BigDecimal amount;

    public Voucher.TransactionType transactionType;

    public BigDecimal quota;

    public BigDecimal usedQuota;

    public BigDecimal maxDiscount;

    public Voucher.ModeType modeType;

    public Voucher.UsedDayType usedDayType;

    public LocalDateTime validFrom;

    public LocalDateTime validTo;

    public String imageUrl;

    public String detail;

    public BigDecimal minSubtotal;

    public Integer maxRedeemedCount;

    public Integer extendValidToInDays;

    public Long qtyClaim;

    public List<OutletDto> outlets;

    public static List<ViewVoucherRewardOas> valueOf(
            List<ViewVoucherRewardOas> vouchers,
            Map<String, List<VoucherOutlet>> mapVos) {
        return vouchers.stream().map(oas -> {
            oas.outlets = Optional.ofNullable(mapVos.get(oas.id)).orElse(List.of())
                    .stream()
                    .map(vo -> OutletDto.valueOf(vo))
                    .toList();
            return oas;
        }).toList();
    }

}
