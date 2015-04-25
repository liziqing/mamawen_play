package repo;

import models.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ronald on 2015/3/10.
 */
public interface HospitalRepository extends JpaRepository<Hospital, Long>{
    Hospital findByName(String name);
}
