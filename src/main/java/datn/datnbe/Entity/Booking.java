package datn.datnbe.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer idbooking;
    String bookingno;
    LocalDateTime startdatetime;
    LocalDateTime enddatetime;
    String driversinformation;
    String paymentmethod;
    String status;

    int carIdcar;

    int carIdcarowner;

    int userIduser;

}
