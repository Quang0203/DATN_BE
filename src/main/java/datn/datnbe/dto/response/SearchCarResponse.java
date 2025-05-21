package datn.datnbe.dto.response;

import datn.datnbe.Entity.Booking;
import datn.datnbe.Entity.Car;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class SearchCarResponse {
    Car car;
    long bookingNumber;
    double rate;

}
