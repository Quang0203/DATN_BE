package datn.datnbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TransactionResponse {
    Integer idtransactions;
    Float amount;
    String type;
    LocalDateTime datetime;
    String bookingno;
    String carname;
    String note;
}
