package datn.datnbe.Enum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum BookingStatus {
    INITIALIZED("Initialized"),
    CANCELLED( "Cancelled"),
    IN_PROGRESS("In - Progress"),
    COMPLETE("Completed"),
    PENDING_PAYMENT("Pending Payment"),
    CONFIRMED("Confirmed"),
    PENDING_DEPOSIT("Pending Deposit"),
    REPORTED("Reported")
    ;
    private String status;
}
