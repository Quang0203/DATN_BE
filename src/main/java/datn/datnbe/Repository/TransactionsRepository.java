package datn.datnbe.Repository;

import datn.datnbe.Entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {
    List<Transactions> findByUserIduser(Integer userId);

    List<Transactions> findByUserIduserAndDatetimeBetween(int userId, LocalDateTime startDate, LocalDateTime endDate);
}
