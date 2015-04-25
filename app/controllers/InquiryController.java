package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.GeneralResponse;
import dto.PushInquiryRequest;
import dto.ReceptInquiryRequest;
import models.Doctor;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ronald on 2015/3/3.
 */
@org.springframework.stereotype.Controller
public class InquiryController extends BaseController {
    @BodyParser.Of(BodyParser.Json.class)
    public Result pushInquiryTest() {
        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<String> tags = mapper.readValue(json.traverse(), new TypeReference<List<String>>() {
            });
            inquiryService.testMany2ManyRelation(tags);

            return ok(Json.toJson(new GeneralResponse()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return invalidJsonResponse();
        }
    }

    public Result queryInquiry(String tag) {
       /* List<Inquiry> inqs = inquiryService.queryInquiry(tag);
        List<InquiryMessage> msgs = new ArrayList<>();
        for (Inquiry inq : inqs){
            InquiryMessage msg = new InquiryMessage();
            msg.description = inq.description;
            msg.point = inq.point;
            msgs.add(msg);
        }
        return ok(Json.toJson(msgs));*/
        return ok("aa");
    }
    public  Result getInquiryDetail(Long uid, String sessionkey, Long inqID){
        return ok(Json.toJson(inquiryService.getInquiryDetail(inqID))) ;
    }
    public Result pushInquiry(Long uid, String sessionKey) {
        User user = verifyUser(uid, sessionKey);
        if (user != null) {
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Map<String, String[]> data = body.asFormUrlEncoded();
            if (data.size() != 0 && data.containsKey("inquiry")) {
                String[] inqReq = data.get("inquiry");
                if (inqReq.length > 0) {
                    String inqString = inqReq[0];
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        PushInquiryRequest inquiry = mapper.readValue(inqString, PushInquiryRequest.class);
                        List<Http.MultipartFormData.FilePart> photos = new ArrayList<>();
                        for (int i = 1; i < 5; i++) {
                            Http.MultipartFormData.FilePart photo = body.getFile("photo_" + i);
                            if (photo != null) {
                                photos.add(photo);
                            }
                        }
                        try {
                            GeneralResponse response = inquiryService.pushInquiry(user, inquiry, photos);
                            return ok(Json.toJson(response));
                        }catch (Exception e){
                            Logger.error("store inq failed", e);
                            return ok(Json.toJson(new GeneralResponse(21, "Store inquiry failed")));
                        }
                    } catch (Exception e) {
                        return ok(Json.toJson(new GeneralResponse(2, "Invalid json format")));
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
    public Result getInquiryList(Long uid, String sessionKey,int page, int limit){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null) {
            try {
                try {
                    GeneralResponse response = inquiryService.getInquiryList(doctor, page, limit);
                    return ok(Json.toJson(response));
                } catch (Exception e){
                    Logger.error("get failed",e);
                    return ok(Json.toJson(new GeneralResponse(31, "Get inquiry failed")));
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
    public Result receptInquiry(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);

        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        try{
            ReceptInquiryRequest req = mapper.readValue(json.traverse(), ReceptInquiryRequest.class);
            try {
                GeneralResponse response = inquiryService.receptInquiry(doctor, req.inquiryID);
                return ok(Json.toJson(response));
            } catch (Exception e){
                Logger.error("recept failed",e);
                return ok(Json.toJson(new GeneralResponse(34, "Recept inquiry failed")));
            }
        } catch (Exception e){
            Logger.info(e.getMessage());
            return invalidJsonResponse();
        }
    }


}


