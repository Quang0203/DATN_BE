package datn.datnbe.Mapper;


import datn.datnbe.Entity.Booking;
import datn.datnbe.dto.request.RentACarRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    public Booking toBooking(RentACarRequest request);
}
