package repo;

import models.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ronald on 2015/4/22.
 */
public interface SuggestionRepository extends JpaRepository<Suggestion, Long>{
}
