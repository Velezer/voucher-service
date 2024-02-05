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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        result = prime * result + ((voucherCode == null) ? 0 : voucherCode.hashCode());
        result = prime * result + ((voucherAmount == null) ? 0 : voucherAmount.hashCode());
        result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
        result = prime * result + ((voucher == null) ? 0 : voucher.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((claimedAt == null) ? 0 : claimedAt.hashCode());
        result = prime * result + ((redeemedAt == null) ? 0 : redeemedAt.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VoucherHistory other = (VoucherHistory) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        if (voucherCode == null) {
            if (other.voucherCode != null)
                return false;
        } else if (!voucherCode.equals(other.voucherCode))
            return false;
        if (voucherAmount == null) {
            if (other.voucherAmount != null)
                return false;
        } else if (!voucherAmount.equals(other.voucherAmount))
            return false;
        if (transactionId == null) {
            if (other.transactionId != null)
                return false;
        } else if (!transactionId.equals(other.transactionId))
            return false;
        if (voucher == null) {
            if (other.voucher != null)
                return false;
        } else if (!voucher.equals(other.voucher))
            return false;
        if (type != other.type)
            return false;
        if (claimedAt == null) {
            if (other.claimedAt != null)
                return false;
        } else if (!claimedAt.equals(other.claimedAt))
            return false;
        if (redeemedAt == null) {
            if (other.redeemedAt != null)
                return false;
        } else if (!redeemedAt.equals(other.redeemedAt))
            return false;
        return true;
    }

}