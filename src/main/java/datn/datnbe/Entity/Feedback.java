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
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer idfeedback;
    Integer rate;
    String content;
    LocalDateTime datetime;

    int bookingIdbooking;

    int bookingCarIdcar;

    int bookingCarIdcarowner;

    int bookingUserIduser;

}
