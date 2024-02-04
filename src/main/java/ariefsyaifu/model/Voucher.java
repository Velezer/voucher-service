package ariefsyaifu.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ariefsyaifu.model.base.Audit;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "voucher", indexes = {
        @Index(name = "idx_voucher_deleted_at_status", columnList = "deleted_at,status"),
})
public class Voucher extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    public String id;

    @Column(name = "prefix_code", unique = true)
    public String prefixCode;

    @Column(name = "name")
    public String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public Type type;

    public enum Type {
        AMOUNT,
        PERCENTAGE,
    }

    /**
     * Rp 1000 | Percentange 25
     */
    @Column(name = "amount")
    public BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    public TransactionType transactionType;

    public enum TransactionType {
        FIRST_VOUCHER,
        TRANSACTION
    }

    /**
     * Rp quota
     */
    @Column(name = "quota", nullable = false)
    public BigDecimal quota;

    /**
     * used for calculation remaining without changing quota value
     */
    @Column(name = "used_quota", nullable = false)
    public BigDecimal usedQuota = BigDecimal.ZERO;

    /**
     * value is Rp for percentage
     */
    @Column(name = "max_discount", nullable = false)
    public BigDecimal maxDiscount;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_type", nullable = false)
    public ModeType modeType;

    public enum ModeType {
        DINE_IN,
        DELIVERY,
        TAKE_AWAY,
    }

    /**
     * value is Rp, min transaction to use voucher
     */
    @Column(name = "min_subtotal", nullable = false)
    public BigDecimal minSubtotal;

    @Column(name = "max_redeemed_count", nullable = false)
    public Integer maxRedeemedCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "used_day_type")
    public UsedDayType usedDayType;

    public enum UsedDayType {
        EVERYDAY,
        BIRTHDAY,
    }

    @Column(name = "valid_from", columnDefinition = "timestamp default now()")
    public LocalDateTime validFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_to", columnDefinition = "timestamp default now()")
    public LocalDateTime validTo;

    @Column(name = "image_url")
    public String imageUrl;

    @Column(name = "detail", columnDefinition = "text")
    public String detail;

    @Column(name = "qty_claim", nullable = false)
    public Long qtyClaim;

    @Column(name = "qty_claimed", nullable = false)
    public Long qtyClaimed = 0l;

    @Column(name = "qty_redeem", nullable = false)
    public Long qtyRedeem;

    @Column(name = "qty_redeemed", nullable = false)
    public Long qtyRedeemed = 0l;

    @Column(name = "extend_valid_to_in_days", nullable = false, columnDefinition = "int4 default 0")
    public Integer extendValidToInDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public Status status;

    public enum Status {
        ACTIVE,
        DRAFT,
        INACTIVE
    }

    @JsonIgnore
    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<VoucherOutlet> outlets;
    
    @JsonIgnore
    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<VoucherTag> tags;
    
    @JsonIgnore
    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<VoucherTier> tiers;

    public boolean isActive() {
        return this.status.equals(Voucher.Status.ACTIVE);
    }

    public boolean isPercentage() {
        return this.type.equals(Voucher.Type.PERCENTAGE);
    }

}