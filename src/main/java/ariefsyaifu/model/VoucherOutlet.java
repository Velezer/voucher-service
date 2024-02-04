package ariefsyaifu.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "voucher_outlet", uniqueConstraints = @UniqueConstraint(columnNames = { "voucher_id", "outlet_id" }))
public class VoucherOutlet extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    public String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voucher_id", nullable = false)
    public Voucher voucher;

    @Column(name = "outlet_id", nullable = false)
    public String outletId;

    @Column(name = "outlet_name")
    public String outletName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

}