package datn.datnbe.Repository.CustomCarRepository;

import java.util.List;

public interface CarRepositoryCustom<T> {
    List<Object[]> findTop5CitiesWithMostCars();
}
