package service;

import actor.IMScheduler;
import actor.message.IMSenderMessage;
import dto.*;
import models.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.mvc.Http;
import utils.IDGenerator;
import utils.MD5Generator;
import utils.XmppConnectionManager;

import javax.persistence.EntityManager;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Created by Ronald on 2015/3/3.
 */
@Service
public class UserService extends AuthenticationService {
    public User getUserByID(Long id) {
        return userRepo.findOne(id);
    }

    public void testAddDoctor() {

    }

    public void testAddPaitient() {
        User user1 = new User();
        user1.userName = "万万想不到";
        user1.phoneNumber = "1234567890";
        user1.email = "dd@a.com";
        userRepo.save(user1);
        user1 = new User();
        user1.userName = "迟来的风";
        user1.phoneNumber = "12345687890";
        user1.email = "dd@b.com";
        userRepo.save(user1);
        user1 = new User();
        user1.userName = "玲儿的妈妈";
        user1.phoneNumber = "1334567890";
        user1.email = "dd@c.com";
        userRepo.save(user1);
        /*Patient p1 = new Patient();
        p1.name = "rose";
        p1.name = "王茜";
        patientRepo.save(p1);
        Patient p2 = new Patient();
        p2.name = "小文儿";
        p2.name = "张小玲";
        patientRepo.save(p2);
        Patient p3 = new Patient();
        p3.name = "meilin";
        p3.name = "王慧丽";
        patientRepo.save(p3);
        Patient p4 = new Patient();
        p4.name = "maria";
        p4.name = "郭曼曼";
        patientRepo.save(p4);
        Patient p5 = new Patient();
        p5.name = "mary";
        p5.name = "张雯";
        patientRepo.save(p5);
        Patient p6 = new Patient();
        p6.name = "alice";
        p6.name= "周瑾";
        patientRepo.save(p6);
        Patient p7 = new Patient();
        p7.name = "elisabeth";
        p7.name = "李静";
        patientRepo.save(p7);
        Patient p8 = new Patient();
        p8.name = "xiaolin";
        p8.name = "李晓玲";
        Patient p9 = new Patient();
        p9.name = "melia";
        p9.name = "周梅";
        patientRepo.save(p9);
        Patient p10 = new Patient();
        p10.name = "随风而去";
        p10.name = "丁梦琪";
        patientRepo.save(p10);*/
    }

    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse userLogin(UserLoginRequest request) {
        try {
            User user = userRepo.login(request.phoneNumber, new String(Base64.decodeBase64(request.password)));
            if (user != null) {
                long timeStamp = System.currentTimeMillis();
                user.sessionKey = md5Hex(Long.toString(timeStamp));
                user.deviceType = request.deviceType;
                userRepo.save(user);
                UserLoginInfo info = UserLoginInfo.genUserLoginInfo(user);
                return new UserLoginResponse(info);
            } else {
                return new GeneralResponse(5, "login failed");
            }
        } catch (Exception e) {
            play.Logger.error("login failed", e);
            throw e;
        }
    }
    public User getUserByHisPhone(String phone){
        return userRepo.findByPhoneNumber(phone);
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse userRegister(UserRegisterRequest request) throws Exception{
        User existtUser = userRepo.findByPhoneNumber(request.phoneNumber);
        if (existtUser == null) {
            User user = new User();
            long timeStamp = System.currentTimeMillis();
            user.sessionKey = md5Hex(Long.toString(timeStamp));
            if (request.name != null) {
                user.userName = user.name = request.name;
            } else {
                user.userName = user.name = request.phoneNumber;
            }
            user.deviceType = request.deviceType;
            user.phoneNumber = request.phoneNumber;
            user.password = new String(Base64.decodeBase64(request.password));
            user.status = UserInfo.getStatusFromPhase(request.phase);
            User persUser = userRepo.save(user);
            Patient pt = new Patient();
            pt.status = persUser.status;
            pt.user = persUser;
            persUser.patients = new HashSet<>();
            persUser.patients.add(pt);
            String jid = "user" + IDGenerator.instance.nextId();
            if (XmppConnectionManager.register(jid, "123456")) {
                persUser.jid = jid;
                persUser.xmppToken = "123456";
                userRepo.save(persUser);
                UserRegisterInfo info = UserRegisterInfo.genUserRegisterResponse(persUser);
                return new UserRegisterResponse(info);
            } else {
                return new GeneralResponse(105, "IM server user register failed");
            }
        } else {
            return new GeneralResponse(33, "This phone has been used");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse updateUserPrivateInfo(User user, UpdateUserInfoRequest request){
        Patient pt = null;
        User persUser = userRepo.save(user);
        if (persUser.patients == null || persUser.patients.size() == 0){
            pt = new Patient();
            persUser.patients = new HashSet<>();
        } else {
            for (Patient tmp : persUser.patients){
                if (tmp.status == UserInfo.getStatusFromPhase(request.phase)){
                    pt = tmp;
                    break;
                }
            }
        }
        if (pt == null){
            pt = new Patient();
        }
        pt.status = UserInfo.getStatusFromPhase(request.phase);
        pt.mensCycle = request.mensCycle;
        pt.exptBirthday = request.exptBirthday;
        pt.height = request.height;
        pt.lastMensDate = request.lastMensDate;
        pt.pastWeight = request.weightBeforePreg;
        pt.user = persUser;
        patientRepo.save(pt);
        persUser.patients.add(pt);
        persUser.status = pt.status;
        userRepo.save(persUser);
        UserInfo info = UserInfo.genUserInfo(persUser);
        return new UpdateUserInfoResponse(info);
    }
    @Transactional
    public GeneralResponse getExpertDoctor(GetExpertDoctorRequest request, int page, int limit) {
        String query = "SELECT doc FROM Doctor doc WHERE ";
        if (request.department != null && !request.department.isEmpty()) {
            query += "doc.department.name='" + request.department + "' AND ";
        }
        if (request.level != null) {
            query += "doc.level=" + request.level;
        } else {
            query += "doc.level>0";
        }
        query += " ORDER BY doc.lastActiveTime DESC";
        EntityManager em = JPA.em("default");
        List<Doctor> doctors = em.createQuery(query, Doctor.class).setFirstResult(page * limit).setMaxResults(limit).getResultList();
        List<DoctorInfo> infos = new ArrayList<>();
        for (Doctor doctor : doctors) {
            DoctorInfo info = DoctorInfo.genDoctorInfo(doctor);
            infos.add(info);
        }
        return new DoctorInfosResponse(infos);
    }

    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse resetPassword(User user, PasswordResetRequest request) {
        String oldPwd = new String(Base64.decodeBase64(request.oldPassword));
        if (oldPwd.equals(user.password) || user.password == null) {
            user.password = new String(Base64.decodeBase64(request.newPassword));
            userRepo.save(user);
            return new GeneralResponse();
        } else {
            return new GeneralResponse(62, "old password not matched");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse resetPasswordByPhone(PasswordResetByPhoneRequest request) {
        User user = userRepo.findByPhoneNumber(request.phoneNumber);
        if (user != null) {
            user.password = new String(Base64.decodeBase64(request.newPassword));
            userRepo.save(user);
            return new GeneralResponse();
        } else {
            return new GeneralResponse(62, "old password not matched");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse storeAvatarFile(User user, Http.MultipartFormData.FilePart avatar) {
        user.avatar = storeAvatarPhoto(user, avatar);
        userRepo.save(user);
        return new UserInfoResponse(UserInfo.genUserInfo(user));
    }

    String storeAvatarPhoto(User user, Http.MultipartFormData.FilePart avatar) {
        String storePath = Play.application().path() + "/store/";
        String docPlatePath = storePath + "user" + user.id + "/avatar/";
        File plateDir = new File(docPlatePath);
        if (!plateDir.exists()) {
            plateDir.mkdirs();
        }
        String avatarUrl = "";
        try {
            String fileName = avatar.getFilename();
            String newFlName;
            if (!fileName.contains(".")) {
                newFlName = MD5Generator.getMd5Value(fileName + System.currentTimeMillis());
            } else {
                newFlName = MD5Generator.getMd5Value(fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()) + fileName.substring(fileName.lastIndexOf("."));
            }
            avatarUrl = "/user/" + user.id + "/avatar/" + newFlName;
            FileUtils.moveFile(avatar.getFile(), new File(plateDir, newFlName));

        } catch (Exception e) {
            Logger.error("store plate failed", e);
        }
        return avatarUrl;
    }

    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse addHealthRecorde(User user, AddHealthRecordRequest request) throws Exception {
        HealthRecord record;
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd");
        if (request.category.equals("宝宝") && request.subCategory.equals("奶量")) {
            Date recordDate = smt.parse(request.recordDate);
            record = healthRecRepo.getBabyMilkRecordOfUser(user.id, recordDate);
            if (record != null) {
                record.value += request.value;
                record.updateTime = System.currentTimeMillis();
                healthRecRepo.save(record);
            }
        } else if (request.subCategory.equals("体温")) {
            Date recordDate = smt.parse(request.recordDate);
            record =  healthRecRepo.getTempRecordOfUser(user.id, request.category, recordDate);
            if (record != null) {
                record.value = request.value;
                record.updateTime = System.currentTimeMillis();
                healthRecRepo.save(record);
            }
        } else {
            record = healthRecRepo.getOtherRecordOfUser(user.id, request.category, request.subCategory, request.cycle);
            if (record != null) {
                record.value = request.value;
                record.updateTime = System.currentTimeMillis();
                healthRecRepo.save(record);
            }
        }
        if (record == null) {
            record = new HealthRecord();
            record.category = request.category;
            record.subCategory = request.subCategory;
            record.value = request.value;
            record.cycle = request.cycle;
            if (request.recordDate != null){
                record.recordDate = smt.parse(request.recordDate);
            }
            record.createTime = System.currentTimeMillis();
            record.updateTime = System.currentTimeMillis();
            HealthRecord persRecord = healthRecRepo.save(record);
            persRecord.user = userRepo.save(user);
            healthRecRepo.save(persRecord);
        }
        HealthRecordInfo info = HealthRecordInfo.genRecordInfo(record);
        return new AddHealthRecordResponse(info);
    }
    public GeneralResponse getHealthRecords(User user, GetHealthRecordRequest request){
        List<HealthRecord> records = healthRecRepo.getSpcificRecordOfUser(user.id, request.category, request.subCategory);
        List<HealthRecordInfo> infos = new ArrayList<>();
        for (HealthRecord record: records){
            HealthRecordInfo info = HealthRecordInfo.genRecordInfo(record);
            infos.add(info);
        }
        return new GetHealthRecordsResponse(infos);
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse bindClientID(User user, BindClientIDRequest request){
        user.clientID = request.clientID;
        userRepo.save(user);
        return new UserInfoResponse(UserInfo.genUserInfo(user));
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse logout(User user){
        user.clientID = "";
        userRepo.save(user);
        return new GeneralResponse();
    }
    public GeneralResponse getUserRecords(Long userID){
        List<Inquiry> inquiries = inquiryRepo.getFinishedInquiryOfUser(userID);
        List<InquiryResult> results = new ArrayList<>();
        for (Inquiry inq : inquiries){
            InquiryResult result = new InquiryResult();
            result.inquiryID = inq.id;
            result.description = inq.description;
            result.content = inq.content;
            if (inq.photoes != null){
                String[] photos = inq.photoes.split("\\s");
                result.photos = Arrays.asList(photos);
            }
            result.drug = inq.drug;
            result.reportDesc = inq.report.description;
            result.repportSuggestion = inq.report.suggestion;
            result.createTime = inq.report.createTime;
            results.add(result);
        }
        UserRecordInfo uInfos = new UserRecordInfo();
        uInfos.patientReecord = new ArrayList<>();
        PatientRecordInfo info = new PatientRecordInfo();
        if (inquiries.isEmpty()){
            User user = userRepo.findOne(userID);
            if (user != null){
                uInfos.userID = userID;
                info.patientID = userID;
                info.age = 4;
                info.gender = "M";
                info.name = user.name;
                info.inquiries = results;
                uInfos.patientReecord.add(info);
            } else {
                return new GeneralResponse(5, "This user not existed");
            }
        } else {
            uInfos.userID = userID;
            info.patientID = userID;
            User user = inquiries.get(0).user;
            info.age = 4;
            info.gender = "M";
            info.name = user.name;
            info.inquiries = results;
            uInfos.patientReecord.add(info);
        }
        return new RecordInfoResponse(uInfos);
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse updateBasicUserInfo(User user, UpdateBasicUserInfoRequest request){
        user.name = request.name;
        user.nickame = request.nickname;
        //user.phoneNumber = request.phoneNumber;
        user.status = UserInfo.getStatusFromPhase(request.phase);
        userRepo.save(user);
        UserInfo info = UserInfo.genUserInfo(user);
        return new UpdateBasicUserInfoResponse(info);
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse addSuggestion(User user, PostSuggestionRequest request){
        Suggestion sug = new Suggestion();
        sug.content = request.content;
        sug.user = userRepo.save(user);
        suggestionRepo.save(sug);
        return new GeneralResponse();
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse userSendMark(User user, UserMarkRequest request){
        Doctor doctor = doctorRepo.findOne(request.doctorID);
        Inquiry inquiry = inquiryRepo.findOne(request.inquiryID);
        if (doctor != null && inquiry != null){
            UserMarkRecord markRecord = new UserMarkRecord();
            markRecord.user = userRepo.save(user);
            markRecord.doctor = doctor;
            markRecord.inquiry = inquiry;
            markRecord.comment = request.comment;
            markRecord.servingMark = request.servingMark;
            markRecord.efftMark = request.efftMark;
            userMarkRecordRepo.save(markRecord);
            ChatRecord record = new ChatRecord();
            record.category = 6;
            record.senderID = doctor.id;
            record.receiverID = user.id;
            record.doctorID = doctor.id;
            record.inquiryID = request.inquiryID;
            record.createTime = System.currentTimeMillis();
            record.comment = request.comment;
            record.servingMark = request.servingMark;
            record.efftMark = request.efftMark;
            chatRepo.save(record);
            ChatParticipantInfo sender = new ChatParticipantInfo();
            sender.id = user.id;
            sender.role = 1;
            sender.name = user.name;
            sender.avatar = user.avatar;
            ChatParticipantInfo receiver = new ChatParticipantInfo();
            receiver.id = doctor.id;
            receiver.role = 0;
            receiver.name = doctor.name;
            receiver.avatar = doctor.avatar;
            MarkChatMessageResponse response = new MarkChatMessageResponse();
            response.sender = sender;
            response.receiver = receiver;
            response.inquiryID = request.inquiryID;
            response.category = 6;
            response.comment = request.comment;
            response.efftMark = request.efftMark;
            response.servingMark = request.servingMark;
            IMScheduler.defaultSender.tell(new IMSenderMessage(doctor.jid,  0, doctor.clientID, doctor.deviceType, response), null);
            return new GeneralResponse();
        } else {
            return new GeneralResponse(106, "No matched doctor or inquiry");
        }
    }
}

