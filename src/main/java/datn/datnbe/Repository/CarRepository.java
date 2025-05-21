package datn.datnbe.Repository;

import datn.datnbe.Entity.Car;
import datn.datnbe.Repository.CustomCarRepository.CarRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface CarRepository extends JpaRepository<Car, Integer>, CarRepositoryCustom<Car> {
    @Query(value = "SELECT TRIM(SUBSTRING_INDEX(c.address, ',', -1)) AS city, COUNT(c.idcar) AS carCount " +
            "FROM Car c " +
            "GROUP BY city " +
            "ORDER BY carCount DESC " +
            "LIMIT 5",nativeQuery = true)
    List<Object[]> findTop5CitiesWithMostCars();

    List<Car> findAllByidcarowner(Long idCarOwner);


    @Query("SELECT c FROM Car c WHERE c.status <> 'Stopped' AND (c.status = 'Available' OR c.idcar NOT IN (" +
            "SELECT b.carIdcar FROM Booking b " +
            "WHERE b.startdatetime <= :endTime AND b.enddatetime >= :startTime " +
            "AND b.status not in ('Completed', 'Cancelled'))) " +
            "AND c.address LIKE %:address%")
    List<Car> findAvailableCars(@Param("address") String address,
                                @Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

    @Query(value = "SELECT EXISTS (" +
            "SELECT 1 FROM car WHERE idCar = :idCar AND (status = 'Available' OR idCar NOT IN (" +
            "SELECT Car_idCar FROM booking " +
            "WHERE startDateTime <= :endTime AND endDateTime >= :startTime " +
            "AND status not in ('Completed', 'Cancelled'))))",
            nativeQuery = true)
    Long checkCarAvailable(@Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime,
                           @Param("idCar") int idCar);
}
