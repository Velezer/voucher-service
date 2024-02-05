package ariefsyaifu.dto.voucher.backoffice;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UpdateVoucherRequestBody extends CreateVoucherRequestBody {
    @JsonIgnore
    private String prefixCode;
    
    @JsonIgnore
    private Long qtyClaim;
    
    @JsonIgnore
    private Long qtyRedeem;

}