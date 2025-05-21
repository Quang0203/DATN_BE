package datn.datnbe.dto.response;

import datn.datnbe.Entity.Additionalfunctions;
import datn.datnbe.Entity.Car;
import datn.datnbe.Entity.Termofuse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ViewCarDetailsResponse {
    private Car car;
    private Termofuse termsOfUse;
    private Additionalfunctions additionalFunctions;
}
