package datn.datnbe.dto.request;

import lombok.Data;

@Data
public class WithdrawRequest {
    private Integer userId;
    private Float amount;
}
