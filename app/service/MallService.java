package service;

import dto.*;
import models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repo.MallRepository;
import repo.RushEventRepository;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by Ronald on 2015/4/13.
 */
@Service
public class MallService extends AuthenticationService {
    @Autowired
    MallRepository mallRepo;
    public List<RushEventInfo> getRushEvents() {
        List<RushEvent> events = new ArrayList<>();
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        Calendar cur = new GregorianCalendar();
        cur.setTime(curDate);
        int curHour = cur.get(Calendar.HOUR_OF_DAY);
        try {
            int weekday = cur.get(Calendar.DAY_OF_WEEK);
            if (curHour >= 7) {
                if (weekday < 6) {
                    String stString = "1970-01-0" + weekday + " 12:00:00";
                    Date start = smt.parse(stString);
                    String edString = "1970-01-0" + (1 + weekday) + " 12:00:00";
                    Date end = smt.parse(edString);
                    events = eventRepo.getRushEventInRange(start, end);
                } else {
                    String stString = "1970-01-07 12:00:00";
                    Date start = smt.parse(stString);
                    String edString = "1970-01-08 00:00:00";
                    Date end = smt.parse(edString);
                    List<RushEvent> first = eventRepo.getRushEventInRange(start, end);
                    events.addAll(first);
                    stString = "1970-01-01 00:00:00";
                    start = smt.parse(stString);
                    edString = "1970-01-01 12:00:00";
                    end = smt.parse(edString);
                    List<RushEvent> second = eventRepo.getRushEventInRange(start, end);
                    events.addAll(second);
                }
            } else {
                if (weekday > 1) {
                    String stString = "1970-01-0" + (weekday - 1) + " 12:00:00";
                    Date start = smt.parse(stString);
                    String edString = "1970-01-0" + weekday + " 12:00:00";
                    Date end = smt.parse(edString);
                    events = eventRepo.getRushEventInRange(start, end);
                } else {
                    String stString = "1970-01-07 12:00:00";
                    Date start = smt.parse(stString);
                    String edString = "1970-01-08 00:00:00";
                    Date end = smt.parse(edString);
                    List<RushEvent> first = eventRepo.getRushEventInRange(start, end);
                    events.addAll(first);
                    stString = "1970-01-01 00:00:00";
                    start = smt.parse(stString);
                    edString = "1970-01-01 12:00:00";
                    end = smt.parse(edString);
                    List<RushEvent> second = eventRepo.getRushEventInRange(start, end);
                    events.addAll(second);

                }
            }
        } catch (Exception e) {

        }
        List<RushEventInfo> eventInfos = new ArrayList<>();
        SimpleDateFormat curTimeForm = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat curDateForm = new SimpleDateFormat("yyyy-MM-dd");
        for (RushEvent event : events) {
            RushEventInfo evt = new RushEventInfo();
            String curTime = curTimeForm.format(event.startTime);
            evt.eventID = event.id;
            evt.startTime = curTime.substring(0, curTime.lastIndexOf(":"));
            Calendar evtCal = new GregorianCalendar();
            evtCal.setTime(event.startTime);
            Calendar tommorw = new GregorianCalendar();
            tommorw.setTime(curDate);
            tommorw.add(Calendar.DAY_OF_YEAR, 1);
            Calendar yesterday = new GregorianCalendar();
            yesterday.setTime(curDate);
            yesterday.add(Calendar.DAY_OF_YEAR, -1);
            if (curHour >= 7){
                if (evtCal.get(Calendar.HOUR_OF_DAY) > 12){
                    evt.realTime = curDateForm.format(curDate) + " " + curTime;
                } else {
                    evt.realTime = curDateForm.format(tommorw.getTime()) + " " + curTime;
                }
            } else {
                if (evtCal.get(Calendar.HOUR_OF_DAY) > 12){
                    evt.realTime = curDateForm.format(yesterday.getTime()) + " " + curTime;
                } else {
                    evt.realTime = curDateForm.format(curDate) + " " + curTime;
                }

            }

            evt.commodities = new ArrayList<>();
            for (Commodity good : event.goods) {
                BaseCommodityInfo info = new BaseCommodityInfo();
                info.name = good.name;
                info.description = good.description;
                if (good.cashPrice != null) {
                    info.price = good.discCashPrice;
                    info.origPrice = good.cashPrice;
                } else {
                    info.price = good.discPointPrice;
                    info.origPrice = good.pointPrice;
                }
                info.thumbUrl = good.thumbUrl;
                if (good.categories.size() == 0){
                    info.stock = good.stock;
                } else {
                    CommodityCategory cat = good.categories.get(0);
                    int totalStock = 0;
                    for (CommodityLabel label : cat.labels){
                        totalStock += label.stock;
                    }
                    good.stock = totalStock;
                }
                evt.commodities.add(info);
            }
            eventInfos.add(evt);
        }
        return eventInfos;
    }

    public List<BaseCommodityInfo>getFreeCommodities() {
        List<FreeCommodity> freeCommodities = mallRepo.getAllFreeCommodity();
        List<BaseCommodityInfo> infos = new ArrayList<>();
        for (FreeCommodity comm : freeCommodities) {
            Commodity goods = comm.commodity;
            BaseCommodityInfo info = new BaseCommodityInfo();
            info.description = goods.description;
            info.name = goods.name;
            info.cid = goods.id;
            if (goods.cashPrice != null) {
                info.price = goods.cashPrice;
                info.origPrice = goods.discCashPrice;
            } else {
                info.price = goods.pointPrice;
                info.origPrice = goods.discPointPrice;
            }
            info.thumbUrl = goods.thumbUrl;
            if (goods.categories.size() == 0){
                info.stock = goods.stock;
            } else {
                CommodityCategory cat = goods.categories.get(0);
                int totalStock = 0;
                for (CommodityLabel label : cat.labels){
                    totalStock += label.stock;
                }
                goods.stock = totalStock;
            }
            infos.add(info);
        }
        return  infos;
    }
    public boolean canBuyNow(Long eid){
        SimpleDateFormat timeForm = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        RushEvent evt = eventRepo.findOne(eid);
        Date cur = new Date();
        Calendar curCal = new GregorianCalendar();
        curCal.setTime(cur);
        int weekday = curCal.get(Calendar.DAY_OF_WEEK);
        Calendar evtCal = new GregorianCalendar();
        evtCal.setTime(evt.startTime);
        String evtTime = timeForm.format(evt.startTime);
        String curDate = dateForm.format(evt.startTime);
        int evtDay = evtCal.get(Calendar.DAY_OF_MONTH);
        if (weekday > evtDay){
            return true;
        } else if (weekday == evtDay){
            try {
                Date evtDate = smt.parse(curDate + " " + evtTime);
                if (cur.getTime() >= evtDate.getTime()){
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e){
                return false;
            }
        } else {
            if (evtDay == 7 && weekday == 1) {
                return true;
            } else {
                return false;
            }
        }
    }
    //public BaseCommodityInfo getCommoditySum(Long cid){
   //     Commodity goods = mallRepo.findOne(cid);

    //}
}
