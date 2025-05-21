package datn.datnbe.dto.response;

import datn.datnbe.Entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FeedbackResponse {
    Integer idfeedback;
    Integer rate;
    String content;
    LocalDateTime datetime;

    int bookingIdbooking;
    int bookingCarIdcar;
    int bookingCarIdcarowner;
    int bookingUserIduser;

    User user;

    String userName;
    String carName;
    String carModel;
    String carImage;
    String bookingStartDate;
    String bookingEndDate;
}
