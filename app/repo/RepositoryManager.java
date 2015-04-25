package repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Ronald on 2015/3/4.
 */
@Component
@Scope(value = "singleton")
public class RepositoryManager {
    @Autowired
    public UserRepository patientRepo;
    @Autowired
    public InquiryRepository inqRepo;
    @Autowired
    public DoctorRepository doctorRepo;
    @Autowired
    public ReminderRepository reminderRepo;
}
