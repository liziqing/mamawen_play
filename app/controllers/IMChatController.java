package controllers;

import akka.actor.FSM;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.BaseChatMessage;
import dto.GeneralResponse;
import dto.NormalChatMessage;
import dto.ReportChatMessage;
import exception.IMServerException;
import models.Doctor;
import models.User;
import org.springframework.stereotype.Controller;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Map;

/**
 * Created by Ronald on 2015/3/24.
 */
@Controller
public class IMChatController extends BaseController{
    public Result doctorPostChat(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Map<String, String[]> data = body.asFormUrlEncoded();
            if (data.size() !=0 && data.containsKey("message")) {
                String[] msgs = data.get("message");
                if (msgs.length > 0) {
                    try {
                        JsonNode json = Json.parse(msgs[0]);
                        if (json.hasNonNull("category") && json.get("category").isInt()) {
                            int cat = json.findValue("category").asInt();
                            ObjectMapper mapper = new ObjectMapper();
                            GeneralResponse response;
                            switch (cat) {
                                case 1:
                                    NormalChatMessage nChatMsg = mapper.readValue(json.traverse(), NormalChatMessage.class);
                                    response = imService.doctorSendNormalChat(doctor, nChatMsg, body);
                                    return ok(Json.toJson(response));
                                case 2:
                                    BaseChatMessage bChatMsg = mapper.readValue(json.traverse(), BaseChatMessage.class);
                                    response = imService.doctorSendReceptMessage(doctor, bChatMsg);
                                    return ok(Json.toJson(response));
                                case 3:
                                    ReportChatMessage rChatMsg = mapper.readValue(json.traverse(), ReportChatMessage.class);
                                    response = imService.doctorSendReportMessage(doctor, rChatMsg);
                                    return ok(Json.toJson(response));
                                default:
                                    return ok(Json.toJson(new GeneralResponse(60, "Invalid type of message")));
                            }
                        } else {
                            return ok(Json.toJson(new GeneralResponse(11, "Please push necessary data")));
                        }
                    } catch (Exception e){
                        if (e instanceof IMServerException){
                            return ok(Json.toJson(new GeneralResponse(53, "Send message failed")));

                        } else if (e instanceof JsonParseException || e instanceof JsonMappingException) {
                            return ok(Json.toJson(new GeneralResponse(2, "Invalid json format")));
                        } else {
                            return ok(Json.toJson(new GeneralResponse(56, "Store chat record failed")));
                        }
                    }
                } else {
                    return ok(Json.toJson(new GeneralResponse(11, "Please push necessary data")));
                }
            } else {
                return ok(Json.toJson(new GeneralResponse(11, "Please push necessary data")));
            }
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
   public Result userPostChat(Long uid, String sessionKey){
       User user = verifyUser(uid, sessionKey);
       if (user != null){
           Http.MultipartFormData body = request().body().asMultipartFormData();
            Map<String, String[]> data = body.asFormUrlEncoded();
            if (data.size() !=0 && data.containsKey("message")) {
                String[] msgs = data.get("message");
                if (msgs.length > 0) {
                    try {
                        JsonNode json = Json.parse(msgs[0]);
                        if (json.hasNonNull("category") && json.get("category").isInt()) {
                            int cat = json.findValue("category").asInt();
                            ObjectMapper mapper = new ObjectMapper();
                            GeneralResponse response;
                            switch (cat) {
                                case 1:
                                    NormalChatMessage nChatMsg = mapper.readValue(json.traverse(), NormalChatMessage.class);
                                    response = imService.userSendNormalMessage(user, nChatMsg, body);
                                    return ok(Json.toJson(response));
                                default:
                                    return ok(Json.toJson(new GeneralResponse(60, "Invalid type of message")));
                            }
                        } else {
                            return ok(Json.toJson(new GeneralResponse(11, "Please push necessary data")));
                        }
                    } catch (Exception e){
                        if (e instanceof IMServerException){
                            return ok(Json.toJson(new GeneralResponse(53, "Send message failed")));

                        } else if (e instanceof JsonParseException || e instanceof JsonMappingException) {
                            return ok(Json.toJson(new GeneralResponse(2, "Invalid json format")));
                        } else {
                            return ok(Json.toJson(new GeneralResponse(56, "Store chat record failed")));
                        }
                    }
                } else {
                    return ok(Json.toJson(new GeneralResponse(11, "Please push necessary data")));
                }
            } else {
                return ok(Json.toJson(new GeneralResponse(11, "Please push necessary data")));
            }
       } else {
           return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
       }
   }
   public Result testSinglePush(){
       return ok(Json.toJson(new GeneralResponse()));
   }
}
