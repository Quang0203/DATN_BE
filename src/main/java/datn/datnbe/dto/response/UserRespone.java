package datn.datnbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserRespone {
    int iduser;
    String name;
    LocalDate dateofbirth;
    int nationalidno;
    String phoneno;
    String email;
    String address;
    String drivinglicense;
    String password;
    String role;
    float wallet;
}
