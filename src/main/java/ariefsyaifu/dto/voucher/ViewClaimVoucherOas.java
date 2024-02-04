package ariefsyaifu.dto.voucher;

import ariefsyaifu.model.VoucherHistory;

public class ViewClaimVoucherOas {
    public String voucherCode;

    public static ViewClaimVoucherOas valueOf(VoucherHistory vh) {
        ViewClaimVoucherOas oas = new ViewClaimVoucherOas();
        oas.voucherCode = vh.voucherCode;
        return oas;
    }
}
