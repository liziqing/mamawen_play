package controllers;

import dto.BaseCommodityInfo;
import dto.GeneralResponse;
import dto.RushEventInfo;
import models.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import play.libs.Json;
import play.mvc.Result;
import service.MallService;

import java.util.List;

/**
 * Created by Ronald on 2015/4/14.
 */
@org.springframework.stereotype.Controller
public class MallController extends BaseController{
    @Autowired
    MallService mallService;
    public Result mallEntry(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
           List<BaseCommodityInfo> commodities = mallService.getFreeCommodities();
           return ok(views.html.mall_entry.render(commodities, uid, sessionKey));
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
    public Result getCommoditySummary(Long cid, Long uid, String sessionKey){
        //return ok(views.html.commodity_sum.render(cid));
        return ok("ok");
    }
    public Result getCommoditySummaryInEvent(Long eid, Long cid, Long uid, String sessionKey){
        if(mallService.canBuyNow(eid)) {
            //return ok(views.html.commodity_sum.render(cid));
            return ok("ok");
        } else {
            return ok("抢购还没开始！");
        }
    }
    public Result getCommodityList(Long uid, String sessionKey){
        Doctor doctor = verifyDoctor(uid, sessionKey);
        if (doctor != null){
            List<RushEventInfo> events = mallService.getRushEvents();
            return ok(views.html.commodity_list.render(events, uid, sessionKey));
        } else {
            return ok(Json.toJson(new GeneralResponse(1, "Authentication not passed")));
        }
    }
}
