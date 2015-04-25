package repo;

import models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ronald on 2015/4/21.
 */
public interface PatientRepository extends JpaRepository<Patient, Long>{
}
