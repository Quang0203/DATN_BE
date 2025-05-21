package datn.datnbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ViewWalletResponse {
    private int userId;
    private float walletBalance;
    private String instruction;
    private String paymentStatus;
    List<TransactionResponse> transactions;
}