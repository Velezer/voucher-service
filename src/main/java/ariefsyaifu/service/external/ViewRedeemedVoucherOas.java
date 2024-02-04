package ariefsyaifu.service.external;

import java.math.BigDecimal;

import ariefsyaifu.model.VoucherHistory;

public class ViewRedeemedVoucherOas {

    public BigDecimal voucherAmount;

    public static ViewRedeemedVoucherOas valueOf(VoucherHistory redeemed) {
        ViewRedeemedVoucherOas oas = new ViewRedeemedVoucherOas();
        oas.voucherAmount = redeemed.voucherAmount;
        return oas;
    }

}
