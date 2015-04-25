package service;

import actor.SmsScheduler;
import actor.message.SmsMessage;
import dto.*;
import exception.IMServerException;
import models.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import play.Play;
import play.mvc.Http;
import utils.ConfigureManager;
import utils.IDGenerator;
import utils.MD5Generator;
import utils.XmppConnectionManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Created by Ronald on 2015/3/15.
 */
@Service
public class DoctorService extends AuthenticationService{
    public Doctor getDoctorByUserName(String userName){
        return doctorRepo.findByUserName(userName);
    }
    public GeneralResponse getVolunteerDoctor(String department, int page, int limit){
        try {
            List<Doctor> doctors = doctorRepo.getDoctorOfDepartment(department, new PageRequest(page, limit)).getContent();
            List<DoctorInfo> infos = new ArrayList<>();
            for (Doctor doctor : doctors) {
                DoctorInfo info = new DoctorInfo();
                info.name = doctor.name;
                info.department = doctor.department.name;
                info.title = doctor.title;
                info.hospital = doctor.inHospital.name;
                info.plateUrl = doctor.plateUrl;
                info.level = doctor.level;
                infos.add(info);
            }
            return new DoctorInfosResponse(infos);
        } catch (Exception e){
            play.Logger.info(e.getMessage());
            return new GeneralResponse(13, "Get doctor list failed");
        }
    }
    /*public GeneralResponse doctorLogin(String name, String password){
        try{
            Doctor doctor = doctorRepo.findByUserName(name);
            if (doctor != null){
                DoctorInfo doctor = new DoctorInfo();
                doctor.name = doctor.name;
                doctor.doctorID = doctor.id;
                doctor.name = doctor.name;
                doctor.department = doctor.department.name;
                doctor.title = doctor.title;
                doctor.hospital = doctor.inHospital.name;
                doctor.plateUrl = doctor.plateUrl;
                doctor.level = doctor.level;
                return new DoctorLoginResponse(doctor);
            } else {
                return new GeneralResponse(5, "Login failed");
            }
        } catch (Exception e){
            play.Logger.doctor(e.getMessage());
            return new GeneralResponse(7, "Get doctor doctor failed");
        }
    }*/
    public GeneralResponse sendSms(Doctor doctor, SmsSenderRequest request){
        SmsMessage msg = new SmsMessage();
        msg.content = doctor.name+"医生（医师号："+ doctor.id + "）：" + request.content;
        msg.category = 1;
        msg.phoneNumber = request.phoneNumber;
        SmsScheduler.scheduler.tell(msg, null);
        return new GeneralResponse();
    }
    public GeneralResponse userUsingPhoneExist(String phoneNumber){
        User user = userRepo.findByPhoneNumber(phoneNumber);
        if (user != null){
            UserInfo info = UserInfo.genUserInfo(user);
            return new UserIsUsingPhoneResponse(true, info);
        } else {
            return new UserIsUsingPhoneResponse(false, null);
        }
    }
    public GeneralResponse doctorTestRegister(RegisterDoctorTestRequest request){
        try {
            Doctor doctor = new Doctor();
            doctor.userName = request.userName;
            doctor.name = request.name;
            doctor.phoneNumber = request.phoneNum;
            doctor.email = request.email;
            doctor.level = request.level;
            doctor.background = "医学硕士";
            doctor.achievement = "发表国家级论文数篇，参与编著健康类著作一部。";
            doctor.createTime = System.currentTimeMillis();

            Doctor persDoctor = doctorRepo.save(doctor);
            Department department = departRepo.findByName(request.department);
            if (department != null){
                persDoctor.department = department;
            } else {
                persDoctor.department = new Department(request.department);
            }
            Hospital hospital = hospitalRepo.findByName(request.hospital);
            if (hospital != null){
                persDoctor.inHospital = hospital;
            } else {
                //persDoctor.inHospital = new Hospital(request.hospital, "三甲");
            }
            doctorRepo.save(persDoctor);
            return  new RegisterDoctorTestResponse(persDoctor.id);
        } catch (Exception e){
            play.Logger.info(e.getMessage());
            return new GeneralResponse(15, "Register doctor failed");
        }
    }
    public GeneralResponse resetPassword(Doctor doctor, PasswordResetRequest request){
        if (doctor.password.equals(new String(Base64.decodeBase64(request.oldPassword)))){
            doctor.password = new String(Base64.decodeBase64(request.newPassword));
            doctorRepo.save(doctor);
            return new GeneralResponse();
        } else {
            return new GeneralResponse(62, "old password not matched");
        }
    }
    public GeneralResponse resetPasswordByPhone(PasswordResetByPhoneRequest request){
        Doctor doctor = doctorRepo.findByPhoneNumber(request.phoneNumber);
        if (doctor != null){
            doctor.password = new String(Base64.decodeBase64(request.newPassword));
            doctorRepo.save(doctor);
            return new GeneralResponse();
        } else {
            return new GeneralResponse(64, "phone number not matched");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse doctorRegister(DoctorRegisterRequest request) throws Exception{
        Doctor existDoctor = doctorRepo.findByPhoneNumber(request.phoneNumber);
        if (existDoctor != null){
            return new GeneralResponse(33, "This phone has been used");
        }
        long start = System.currentTimeMillis();
        long timeStamp = System.currentTimeMillis();
        Doctor doctor = new Doctor();
        doctor.password = new String(Base64.decodeBase64(request.password));
        doctor.sessionKey = md5Hex(Long.toString(timeStamp));
        doctor.name = request.name;
        doctor.title = request.title;
        doctor.createTime = timeStamp;
        doctor.updateTime = 0l;
        doctor.level = 0;
        doctor.deviceType = request.deviceType;
        doctor.phoneNumber = request.phoneNumber;
        Doctor persDoc = doctorRepo.save(doctor);
        if (request.department != null && !request.department.isEmpty()) {
            Department depart = departRepo.findByName(request.department);
            if (depart != null) {
                persDoc.department = depart;
            } else {
                persDoc.department = new Department(request.department);
            }
        }
        if (request.hospital != null && !request.hospital.isEmpty()) {
            Hospital hospital = hospitalRepo.findByName(request.hospital);
            if (hospital != null) {
                persDoc.inHospital = hospital;
            } else {
                persDoc.inHospital = new Hospital(request.hospital);
            }
        }
        if (request.workTime != null) {
            for (OutPatientTimeRequest opReq : request.workTime) {
                OutPatientTime time = doctorRepo.getOutpatientTime(opReq.weekday, opReq.timeSegment);
                if (time != null) {
                    persDoc.workTimes.add(time);
                } else {
                    persDoc.workTimes.add(new OutPatientTime(opReq.weekday, opReq.timeSegment));
                }
            }
        }
        String jid="doctor" + IDGenerator.instance.nextId();
        if (XmppConnectionManager.register(jid, "123456")) {
            persDoc.jid = jid;
            persDoc.xmppToken = "123456";
            doctorRepo.save(persDoc);
            long end = System.currentTimeMillis();
            Logger.info("doctorRegister elapsed: " + (end - start));
            DoctorRegisterInfo info = new DoctorRegisterInfo();
            info.name = persDoc.name;
            info.cellPhone = persDoc.phoneNumber;
            info.doctorID = persDoc.id;
            info.level = persDoc.level;
            info.title = persDoc.title;
            info.department = persDoc.department.name;
            info.hospital = persDoc.inHospital.name;
            info.jid = persDoc.jid;
            info.imToken = persDoc.xmppToken;
            info.sessionKey = persDoc.sessionKey;
            return new DoctorRegisterResponse(info);
        } else {
            return new GeneralResponse(34, "IM server doctorRegister failed");
        }

    }
    public GeneralResponse storeAuthenFile(Doctor doctor, Http.MultipartFormData.FilePart plate, Http.MultipartFormData.FilePart license){
        try{
            if (plate != null){
                doctor.plateUrl = storePlatePhoto(doctor, plate);
            }
            if (license != null){
                doctor.licenceUrl = storeLicensePhoto(doctor, license);
            }
            doctor.level = 1;
            doctorRepo.save(doctor);
            return new DoctorInfoResponse(DoctorInfo.genDoctorInfo(doctor));
        } catch (Exception e){
            Logger.error("post password failed", e);
            return new GeneralResponse(47, "store authenticating file failed");
        }
    }
    public GeneralResponse storeAvatarFile(Doctor doctor, Http.MultipartFormData.FilePart avatar){
        try{
            doctor.avatar = storeAvatarPhoto(doctor, avatar);
            doctorRepo.save(doctor);
            return new DoctorInfoResponse(DoctorInfo.genDoctorInfo(doctor));
        } catch (Exception e){
            Logger.error("post password failed", e);
            return new GeneralResponse(47, "store authenticating file failed");
        }
    }
    public GeneralResponse doctorLogin(DoctorLoginRequest request){
        try {
            Doctor doctor = doctorRepo.findByPhoneNumberAndPassword(request.phoneNumber, new String(Base64.decodeBase64(request.password)));
            if (doctor != null) {
                long timeStamp = System.currentTimeMillis();
                doctor.sessionKey = md5Hex(Long.toString(timeStamp));
                doctor.deviceType = request.devceType;
                doctorRepo.save(doctor);
                DoctorLoginInfo info = new DoctorLoginInfo();
                info.name = doctor.name;
                info.cellPhone = doctor.phoneNumber;
                info.doctorID = doctor.id;
                info.level = doctor.level;
                info.title = doctor.title;
                info.department = doctor.department.name;
                info.hospital = doctor.inHospital.name;
                info.plateUrl = doctor.plateUrl;
                info.jid = doctor.jid;
                info.imToken = doctor.xmppToken;
                info.sessionKey = doctor.sessionKey;
                info.goodAt = doctor.goodAt;
                info.achievement = doctor.achievement;
                info.background = doctor.background;
                info.serveMore = doctor.serveMore;
                info.imgTxtDiagFee = doctor.txtDiagFee;
                info.phnDiagFee = doctor.phnDiaFee;
                info.prvtDiagFee = doctor.prvtDiaFee;
                info.diagPlusFee = doctor.diagPlusFee;
                info.voltrWeekday = doctor.voltrDay;
                info.freeServing = doctor.freeServing;
                info.freeCount = doctor.freeCount;
                info.avatar = doctor.avatar;
                return new DoctorLoginResponse(info);
            } else {
                return new GeneralResponse(5, "Doctor login failed");
            }
        } catch (Exception e){
            Logger.error("Get doctor when login", e);
            return new GeneralResponse(36, "Get authenticated doctor failed");
        }
    }
    public Doctor getDoctorByHisPhone(String phone){
        return doctorRepo.findByPhoneNumber(phone);
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse updateInformation(Doctor doctor, DoctorInformationRequest request){
        Calendar oneMonAgo = new GregorianCalendar();
        oneMonAgo.setTime(new Date());
        oneMonAgo.add(Calendar.MONTH, -1);
        if (doctor.updateTime > oneMonAgo.getTime().getTime()){
            return new GeneralResponse(72, "You have updated information recently");
        } else {
            doctor.name = request.name;
            doctor.title = request.title;
            Department dp = departRepo.findByName(request.department);
            if (dp == null){
                doctor.department = new Department(request.department);
            } else {
                doctor.department = dp;
            }
            Hospital hp = hospitalRepo.findByName(request.hospital);
            if (hp == null){
                doctor.inHospital = new Hospital(request.hospital);
            } else {
                doctor.inHospital = hp;
            }
            doctorRepo.save(doctor);
            return new DoctorInfoResponse(DoctorInfo.genDoctorInfo(doctor));
        }

    }
    public boolean registerXmpServer(String jid, String password) throws Exception{
        try {
            ConnectionConfiguration config = new ConnectionConfiguration(ConfigureManager.xmppAdress, ConfigureManager.xmppPort, ConfigureManager.xmppServer);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setReconnectionAllowed(true);
            XMPPConnection connection = new XMPPTCPConnection(config);
            connection.connect();
            AccountManager manager = AccountManager.getInstance(connection);
            manager.createAccount(jid, password);
            connection.disconnect();
            return true;
        } catch (Exception e){
            Logger.error("Register im server failed", e);
            throw new IMServerException("Register: " + e.getMessage());
        }
    }
    public String storeLicensePhoto(Doctor doctor, Http.MultipartFormData.FilePart license){
        String storePath = Play.application().path() + "/store/";
        String docPlatePath = storePath + "doctor" + doctor.id + "/license/";
        File plateDir = new File(docPlatePath);
        if (!plateDir.exists()){
            plateDir.mkdirs();
        }
        String plateUrl = "";
        try {
            String fileName = license.getFilename();
            String newFlName;
            if (!fileName.contains(".")){
                newFlName = MD5Generator.getMd5Value(fileName + System.currentTimeMillis());
            }else {
                newFlName = MD5Generator.getMd5Value(fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()) + fileName.substring(fileName.lastIndexOf("."));
            }
            plateUrl = "/doctor/" + doctor.id + "/license/" + newFlName;
            FileUtils.moveFile(license.getFile(), new File(plateDir, newFlName));

        } catch (Exception e){
           Logger.error("store license failed", e);
        }
        return plateUrl;
    }
    public String storePlatePhoto(Doctor doctor, Http.MultipartFormData.FilePart plate){
        String storePath = Play.application().path() + "/store/";
        String docPlatePath = storePath + "doctor" + doctor.id + "/plate/";
        File plateDir = new File(docPlatePath);
        if (!plateDir.exists()){
            plateDir.mkdirs();
        }
        String plateUrl = "";
        try {
            String fileName = plate.getFilename();
            String newFlName;
            if (!fileName.contains(".")){
                newFlName = MD5Generator.getMd5Value(fileName + System.currentTimeMillis());
            }else {
                newFlName = MD5Generator.getMd5Value(fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()) + fileName.substring(fileName.lastIndexOf("."));
            }
            plateUrl = "/doctor/" + doctor.id + "/plate/" + newFlName;
            FileUtils.moveFile(plate.getFile(), new File(plateDir, newFlName));

        } catch (Exception e){
           Logger.error("store plate failed", e);
        }
        return plateUrl;
    }
    String storeAvatarPhoto(Doctor doctor, Http.MultipartFormData.FilePart avatar){
        String storePath = Play.application().path() + "/store/";
        String docPlatePath = storePath + "doctor" + doctor.id + "/avatar/";
        File plateDir = new File(docPlatePath);
        if (!plateDir.exists()){
            plateDir.mkdirs();
        }
        String avatarUrl = "";
        try {
            String fileName = avatar.getFilename();
            String newFlName;
            if (!fileName.contains(".")){
                newFlName = MD5Generator.getMd5Value(fileName + System.currentTimeMillis());
            }else {
                newFlName = MD5Generator.getMd5Value(fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()) + fileName.substring(fileName.lastIndexOf("."));
            }
            avatarUrl = "/doctor/" + doctor.id + "/avatar/" + newFlName;
            FileUtils.moveFile(avatar.getFile(), new File(plateDir, newFlName));

        } catch (Exception e){
            Logger.error("store plate failed", e);
        }
        return avatarUrl;
    }
    @Transactional
    public GeneralResponse reportInquiry(InquiryReportRequest request){
        try{
            InquiryReport report = new InquiryReport();
            report.description = request.description;
            report.suggestion = request.suggestion;
            report.createTime = System.currentTimeMillis();
            Inquiry inquiry =inquiryRepo.findOne(request.inquiryID);
            inquiry.report = report;
            inquiry.finished = true;
            inquiryRepo.save(inquiry);
            return new GeneralResponse();
        }catch (Exception e){
            Logger.error("store report failed", e);
            return new GeneralResponse(41, "Store report failed");
        }
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
    public GeneralResponse updateWorkTime(Doctor doctor, List<OutPatientTimeRequest> requests){
        if (doctor.workTimes != null){
            doctor.workTimes.clear();
        }
        for (OutPatientTimeRequest opReq: requests){
                OutPatientTime time = doctorRepo.getOutpatientTime(opReq.weekday, opReq.timeSegment);
                if (time != null) {
                    doctor.workTimes.add(time);
                } else {
                    doctor.workTimes.add(new OutPatientTime(opReq.weekday, opReq.timeSegment));
                }
        }
        doctorRepo.save(doctor);
        return new DoctorInfoResponse(DoctorInfo.genDoctorInfo(doctor));
    }
    public GeneralResponse getWorkTime(Doctor doctor){
        List<OutPatientTimeInfo> infos = new ArrayList<>();
        List<OutPatientTime> times = doctorRepo.getDoctorWorkTimes(doctor.id);
        for (OutPatientTime time: times){
            OutPatientTimeInfo info = new OutPatientTimeInfo(time.weekday, time.timeSegment);
            infos.add(info);
        }
        return new GetOutPatientTimeResponse(infos);
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse updateExtraInformation(Doctor doctor, UpdateExtraDocInfoRequest request){
        doctor.goodAt = request.goodAt;
        doctor.title = request.goodAt;
        doctor.achievement = request.achievement;
        doctor.background = request.background;
        doctor.serveMore = request.serveMore;
        doctorRepo.save(doctor);
        return new DoctorInfoResponse(DoctorInfo.genDoctorInfo(doctor));
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse updateServiceFee(Doctor doctor, UpdateDocServiceFeeRequest request){
        doctor.freeServing = request.freeServing;
        doctor.txtDiagFee = request.imgTxtDiagFee;
        doctor.phnDiaFee = request.phnDiagFee;
        doctor.prvtDiaFee = request.prvtDiagFee;
        doctor.diagPlusFee = request.diagPlusFee;
        doctor.voltrDay = request.voltrWeekday;
        doctor.freeCount = request.freeCount;
        doctorRepo.save(doctor);
        return new DoctorInfoResponse(DoctorInfo.genDoctorInfo(doctor));
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse addReminder(Doctor doctor, AddReminderInfoRequest request)throws Exception{
        Reminder reminder = new Reminder();
        SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
        if (!request.remindMe) {
            User user = userRepo.findOne(request.userID);
            reminder.user = user;
        }
        reminder.doctor = doctor;
        reminder.patientName = request.patientName;
        reminder.title = request.title;
        reminder.content = request.content;
        reminder.startTime = smf.parse(request.startDate);
        reminder.endTime = smf.parse(request.endDate);
        reminder.remindTime = request.remindTime.substring(0, request.remindTime.lastIndexOf(":") + 1) + "00";
        reminder.remindMe = request.remindMe;
        Reminder persReminder = reminderRepo.save(reminder);
        ReminderInfo info = new ReminderInfo();
        info.reminderID = persReminder.id;
        info.title = persReminder.title;
        info.content = persReminder.content;
        info.startDate = request.startDate;
        info.endDate = request.endDate;
        info.remindTime = request.remindTime;
        info.remindMe = request.remindMe;
        info.doctorID = request.doctorID;
        info.userID = request.userID;
        info.patientName = request.patientName;
        return new AddReminderResponse(info);
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse updateReminder(Doctor doctor, ReminderUpdateRequest request)throws Exception{
        Reminder reminder = reminderRepo.findOne(request.reminderID);
        SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
        if (!request.remindMe) {
            User user = userRepo.findOne(request.userID);
            reminder.user = user;
        }
        reminder.doctor = doctor;
        reminder.patientName = request.patientName;
        reminder.title = request.title;
        reminder.content = request.content;
        reminder.startTime = smf.parse(request.startDate);
        reminder.endTime = smf.parse(request.endDate);
        reminder.remindTime = request.remindTime.substring(0, request.remindTime.lastIndexOf(":") + 1) + "00";
        reminder.remindMe = request.remindMe;
        Reminder persReminder = reminderRepo.save(reminder);
        ReminderInfo info = new ReminderInfo();
        info.reminderID = persReminder.id;
        info.title = persReminder.title;
        info.content = persReminder.content;
        info.startDate = request.startDate;
        info.endDate = request.endDate;
        info.remindTime = request.remindTime;
        info.remindMe = request.remindMe;
        info.doctorID = request.doctorID;
        info.userID = request.userID;
        info.patientName = request.patientName;
        return new ReminderUpdateResponse(info);
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse deleteReminder(Doctor doctor, Long reminderID){
        Reminder reminder = reminderRepo.getReminderOfDoctor(reminderID, doctor.id);
        if (reminder != null) {
            reminderRepo.delete(reminder);
            return new GeneralResponse();
        } else {
            return new GeneralResponse(72, "You have not right to delete this reminder");
        }
    }
    public GeneralResponse getFriends(Doctor doctor, int page, int limit){
        List<FriendItem> friendItems = friendRepo.getFriendsOfDoctor(doctor.id, new PageRequest(page, limit)).getContent();
        List<UserInfo> userInfos = new ArrayList<>();
        for(FriendItem item: friendItems){
            UserInfo info = UserInfo.genUserInfo(item.user);
            userInfos.add(info);
        }
        return new GetFriendsResponse(userInfos);
    }
    public GeneralResponse getReminders(Doctor doctor){
        List<Reminder> reminders = reminderRepo.getRemindersOfDcotor(doctor.id);
        List<ReminderInfo> infos = new ArrayList<>();
        SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
        for (Reminder reminder: reminders){
            ReminderInfo info = new ReminderInfo();
            info.reminderID = reminder.id;
            info.title = reminder.title;
            info.content = reminder.content;
            info.startDate = smf.format(reminder.startTime);
            info.endDate = smf.format(reminder.endTime);
            info.remindTime = reminder.remindTime;
            info.remindMe = reminder.remindMe;
            info.doctorID = reminder.doctor.id;
            info.userID = reminder.user == null ? null : reminder.user.id;
            info.patientName = reminder.patientName;
            infos.add(info);
        }
        return new RemindersInfoResponse(infos);
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse bindClientID(Doctor doctor, BindClientIDRequest request){
        doctor.clientID = request.clientID;
        doctorRepo.save(doctor);
        return new DoctorInfoResponse(DoctorInfo.genDoctorInfo(doctor));
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse logout(Doctor doctor){
        doctor.clientID = "";
        doctorRepo.save(doctor);
        return new GeneralResponse();
    }
    public GeneralResponse getOrdersOfDoctor(Doctor doctor, int page, int limit){
        List<DoctorCommodityOrder> orders = orderRepo.getOrdersOfDoctor(doctor.id, new PageRequest(page, limit)).getContent();
        List<OrderInfo> infos = new ArrayList<>();
        for (DoctorCommodityOrder order : orders){
            OrderInfo info = OrderInfo.getOrderInfo(order);
            infos.add(info);
        }
        return new GetOrderResponse(infos);
    }
    public GeneralResponse getFriendsHealthRecord(Doctor doctor){
        List<FriendsHealthRecord> records = friendRepo.getFriendsHealthRecord(doctor.id);
        List<FriendHealthRecordInfo> infos = new ArrayList<>();
        for (FriendsHealthRecord record: records){
            FriendHealthRecordInfo info = new FriendHealthRecordInfo();
            info.friend = UserInfo.genUserInfo(record.user);
            if (record.record != null) {
                info.lastRecord = HealthRecordInfo.genRecordInfo(record.record);
            }
            infos.add(info);
        }
        return new GetFriendRecordInfoResponse(infos);
    }
    @Transactional
    public GeneralResponse getFriendHealthRecord(Long uid, GetHealthRecordRequest request){
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd");
        List<HealthRecord> records = healthRecRepo.getSpcificRecordOfUser(uid, request.category, request.subCategory);
        List<HealthRecordInfo> infos = new ArrayList<>();
        for (HealthRecord record: records){
            HealthRecordInfo info = new HealthRecordInfo();
            info.id = record.id;
            info.category = record.category;
            info.subCategory = record.subCategory;
            info.value = record.value;
            info.cycle = record.cycle;
            if (record.recordDate != null) {
                info.recordDate = smt.format(record.recordDate);
            }
            infos.add(info);
        }
        return new GetHealthRecordsResponse(infos);
    }
    public GeneralResponse getFriendsHealthRecord2(Doctor doctor){

        return new GeneralResponse();
    }
}
