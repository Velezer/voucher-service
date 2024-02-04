package ariefsyaifu.dto.voucher.backoffice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import ariefsyaifu.dto.OutletDto;
import ariefsyaifu.model.Voucher;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateVoucherRequestBody {
    @Size(min = 5)
    @NotBlank
    @Schema(example = "HEMAT")
    public String prefixCode;
    
    @NotBlank
    public String name;
    
    @NotNull
    public Voucher.Type type;
    
    @Min(0)
    public BigDecimal amount;
    
    @NotNull
    public Voucher.TransactionType transactionType;
    
    @Min(0)
    public BigDecimal quota;
    
    @Min(0)
    public BigDecimal maxDiscount;
    
    @NotNull
    public Voucher.ModeType modeType;
    
    @Min(0)
    public BigDecimal minSubtotal;
    
    @Min(1)
    public Integer maxRedeemedCount;
    
    @NotNull
    public Voucher.UsedDayType usedDayType;

    @NotNull
    public LocalDateTime validFrom;
    
    @NotNull
    public LocalDateTime validTo;

    public String imageUrl;

    public String detail;

    @Min(1)
    public Long qtyClaim;
    
    @Min(1)
    public Long qtyRedeem;
    
    @Min(0)
    public Integer extendValidToInDays;

    @NotNull
    public Voucher.Status status;

    public List<OutletDto> voucherOutlets;

    public List<String> voucherTags;

    public List<String> tiers;
}
