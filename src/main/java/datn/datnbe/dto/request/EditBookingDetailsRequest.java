package datn.datnbe.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EditBookingDetailsRequest {
    private String bookingno;
    private LocalDateTime startdatetime;
    private LocalDateTime enddatetime;
    private String driversinformation;
    private String paymentmethod;
    private String status;
    private int carIdcar;
    private int carIdcarowner;
    private int userIduser;
}