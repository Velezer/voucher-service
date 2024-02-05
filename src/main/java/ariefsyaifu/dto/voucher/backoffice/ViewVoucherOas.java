package ariefsyaifu.dto.voucher.backoffice;

import ariefsyaifu.dto.OutletDto;
import ariefsyaifu.model.Voucher;

public class ViewVoucherOas extends CreateVoucherRequestBody {
    public String id;

    public static ViewVoucherOas valueOf(Voucher v) {
        ViewVoucherOas oas = new ViewVoucherOas();
        oas.id = v.id;
        oas.prefixCode = v.prefixCode;
        oas.name = v.name;
        oas.type = v.type;
        oas.amount = v.amount;
        oas.transactionType = v.transactionType;
        oas.quota = v.quota;
        oas.maxDiscount = v.maxDiscount;
        oas.modeType = v.modeType;
        oas.minSubtotal = v.minSubtotal;
        oas.maxRedeemedCount = v.maxRedeemedCount;
        oas.usedDayType = v.usedDayType;
        oas.validFrom = v.validFrom;
        oas.validTo = v.validTo;
        oas.imageUrl = v.imageUrl;
        oas.detail = v.detail;
        oas.qtyClaim = v.qtyClaim;
        oas.qtyRedeem = v.qtyRedeem;
        oas.extendValidToInDays = v.extendValidToInDays;
        oas.status = v.status;
        oas.outlets = v.outlets.stream().map(o -> OutletDto.valueOf(o)).toList();
        oas.tags = v.tags.stream().map(o -> o.tagId).toList();
        oas.tiers = v.tiers.stream().map(o -> o.tierId).toList();
        return oas;
    }
}
