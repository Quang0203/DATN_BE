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
public class EditCarRequest {
    String name;

    String brand;

    String model;
    String color;

    int numberofseats;

    int productionyears;

    String tranmissiontype;

    String fueltype;

    int mileage;

    float fuelconsumption;

    float baseprice;

    float deposite;

    String address;

    String descripton;

    String images;

    String status;

    int idcarowner;
}
