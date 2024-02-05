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
    
    @Min(1)
    @Schema(example = "10000")
    public BigDecimal amount;
    
    @NotNull
    public Voucher.TransactionType transactionType;
    
    @Min(1)
    @Schema(example = "100000")
    public BigDecimal quota;
    
    @Min(1)
    @Schema(example = "10000")
    public BigDecimal maxDiscount;
    
    @NotNull
    public Voucher.ModeType modeType;
    
    @Min(0)
    public BigDecimal minSubtotal;
    
    @Min(1)
    public Integer maxRedeemedCount;
    
    @NotNull
    public Voucher.UsedDayType usedDayType;

    @Schema(example = "false")
    public boolean isHidden;

    @NotNull
    @Schema(example = "2024-02-04T12:15:50")
    public LocalDateTime validFrom;
    
    @NotNull
    @Schema(example = "2024-12-10T12:15:50")
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
    @Schema(example = "ACTIVE")
    public Voucher.Status status;

    public List<OutletDto> outlets;

    @Schema(example = "[\"FEB\"]")
    public List<String> tags;
    
    @Schema(example = "[\"GOLDEN\"]")
    public List<String> tiers;
}
