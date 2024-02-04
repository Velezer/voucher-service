package ariefsyaifu.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import ariefsyaifu.model.base.Audit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "voucher_history", indexes = {
        @Index(name = "idx_vh_deleted_at_voucher_id_type_user_id", columnList = "deleted_at,voucher_id,type,user_id"),
        @Index(name = "idx_vh_deleted_at_user_id_type", columnList = "deleted_at,user_id,type"),
        @Index(name = "idx_vh_deleted_at_voucher_code", columnList = "deleted_at,voucher_code"),
})
public class VoucherHistory extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false)
    public String id;

    @Column(name = "user_id")
    public String userId;

    @Column(name = "user_name")
    public String userName;

    @Column(name = "voucher_code", nullable = false, unique = true)
    public String voucherCode;

    /**
     * Rp
     */
    @Column(name = "voucher_amount")
    public BigDecimal voucherAmount;

    @Column(name = "transaction_id")
    public String transactionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voucher_id")
    public Voucher voucher;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public Type type;

    public enum Type {
        AVAILABLE,
        CLAIMED,
        REDEEMED, FAILED,
    }

    @Column(name = "claimed_at", columnDefinition = "timestamp")
    public LocalDateTime claimedAt;

    @Column(name = "redeemed_at", columnDefinition = "timestamp")
    public LocalDateTime redeemedAt;

    public boolean isRedeemed() {
        return this.type.equals(VoucherHistory.Type.REDEEMED);
    }
    
    public boolean isClaimed() {
        return this.type.equals(VoucherHistory.Type.CLAIMED);
    }

}