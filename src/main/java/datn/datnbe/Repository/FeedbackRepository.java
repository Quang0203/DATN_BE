package datn.datnbe.Repository;

import datn.datnbe.Entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findAllByBookingCarIdcarowner(int carOwnerId);

    List<Feedback> findAllByBookingCarIdcarownerAndRate(int carOwnerId, int rate);

    List<Feedback> findAllByBookingCarIdcar(int carOwnerId);
}
