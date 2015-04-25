package repo;

import models.Commodity;
import models.FreeCommodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Ronald on 2015/4/14.
 */
public interface MallRepository extends JpaRepository<Commodity, Long>{
    @Query("SELECT f FROM FreeCommodity f ORDER BY f.id")
    List<FreeCommodity> getAllFreeCommodity();
}
