package datn.datnbe.Mapper;

import datn.datnbe.Entity.Car;
import datn.datnbe.dto.request.AddCarRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {
    public Car toCar(AddCarRequest request);
}
