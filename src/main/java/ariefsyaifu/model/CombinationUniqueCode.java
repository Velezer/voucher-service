package ariefsyaifu.model;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "combination_unique_code")
public class CombinationUniqueCode extends PanacheEntityBase {
    
    @Id
    @Column(name = "code", length = 4)
    public String code;
}
