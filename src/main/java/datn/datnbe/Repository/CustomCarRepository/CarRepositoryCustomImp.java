package datn.datnbe.Repository.CustomCarRepository;

import datn.datnbe.Entity.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarRepositoryCustomImp implements CarRepositoryCustom<Car> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> findTop5CitiesWithMostCars() {
        String sql = "SELECT SUBSTRING_INDEX(c.address, ',', -1) AS city, COUNT(c.idCar) AS carCount " +
                "FROM Car c " +
                "GROUP BY city " +
                "ORDER BY carCount DESC " +
                "LIMIT 5";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }
}
