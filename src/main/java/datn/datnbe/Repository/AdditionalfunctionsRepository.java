package datn.datnbe.Repository;

import datn.datnbe.Entity.Additionalfunctions;
import datn.datnbe.Entity.AdditionalfunctionsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdditionalfunctionsRepository extends JpaRepository<Additionalfunctions, AdditionalfunctionsId> {
    Additionalfunctions findByIdcar(int idcar);

}
