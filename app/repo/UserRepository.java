package repo;

import models.Patient;
import models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Ronald on 2015/3/3.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
    User findByPhoneNumber(String phone);

    @Query("SELECT usr FROM User usr WHERE usr.phoneNumber=?1 AND usr.password=?2")
    User login(String phone, String password);
}
