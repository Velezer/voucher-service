package ariefsyaifu.dto.voucher.backoffice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import ariefsyaifu.model.VoucherHistory;

public class ViewVoucherHistoryOas {
    public String id;

    public String userId;

    public String userCode;

    public String userName;

    public String voucherCode;

    public BigDecimal voucherAmount;

    public String transactionId;

    public VoucherHistory.Type type;

    public LocalDateTime claimedAt;

    public LocalDateTime redeemedAt;

    public static ViewVoucherHistoryOas valueOf(VoucherHistory vh) {
        ViewVoucherHistoryOas oas = new ViewVoucherHistoryOas();
        oas.id = vh.id;
        oas.userId = vh.userId;
        oas.userName = vh.userName;
        oas.voucherCode = vh.voucherCode;
        oas.voucherAmount = vh.voucherAmount;
        oas.transactionId = vh.transactionId;
        oas.type = vh.type;
        oas.claimedAt = vh.claimedAt;
        oas.redeemedAt = vh.redeemedAt;
        return oas;
    }
}
