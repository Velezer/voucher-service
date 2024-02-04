package ariefsyaifu.dto.voucher.external;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RedeemVoucherRequestBody {
    @Min(0)
    public BigDecimal subTotal;

    @NotBlank
    public String voucherCode;

    @NotBlank
    public String userId;

    @NotBlank
    public String outletId;

    @NotBlank
    public String transactionId;

}
