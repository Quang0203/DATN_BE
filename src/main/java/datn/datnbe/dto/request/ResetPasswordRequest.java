package datn.datnbe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequest {
    @NotBlank private String token;
    @NotBlank private String newpassword;
    @NotBlank
    private String confirmpassword;
}
