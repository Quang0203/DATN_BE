package datn.datnbe.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@IdClass(TermofuseId.class)
public class Termofuse {
    @Id
    @Column(nullable = false)
    int idcar;

    @Id
    @Column(nullable = false, length = 100)
    String nameterms;
}
