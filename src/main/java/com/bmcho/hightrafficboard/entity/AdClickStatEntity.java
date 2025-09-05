
package com.bmcho.hightrafficboard.entity;

import com.bmcho.hightrafficboard.entity.audit.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "ad_click_stat")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdClickStatEntity extends MutableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adId;

    private Long count;

    private String dt;

    public AdClickStatEntity(Long adId, Long count, String dt) {
        this.adId = adId;
        this.count = count;
        this.dt = dt;
    }

}