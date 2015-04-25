package repo;

import models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ronald on 2015/3/10.
 */
public interface DepartmentRepository extends JpaRepository<Department, Long>{
    Department findByName(String name);
}
