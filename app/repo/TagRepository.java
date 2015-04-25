package repo;

import models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Ronald on 2015/3/11.
 */
public interface TagRepository extends JpaRepository<Tag, Long>{
    List<Tag> findByTagIn(List<String> tags);
    Tag findByTag(String tag);
}
