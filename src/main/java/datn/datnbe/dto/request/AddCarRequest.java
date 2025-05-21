package datn.datnbe.dto.request;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddCarRequest {
    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String brand;

    String model;
    String color;

    @Column(nullable = false)
    int numberofseats;

    @Column(nullable = false)
    int productionyears;

    String tranmissiontype;

    String fueltype;

    @Column(nullable = false)
    int mileage;

    @Column(nullable = false)
    float fuelconsumption;

    @Column(nullable = false)
    float baseprice;

    @Column(nullable = false)
    float deposite;

    @Column(nullable = false, length = 200)
    String address;

    @Column(nullable = false, length = 300)
    String descripton;

    String images;

    @Column(nullable = false)
    String status;

    @Column(nullable = false)
    int idcarowner;
}
