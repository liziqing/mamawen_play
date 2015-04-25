package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.*;
import exception.IMServerException;
import models.Doctor;
import models.User;
import com.github.bingoohuang.patchca.random.RandUtils;
import play.Logger;
import play.cache.Cache;
import play.libs.Json;
import play.mvc.BodyParser;


import play.mvc.Http;
import play.mvc.Result;
import utils.SMSUtils;


/**
 * Created by Ronald on 2015/3/13.
 */
@org.springframework.stereotype.Controller
public class UserController extends BaseController{
    @BodyParser.Of(BodyParser.Json.class)
    public Result userLogin(){
        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            UserLoginRequest request = mapper.readValue(json.traverse(), UserLoginRequest.class);
            try {
                GeneralResponse response = userService.userLogin(request);
                return ok(Json.toJson(response));
            } catch (Exception e){
                return ok(Json.toJson(new GeneralResponse(7, "User login failed")));
            }
        }catch (Exception e){
            Logger.info(e.getMessage());
            return invalidJsonResponse();
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result userRegister(){
        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            UserRegisterRequest request = mapper.readValue(json.traverse(), UserRegisterRequest.class);
            try {
                GeneralResponse response = userService.userRegister(request);
                return ok(Json.toJson(response));
            } catch (Exception e){
                if (e instanceof IMServerException){
                    return ok(Json.toJson(new GeneralResponse(34, "Register IM server failed")));
                } else {
                    return ok(Json.toJson(new GeneralResponse(17, "Register user failed")));
                }
            }
        }catch (Exception e){
            Logger.info(e.getMessage());
            return invalidJsonResponse();
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateUserInfo(Long uid, String sessionKey){
        User user = verifyUser(uid, sessionKey);
        if(user != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                UpdateUserInfoRequest request = mapper.readValue(json.traverse(), UpdateUserInfoRequest.class);
                try {
                    GeneralResponse response = userService.updateUserPrivateInfo(user, request);
                    return ok(Json.toJson(response));
                } catch (Exception e){
                    Logger.error("update user info failed", e);
                    return ok(Json.toJson(new GeneralResponse(106, "Update user info failed.")));
                }
            } catch (Exception e) {
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result getValidateCode(String phoneNumber){
        User user= userService.getUserByHisPhone(phoneNumber);
        if (user == null) {
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
    @BodyParser.Of(BodyParser.Json.class)
    public Result validateCode(){
        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            ValidateCodeRequest request = mapper.readValue(json.traverse(), ValidateCodeRequest.class);
            String storeCode = (String)Cache.get(request.phoneNumber);
            if (storeCode != null && storeCode.equals(request.code)){
                Cache.remove(request.phoneNumber);
                return ok(Json.toJson(new GeneralResponse()));
            } else {
                return ok(Json.toJson(new GeneralResponse(27, "validate code is incorrect.")));
            }
        }catch (Exception e){
            Logger.info(e.getMessage());
            return invalidJsonResponse();
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result getExpertDoctor(Long uid, String sessionKey, int page, int limit){
        User user = verifyUser(uid, sessionKey);
        if (user != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                GetExpertDoctorRequest request = mapper.readValue(json.traverse(), GetExpertDoctorRequest.class);
                GeneralResponse response = userService.getExpertDoctor(request, page, limit);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("get expert failed", e);
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result userResetPassword(Long uid, String sessionKey){
        User user = verifyUser(uid, sessionKey);
        if (user != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                PasswordResetRequest request = mapper.readValue(json.traverse(), PasswordResetRequest.class);
                if (uid.equals(request.id)) {
                    GeneralResponse response = userService.resetPassword(user,request);
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
    public Result userResetPasswordByPhone(){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                PasswordResetByPhoneRequest request = mapper.readValue(json.traverse(),PasswordResetByPhoneRequest .class);
                GeneralResponse response = userService.resetPasswordByPhone(request);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info(e.getMessage());
                return invalidJsonResponse();
            }
    }
    public Result userUploadAvatar(Long uid, String sessionKey){
        User user = verifyUser(uid, sessionKey);
        if (user != null){
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart avatar = body.getFile("avatar");
            if (avatar != null) {
                GeneralResponse response = userService.storeAvatarFile(user, avatar);
                return ok(Json.toJson(response));
            } else {
                return ok(Json.toJson(new GeneralResponse(11, "Please push necessary data")));
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result addHealthRecord(Long uid, String sessionKey){
        User user = verifyUser(uid, sessionKey);
        if (user != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                AddHealthRecordRequest request = mapper.readValue(json.traverse(), AddHealthRecordRequest.class);
                try {
                    GeneralResponse response = userService.addHealthRecorde(user, request);
                    return ok(Json.toJson(response));
                } catch (Exception e){
                    Logger.error("store health record failed", e);
                    return ok(Json.toJson(new GeneralResponse(101, "Store health record failed")));
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
    public Result getHealthRecords(Long uid, String sessionKey){
        User user = verifyUser(uid, sessionKey);
        if (user != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                GetHealthRecordRequest request = mapper.readValue(json.traverse(), GetHealthRecordRequest.class);
                try {
                    GeneralResponse response = userService.getHealthRecords(user, request);
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
    @BodyParser.Of(BodyParser.Json.class)
    public Result bindClientID(Long uid, String sessionKey){
        User user = verifyUser(uid, sessionKey);
        if (user != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                BindClientIDRequest request = mapper.readValue(json.traverse(),BindClientIDRequest.class);
                try {
                    GeneralResponse response = userService.bindClientID(user, request);
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
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateBasicInfo(Long uid, String sessionKey){
        User user = verifyUser(uid, sessionKey);
        if (user != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                UpdateBasicUserInfoRequest request = mapper.readValue(json.traverse(), UpdateBasicUserInfoRequest.class);
                try {
                    GeneralResponse response = userService.updateBasicUserInfo(user, request);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    Logger.error("Update user basic info failed", e);
                    return ok(Json.toJson(new GeneralResponse(107, "Update user basic info failed")));
                }
            } catch (Exception e){
                return invalidJsonResponse();
            }

        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result addSuggestion(Long uid, String sessionKey){
        User user = verifyUser(uid, sessionKey);
        if (user != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                PostSuggestionRequest request = mapper.readValue(json.traverse(), PostSuggestionRequest.class);
                try {
                    GeneralResponse response = userService.addSuggestion(user, request);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    Logger.error("add suggestion failed", e);
                    return ok(Json.toJson(new GeneralResponse(108, "Add suggestion failed")));
                }
            } catch (Exception e){
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    @BodyParser.Of(BodyParser.Json.class)
    public Result mark(Long uid, String sessionKey){
        User user = verifyUser(uid, sessionKey);
        if (user != null){
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                UserMarkRequest request = mapper.readValue(json.traverse(), UserMarkRequest.class);
                try {
                    GeneralResponse response = userService.userSendMark(user, request);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    Logger.error("mark for doctor failed", e);
                    return ok(Json.toJson(new GeneralResponse(109, "Mark suggestion failed")));
                }
            } catch (Exception e){
                return invalidJsonResponse();
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result logout(Long uid, String sessionKey){
        User user= verifyUser(uid, sessionKey);
        if (user != null) {
            try{
                GeneralResponse response = userService.logout(user);
                return ok(Json.toJson(response));
            } catch (Exception e){
                Logger.error("Logout failed", e);
                return ok(Json.toJson(new GeneralResponse(104, "Logout failed")));
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
}
