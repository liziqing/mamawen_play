package controllers;

import play.Play;
import play.mvc.Result;

import java.io.File;

/**
 * Created by Ronald on 2015/3/11.
 */
@org.springframework.stereotype.Controller
public class FileController extends BaseController {
    public Result getInquiryPhoto(Long uid, Long inqID, String fileName){
        String storePath = Play.application().path() + "/store/";
        String inqPhotoPath = storePath + "user" + uid + "/" + inqID + "/photos/";
        File photoDir = new File(inqPhotoPath);
        return ok(new File(photoDir, fileName));
    }
    public Result getPlatePhoto(Long doctorID,  String fileName){
        String storePath = Play.application().path() + "/store/";
        String platePhtPath = storePath + "doctor" + doctorID + "/plate/";
        File photoDir = new File(platePhtPath);
        return ok(new File(photoDir, fileName));
    }
    public Result getLicensePhoto(Long doctorID,  String fileName){
        String storePath = Play.application().path() + "/store/";
        String platePhtPath = storePath + "doctor" +  doctorID + "/license/";
        File photoDir = new File(platePhtPath);
        return ok(new File(photoDir, fileName));
    }
    public Result getChatImage(String id, String fileName){
        String storePath = Play.application().path() + "/store/chats/" + id;
        return ok(new File(storePath, fileName));
    }
    public Result getChatAudio(String id, String file) {
        String storePath = Play.application().path() + "/store/chats/" + id;
        return ok(new File(storePath, file));
    }
    public Result getDoctorAvatar(Long id, String file){
        String storePath = Play.application().path() + "/store/";
        String avatarPhtPath = storePath + "doctor" + id + "/avatar/";
        return ok(new File(avatarPhtPath,file));
    }
    public Result getUserAvatar(Long id, String file){
        String storePath = Play.application().path() + "/store/";
        String avatarPhtPath = storePath + "user" + id + "/avatar/";
        return ok(new File(avatarPhtPath,file));
    }

}
