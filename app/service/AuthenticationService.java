package service;

import models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repo.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;


/**
 * Created by Ronald on 2015/3/3.
 */
@Service
public class AuthenticationService {
    @Autowired
    DoctorRepository doctorRepo;
    @Autowired
    PatientRepository patientRepo;
    @Autowired
    InquiryRepository inquiryRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    HospitalRepository hospitalRepo;
    @Autowired
    DepartmentRepository departRepo;
    @Autowired
    TagRepository tagRepo;
    @Autowired
    ReportRepository reportRepo;
    @Autowired
    ChatRecordRepository chatRepo;
    @Autowired
    ReminderRepository reminderRepo;
    @Autowired
    FriendRepository friendRepo;
    @Autowired
    OrderRepository orderRepo;
    @Autowired
    RushEventRepository eventRepo;
    @Autowired
    HealthRecordRepository healthRecRepo;
    @Autowired
    SuggestionRepository suggestionRepo;
    @Autowired
    UserMarkRecordRepository  userMarkRecordRepo;

    public boolean authenticateDoctor(String userName, String sessionKey){
        Doctor doctor = doctorRepo.findByUserName(userName);
        return doctor.sessionKey.equals(sessionKey);
    }
    public boolean authenticatePatient(String userName, String sessionKey){
        User user = userRepo.findByUserName(userName);
        return user.sessionKey.equals(sessionKey);
    }
    public Doctor verifyDoctor(long uid, String sessionKey){
        return doctorRepo.findOne(uid);
    }
    public User verifyUser(long uid, String sessionKey){
        return userRepo.findOne(uid);
    }
}
