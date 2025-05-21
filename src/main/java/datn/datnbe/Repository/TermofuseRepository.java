package datn.datnbe.Repository;

import datn.datnbe.Entity.Termofuse;
import datn.datnbe.Entity.TermofuseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermofuseRepository extends JpaRepository<Termofuse, TermofuseId> {
    Termofuse findByIdcar(int idcar);
}
