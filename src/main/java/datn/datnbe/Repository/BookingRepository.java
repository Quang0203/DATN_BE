package datn.datnbe.Repository;

import datn.datnbe.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserIduser(int userId);

    @Query(value = "select * from booking where Car_idCarOwner = :idcarowner", nativeQuery = true)
    public List<Booking> findBookingByIdCarOwner(@Param("idcarowner") Integer idcarowner);

    @Query(value = "select * from booking where User_idUser = :iduser", nativeQuery = true)
    public List<Booking> findBookingByIdUser(@Param("iduser") Integer iduser);

    @Query(value = "select * from booking where Car_idCar = :idcar and status NOT IN ('Completed', 'Cancelled')", nativeQuery = true)
    public List<Booking> findBookingByIdCar(@Param("idcar") Integer idcar);

    @Query("SELECT b FROM Booking b WHERE b.carIdcar = :idCar AND " +
            "(b.startdatetime < :endTime AND b.enddatetime > :startTime) " +
            "AND b.status <> 'Cancelled'")
    List<Booking> findBookingsForCar(@Param("idCar") int idCar,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

}