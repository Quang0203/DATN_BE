package datn.datnbe.Entity;

import datn.datnbe.Entity.AdditionalfunctionsId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(AdditionalfunctionsId.class)
public class Additionalfunctions {
    @Id
    private int idcar;

    @Id
    private String namefunctions;

    // Other fields, getters, setters, etc.
}
