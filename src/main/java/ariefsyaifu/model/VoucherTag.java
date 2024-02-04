package ariefsyaifu.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "voucher_tag", uniqueConstraints = @UniqueConstraint(columnNames = { "voucher_id", "tag_id" }))
public class VoucherTag extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false)
    public String id;

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = false)
    public Voucher voucher;

    @Column(name = "tag_id", nullable = false)
    public String tagId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

}