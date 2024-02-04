package ariefsyaifu.dto.voucher.external;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.VoucherHistory;

public class ViewClaimedVoucherExternalOas {
    public String voucherCode;

    public LocalDateTime validTo;

    public BigDecimal minSubtotal;

    public BigDecimal amount;

    public Voucher.Type type;

    public String voucherName;

    public static ViewClaimedVoucherExternalOas valueOf(VoucherHistory vh) {
        ViewClaimedVoucherExternalOas oas = new ViewClaimedVoucherExternalOas();
        oas.voucherCode = vh.voucherCode;
        oas.validTo = vh.voucher.validTo;
        oas.minSubtotal = vh.voucher.minSubtotal;
        oas.amount = vh.voucher.amount;
        oas.type = vh.voucher.type;
        oas.voucherName = vh.voucher.name;
        return oas;

    }

}
