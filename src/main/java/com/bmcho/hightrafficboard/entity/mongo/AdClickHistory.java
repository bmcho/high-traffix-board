package com.bmcho.hightrafficboard.entity.mongo;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "adClickHistory")
public class AdClickHistory extends BaseHistory{
    @Id
    private String id;
}