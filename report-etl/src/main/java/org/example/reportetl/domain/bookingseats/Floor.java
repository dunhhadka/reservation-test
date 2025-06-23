package org.example.reportetl.domain.bookingseats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Setter
@Table(name = "floors")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "building_id", referencedColumnName = "id")
    private Building building;

    @Positive
    private int floorNumber;

    private BigDecimal width;

    private BigDecimal height;

    @OneToMany(mappedBy = "floor", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<Asset> assets = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Version
    private Integer version;

    public void addNewAssets(List<Asset> assets) {
        if (CollectionUtils.isEmpty(assets)) {
            this.assets.clear();
            return;
        }

        this.assets.clear();
        assets.forEach(asset -> {
            asset.setFloor(this);
            this.assets.add(asset);
        });
    }
}
