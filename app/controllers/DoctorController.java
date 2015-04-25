package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bingoohuang.patchca.random.RandUtils;
import dto.*;
import exception.IMServerException;
import models.Doctor;
import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.SMSUtils;

import javax.print.Doc;

/**
 * Created by Ronald on 2015/3/11.
 */

@org.springframework.stereotype.Controller
public class DoctorController extends BaseController{
    @BodyParser.Of(BodyParser.Json.class)
    public Result getVolunteerDoctors(Long uid, String sessionKey, Integer page, Integer limit){
        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            GetVolunteerDoctorRequest request = mapper.readValue(json.traverse(), GetVolunteerDoctorRequest.class);
            GeneralResponse response = doctorService.getVolunteerDoctor(request.department, page, limit);
            return ok(Json.toJson(response));
        }catch (Exception e){
            Logger.info(e.getMessage());
            return invalidJsonResponse();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result doctorTestRegister(){
        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            RegisterDoctorTestRequest request = mapper.readValue(json.traverse(), RegisterDoctorTestRequest.class);
            GeneralResponse response = doctorService.doctorTestRegister(request);
            return ok(Json.toJson(response));
        }catch (Exception e){
            Logger.info(e.getMessage());
            return invalidJsonResponse();
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result doctorResetPassword(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                PasswordResetRequest request = mapper.readValue(json.traverse(), PasswordResetRequest.class);
                if (uid.equals(request.id)) {
                    GeneralResponse response = doctorService.resetPassword(doctor, request);
                    return ok(Json.toJson(response));
                } else {
                    return ok(Json.toJson(new GeneralResponse(64, "Have no right to change password")));
                }

            } catch (Exception e) {
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result doctorResetPasswordByPhone(){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                PasswordResetByPhoneRequest request = mapper.readValue(json.traverse(), PasswordResetByPhoneRequest.class);
                GeneralResponse response = doctorService.resetPasswordByPhone(request);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result doctorRegister(){
        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            DoctorRegisterRequest request = mapper.readValue(json.traverse(), DoctorRegisterRequest.class);
            try {
                GeneralResponse response = doctorService.doctorRegister(request);
                return ok(Json.toJson(response));
            } catch (Exception e){
                if (e instanceof IMServerException){
                    return ok(Json.toJson(new GeneralResponse(34, "Register IM server failed")));
                } else {
                    return ok(Json.toJson(new GeneralResponse(48, "Store doctor info failed")));
                }
            }
        }catch (Exception e){
            Logger.info(e.getMessage());
            return invalidJsonResponse();
        }
    }
    public Result getValidateCode(String phoneNumber){
        Doctor doctor = doctorService.getDoctorByHisPhone(phoneNumber);
        if (doctor == null) {
            String code = RandUtils.randNum(6);
            Cache.set(phoneNumber, code, 60);
            try {
                if (SMSUtils.sendValidateCode(phoneNumber, code)) {
                    return ok(Json.toJson(new GeneralResponse()));
                } else {
                    return ok(Json.toJson(new GeneralResponse(28, "Get validate code failed")));
                }
            } catch (Exception e) {
                Logger.error(e.getMessage());
                return ok(Json.toJson(new GeneralResponse()));
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(33, "This phone has been used")));
        }
    }
    public Result doctorAuthenticate(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart plate = body.getFile("plate");
            Http.MultipartFormData.FilePart license = body.getFile("license");
            if (plate != null || license != null) {
                GeneralResponse response = doctorService.storeAuthenFile(doctor, plate, license);
                return ok(Json.toJson(response));
            } else {
                return ok(Json.toJson(new GeneralResponse(11, "Please push necessary data")));
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result doctorUploadAvatar(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart avatar = body.getFile("avatar");
            if (avatar != null) {
                GeneralResponse response = doctorService.storeAvatarFile(doctor, avatar);
                return ok(Json.toJson(response));
            } else {
                return ok(Json.toJson(new GeneralResponse(11, "Please push necessary data")));
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateDoctorInfo(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                 DoctorInformationRequest request = mapper.readValue(json.traverse(), DoctorInformationRequest.class);
                try {
                    GeneralResponse response = doctorService.updateInformation(doctor, request);
                    return ok(Json.toJson(response));
                }catch (Exception e){
                    Logger.error("update failed", e);
                    return ok(Json.toJson(new GeneralResponse(55, "Store doctor information failed")));
                }
            }catch (Exception e){
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result doctorLogin(){
        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            DoctorLoginRequest request = mapper.readValue(json.traverse(), DoctorLoginRequest.class);

            GeneralResponse response = doctorService.doctorLogin(request);
            return ok(Json.toJson(response));
        }catch (Exception e){
            Logger.info(e.getMessage());
            return invalidJsonResponse();
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result reportInquiry(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            try {
                JsonNode json = request().body().asJson();
                ObjectMapper mapper = new ObjectMapper();
                InquiryReportRequest request = mapper.readValue(json.traverse(), InquiryReportRequest.class);
                GeneralResponse response = doctorService.reportInquiry(request);
                return ok(Json.toJson(response));
            }catch (Exception e){
                return ok(Json.toJson(new GeneralResponse(2, "Invalid json format")));
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result getUserRecords(Long id, Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            GeneralResponse response = doctorService.getUserRecords(id);
            return ok(Json.toJson(response));

        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result updateWorkTime(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            try{
                JsonNode json = request().body().asJson();
                ObjectMapper mapper = new ObjectMapper();
                List<OutPatientTimeRequest> opRequests = Arrays.asList(mapper.readValue(json.traverse(), OutPatientTimeRequest[].class));
                try{
                    GeneralResponse response = doctorService.updateWorkTime(doctor, opRequests);
                    return ok(Json.toJson(response));
                } catch (Exception e){
                    Logger.error("update work time failed", e);
                    return ok(Json.toJson(new GeneralResponse(55, "Store doctor information failed")));
                }
            } catch (Exception e){
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }

        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result getWorkTime(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            return ok(Json.toJson(doctorService.getWorkTime(doctor)));
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateDoctorExtraInfo(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                UpdateExtraDocInfoRequest request = mapper.readValue(json.traverse(),UpdateExtraDocInfoRequest .class);
                try {
                    GeneralResponse response = doctorService.updateExtraInformation(doctor, request);
                    return ok(Json.toJson(response));
                }catch (Exception e){
                    Logger.error("update extra info failed", e);
                    return ok(Json.toJson(new GeneralResponse(55, "Store doctor extra information failed")));
                }
            }catch (Exception e){
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateDoctorServeFee(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                UpdateDocServiceFeeRequest request = mapper.readValue(json.traverse(),UpdateDocServiceFeeRequest.class);
                try {
                    GeneralResponse response = doctorService.updateServiceFee(doctor, request);
                    return ok(Json.toJson(response));
                }catch (Exception e){
                    Logger.error("Update serve fee failed", e);
                    return ok(Json.toJson(new GeneralResponse(55, "Store doctor serving fee failed")));
                }
            }catch (Exception e){
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result addReminder(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                AddReminderInfoRequest request = mapper.readValue(json.traverse(),AddReminderInfoRequest.class);
                try {
                    GeneralResponse response = doctorService.addReminder(doctor, request);
                    return ok(Json.toJson(response));
                }catch (Exception e){
                    Logger.error("store reminder failed", e);
                    return ok(Json.toJson(new GeneralResponse(55, "Store reminder failed")));
                }
            }catch (Exception e){
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateReminder(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                ReminderUpdateRequest request = mapper.readValue(json.traverse(),ReminderUpdateRequest.class);
                try {
                    GeneralResponse response = doctorService.updateReminder(doctor, request);
                    return ok(Json.toJson(response));
                }catch (Exception e){
                    Logger.error("update reminder failed", e);
                    return ok(Json.toJson(new GeneralResponse(55, "update reminder failed")));
                }
            }catch (Exception e){
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteReminder(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                ReminderDeleteRequest request = mapper.readValue(json.traverse(),ReminderDeleteRequest.class);
                try {
                    GeneralResponse response = doctorService.deleteReminder(doctor, request.reminderID);
                    return ok(Json.toJson(response));
                }catch (Exception e){
                    Logger.error("delete reminder failed", e);
                    return ok(Json.toJson(new GeneralResponse(55, "delete reminder failed")));
                }
            }catch (Exception e){
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result getUserUseingThisPhone(String phone, Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            GeneralResponse response = doctorService.userUsingPhoneExist(phone);
            return ok(Json.toJson(response));
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result getFriendsList(Long uid, String  sessionKey, int page, int limit){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            GeneralResponse response = doctorService.getFriends(doctor, page, limit);
            return ok(Json.toJson(response));
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
   }
    @BodyParser.Of(BodyParser.Json.class)
    public Result sendCustomSMS(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                SmsSenderRequest request = mapper.readValue(json.traverse(),SmsSenderRequest.class);
                GeneralResponse response = doctorService.sendSms(doctor, request);
                return ok(Json.toJson(response));
            } catch (Exception e){
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result getReminder(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            return ok(Json.toJson(doctorService.getReminders(doctor)));
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }

    }
    public Result getOrderList(Long uid, String sessionKey, int page, int limit){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            return ok(Json.toJson(doctorService.getOrdersOfDoctor(doctor, page, limit)));
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result bindClientID(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                BindClientIDRequest request = mapper.readValue(json.traverse(),BindClientIDRequest.class);
                try {
                    GeneralResponse response = doctorService.bindClientID(doctor, request);
                    return ok(Json.toJson(response));
                } catch (Exception e){
                    Logger.error("bind client failed", e);
                    return ok(Json.toJson(new GeneralResponse(103, "Bind client failed")));
                }
            } catch (Exception e) {
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result logout(Long uid, String sessionKey){
        Doctor doctor= verifyDoctor(uid, sessionKey);
        if (doctor != null) {
            try{
                GeneralResponse response = doctorService.logout(doctor);
                return ok(Json.toJson(response));
            } catch (Exception e){
                Logger.error("Logout failed", e);
                return ok(Json.toJson(new GeneralResponse(104, "Logout failed")));
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result getFriendsRecord(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            try{
                GeneralResponse response = doctorService.getFriendsHealthRecord(doctor);
                return ok(Json.toJson(response));
            } catch (Exception e){
                Logger.error("get health records failed", e);
                return ok(Json.toJson(new GeneralResponse(105, "Get friends record list failed")));
            }

        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result getFriendRecord(Long fid, Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                GetHealthRecordRequest request = mapper.readValue(json.traverse(), GetHealthRecordRequest.class);
                try {
                    GeneralResponse response = doctorService.getFriendHealthRecord(fid, request);
                    return ok(Json.toJson(response));
                } catch (Exception e){
                    Logger.error("Get health record failed", e);
                    return ok(Json.toJson(new GeneralResponse(102, "Get health record failed")));
                }
            } catch (Exception e) {
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
}
