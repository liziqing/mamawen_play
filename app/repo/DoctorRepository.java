package repo;

import models.Doctor;
import models.OutPatientTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Ronald on 2015/3/2.
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    public Doctor findByUserName(String userName);

    @Query("SELECT doc FROM Doctor doc WHERE doc.department.name=?1")
    Page<Doctor> getDoctorOfDepartment(String department,Pageable page);
    @Query("SELECT  time FROM OutPatientTime time WHERE time.weekday = ?1 AND time.timeSegment = ?2")
    OutPatientTime getOutpatientTime(int weekday, int segment);
    @Query("SELECT time FROM OutPatientTime time JOIN time.doctor d WHERE d.id=?1 ORDER BY  time.weekday, time.timeSegment")
    List<OutPatientTime> getDoctorWorkTimes(Long id);
    public Doctor findByPhoneNumber(String phone);
    public Doctor findByPhoneNumberAndPassword(String phone, String password);
}
