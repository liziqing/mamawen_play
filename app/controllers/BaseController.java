package controllers;

import dto.GeneralResponse;
import models.Doctor;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;

/**
 * Created by Ronald on 2015/3/11.
 */
@org.springframework.stereotype.Controller
public class BaseController extends Controller {
    @Autowired
    UserService userService;
    @Autowired
    InquiryService inquiryService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    IMService imService;
    @Autowired
    MallService mallService;

    Doctor verifyDoctor(long uid, String sessionKey){
        Doctor doctor = userService.verifyDoctor(uid, sessionKey);
        if (doctor != null){
            doctor.lastActiveTime = System.currentTimeMillis();
        }
        return doctor;
    }
    User verifyUser(long uid, String sessionKey){
        return userService.verifyUser(uid, sessionKey);
    }

    Result invalidJsonResponse(){
        return ok(Json.toJson(new GeneralResponse(2, "Invalid json format")));
    }
}
