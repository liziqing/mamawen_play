package service;

import actor.IMScheduler;
import actor.message.IMSenderMessage;
import dto.*;
import models.*;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import play.Play;
import play.mvc.Http;
import utils.MD5Generator;

import java.io.File;
import java.util.*;

/**
 * Created by Ronald on 2015/3/3.
 */
@Service
public class InquiryService extends AuthenticationService {
    static final Object lock = new Object();
    public List<Inquiry> getInquiriesInRange() {
        Date cur = new Date();
        Calendar twoMinAgo = new GregorianCalendar();
        twoMinAgo.setTime(cur);
        twoMinAgo.add(Calendar.HOUR_OF_DAY, -1);
        return inquiryRepo.getUnreceptLevel1Inquiry(twoMinAgo.getTime(), cur);
    }

    public String receptInquiry(Long docID, Long inqID) {
        Doctor doctor = doctorRepo.findOne(docID);
        Inquiry inq = inquiryRepo.findOne(inqID);

        inq.recepted = true;
        inq.doctor = doctor;
        inquiryRepo.save(inq);

        return doctor.name + "已经接受了";
    }

    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse pushInquiry(User user, PushInquiryRequest inqRequest, List<Http.MultipartFormData.FilePart> photos) {
        try {
            Inquiry inquiry = new Inquiry();
            inquiry.description = inqRequest.description;
            inquiry.drug = inqRequest.drug;
            inquiry.content = inqRequest.textContent;
            inquiry.level = 1;
            inquiry.createTime = new Date().getTime();
            inquiry.updateTime = new Date().getTime();
            Inquiry persInq = inquiryRepo.save(inquiry);

            if (inqRequest.keyWords != null) {
                for (String tag : inqRequest.keyWords) {
                    Tag pTag = tagRepo.findByTag(tag);
                    if (pTag != null) {
                        persInq.tags.add(pTag);
                    } else {
                        persInq.tags.add(new Tag(tag));
                    }
                }
            }
            Department depart = departRepo.findByName(inqRequest.department);
            if (depart != null) {
                persInq.depart = depart;
            } else {
                persInq.depart = new Department(inqRequest.department);
            }
            persInq.user = userRepo.save(user);
            persInq.photoes = storeInquiryPhoto(photos, user.id, persInq.id);
            if (inqRequest.questionTo != null && !inqRequest.questionTo.isEmpty()){
                String[] to = inqRequest.questionTo.split("_");
                if (to.length > 1){
                    persInq.doctor = doctorRepo.findOne(Long.valueOf(to[1]));
                    persInq.assigned = persInq.doctor.id;
                    persInq.recepted = true;
                    persInq.category = 1;
                    InquiryPushChatResponse imResp = new InquiryPushChatResponse();
                    imResp.sender = new ChatParticipantInfo();
                    imResp.sender.id = user.id;
                    imResp.sender.role = 1;
                    imResp.sender.name = user.name;
                    imResp.sender.avatar = user.avatar;
                    imResp.receiver = new ChatParticipantInfo();
                    imResp.receiver.id = persInq.doctor.id;
                    imResp.receiver.role = 0;
                    imResp.receiver.name = persInq.doctor.name;
                    imResp.receiver.avatar = persInq.doctor.avatar;
                    imResp.category = 4;
                    imResp.inquiryID = persInq.id;
                    imResp.department = persInq.depart.name;
                    imResp.content = persInq.content;
                    imResp.drug = persInq.drug;
                    imResp.description = persInq.description;
                    imResp.point = persInq.point;
                    if (persInq.photoes != null && !persInq.photoes.isEmpty()) {
                        imResp.photoes = Arrays.asList(persInq.photoes.split("\\s"));
                    }
                    imResp.priority = 1;
                    IMScheduler.defaultSender.tell(new IMSenderMessage(persInq.doctor.jid, 0, persInq.doctor.clientID, persInq.doctor.deviceType,imResp), null);
                }
            }
            inquiryRepo.save(persInq);
            PushInquiryResponse response = new PushInquiryResponse(persInq.id);
            return response;
        } catch (Exception e) {
            Logger.info(e.getMessage());
            throw e;
        }
    }

    public void testMany2ManyRelation(List<String> tags) {
        /*switch (tags.size()){
            case 1:
                Inquiry x1 = new Inquiry();
                x1.description = "abcd";
                x1.level = 1;
                x1.recepted = false;
                x1.point = 20;
                inquiryRepo.save(x1);
                for(String tag: tags){
                    Tag pTag = tagRepo.findByTag(tag);
                    if (pTag != null){
                        x1.tags.add(pTag);
                    } else {
                        x1.tags.add(new Tag(tag));
                    }
                }
                inquiryRepo.save(x1);
                break;
            case 3:
                Inquiry x2 = new Inquiry();
                x2.description = "abcde";
                x2.level = 1;
                x2.recepted = false;
                x2.point = 20;
                inquiryRepo.save(x2);
                for(String tag: tags){
                    Tag pTag = tagRepo.findByTag(tag);
                    if (pTag != null){
                        x2.tags.add(pTag);
                    } else {
                        x2.tags.add(new Tag(tag));
                    }
                }
                inquiryRepo.save(x2);
                break;
        }*/
    }

    /* public List<Inquiry> queryInquiry(String tag){
         Tag pTag = tagRepo.findByTag(tag);
         return pTag.inquiries;
     }*/
    public GeneralResponse getInquiryDetail(Long inqID) {
        try {
            Inquiry inquiry = inquiryRepo.findOne(inqID);
            InquiryMessage message = new InquiryMessage();
            message.description = inquiry.description;
            message.content = inquiry.content;
            message.id = inqID;
            message.department = inquiry.depart.name;
            message.drug = inquiry.drug;
            if (!inquiry.photoes.isEmpty()) {
                String[] photos = inquiry.photoes.split("\\s");
                message.photoes = Arrays.asList(photos);
            }
            message.user = new PatientInfo(inquiry.user.id, inquiry.user.userName, inquiry.user.jid);

            message.point = inquiry.point;
            message.priority = inquiry.level;
            return new InquiryResponse(message);
        } catch (Exception e) {
            Logger.info(e.getMessage());
            return new GeneralResponse(4, "Get inquiry detail failed");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse receptInquiry(Doctor doctor, Long inqID) {
        try {
            Inquiry inquiry = inquiryRepo.findOneWithLock(inqID);
            if (inquiry != null && !inquiry.recepted){
                User user = inquiry.user;
                ChatParticipantInfo sender = new ChatParticipantInfo();
                sender.id = doctor.id;
                sender.role = 0;
                sender.name = doctor.name;
                sender.avatar = doctor.avatar;
                ChatParticipantInfo receiver = new ChatParticipantInfo();
                receiver.id = user.id;
                receiver.role = 1;
                receiver.name = user.name;
                ReceptChatMessageResponse response = new ReceptChatMessageResponse();
                response.sender = sender;
                response.receiver = receiver;
                response.category = 2;
                response.department = doctor.department.name;
                response.name = doctor.name;
                response.hospital = doctor.inHospital.name;
                response.title = doctor.title;
                response.inquiryID = inqID;
                IMScheduler.defaultSender.tell(new IMSenderMessage(user.jid,  1, user.clientID, user.deviceType, response), null);
                ChatRecord record = new ChatRecord();
                record.category = 2;
                record.senderID = doctor.id;
                record.receiverID = user.id;
                record.doctorID = doctor.id;
                record.inquiryID = inqID;
                record.createTime = System.currentTimeMillis();
                chatRepo.save(record);
                inquiry.doctor = doctorRepo.save(doctor);
                inquiry.recepted = true;
                inquiryRepo.save(inquiry);
                FriendItem friend = friendRepo.getFriendOfDoctor(doctor.id, inquiry.user.id);
                if (friend == null) {
                    friend = new FriendItem();
                    friend.createTime = System.currentTimeMillis();
                    FriendItem persFriend = friendRepo.save(friend);
                    persFriend.doctor = doctor;
                    persFriend.user = inquiry.user;
                    friendRepo.save(persFriend);
                }
                return new GeneralResponse();
            } else {
                return new GeneralResponse(22, "Sombody has recpted this inquiry");
            }
        } catch (Exception e) {
            Logger.info("recept inquiry failed", e);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse getInquiryList(Doctor doctor,int page, int limit) {
        try {
            List<Inquiry> inquiries = getAndUpdateInquiryAssignStatus(doctor,page, limit);
            List<InquiryMessage> messages = new ArrayList<>();
            for (Inquiry inquiry: inquiries){
                InquiryMessage message = new InquiryMessage();
            message.description = inquiry.description;
            message.id = inquiry.id;
            message.content = inquiry.content;
            message.department = inquiry.depart.name;
            message.drug = inquiry.drug;
            if (inquiry.photoes != null &&!inquiry.photoes.isEmpty()) {
                String[] photos = inquiry.photoes.split("\\s");
                message.photoes = Arrays.asList(photos);
            }
            message.user = new PatientInfo(inquiry.user.id, inquiry.user.userName,inquiry.user.jid);
            message.point = inquiry.point;
            message.priority = inquiry.level;
                message.createTime = inquiry.createTime;
                messages.add(message);
            }
            return new GetInquiryListResponse(messages);
        } catch (Exception e) {
            Logger.error("get inquriy failed", e);
            throw  e;
        }
    }

     List<Inquiry> getAndUpdateInquiryAssignStatus(Doctor doctor, int page, int limit) {
            int docLevel;
            if (doctor.level == 0){
                docLevel = 1;
            } else {
                docLevel = doctor.level;
            }
            Department department = departRepo.findByName(doctor.department.name);
            List<Inquiry> inquiries = inquiryRepo.getAllAvailibleInquiry(docLevel, department.id, doctor.id, page * limit, limit);
            ///List<Inquiry> inquiries = inquiryRepo.getLevelNUnrecptedInquiry(docLevel, doctor.department.name, doctor.id, new PageRequest(page, limit)).getContent();
            for (Inquiry inq : inquiries) {
                if (inq.assigned == 0) {
                    inq.updateTime = System.currentTimeMillis();
                }
                inq.assigned = doctor.id;
                inquiryRepo.save(inq);
            }
            /*totalList.addAll(inquiries);
            if (inquiries.size() < limit) {
                List<Inquiry> otherInquiries;
                if (inquiries.size() == 0) {
                    otherInquiries = inquiryRepo.getOtherLevelNUnrecptedInquiry(docLevel, doctor.department.name, doctor.id, new PageRequest(page, limit)).getContent();
                } else {
                    otherInquiries = inquiryRepo.getOtherLevelNUnrecptedInquiry(docLevel, doctor.department.name, doctor.id, new PageRequest(0, limit - inquiries.size())).getContent();
                }
                for (Inquiry inq: otherInquiries){
                    inq.assigned = doctor.id;
                    inq.updateTime = System.currentTimeMillis();
                    inquiryRepo.save(inq);
                }
                //inquiries.addAll(otherInquiries);
                totalList.addAll(otherInquiries);
            }*/
            return inquiries;
    }

    public String storeInquiryPhoto(List<Http.MultipartFormData.FilePart> photos, Long uid, Long inqID) {
        if (photos.size() == 0) {
            return "";
        }
        String storePath = Play.application().path() + "/store/";
        String inqPhotoPath = storePath + "user" + uid + "/" + inqID + "/photos/";
        File photoDir = new File(inqPhotoPath);
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        String photoUrls = "";
        String fileName0 = photos.get(0).getFilename();
        try {
            String newFlName = "";
            if (fileName0.lastIndexOf(".") < 0) {
                newFlName = MD5Generator.getMd5Value(fileName0 + System.currentTimeMillis());
                photoUrls = photoUrls + "/user/" + uid + "/inquiry/" + inqID + "/photo/" + newFlName;

            } else {
                newFlName = MD5Generator.getMd5Value(fileName0.substring(0, fileName0.lastIndexOf(".")) + System.currentTimeMillis()) + fileName0.substring(fileName0.lastIndexOf("."));
                photoUrls = photoUrls + "/user/" + uid + "/inquiry/" + inqID + "/photo/" + newFlName;
            }
            FileUtils.moveFile(photos.get(0).getFile(), new File(photoDir, newFlName));
        } catch (Exception e) {

        }
        for (int i = 1; i < photos.size(); i++) {
            try {
                Http.MultipartFormData.FilePart photo = photos.get(i);
                String newFlName = "";
                String fileName = photo.getFilename();
                if (fileName.lastIndexOf(".") < 0) {
                    newFlName = MD5Generator.getMd5Value(fileName + System.currentTimeMillis());
                    photoUrls = photoUrls + " /user/" + uid + "/inquiry/" + inqID + "/photo/" + newFlName;

                } else {
                    newFlName = MD5Generator.getMd5Value(fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()) + fileName.substring(fileName.lastIndexOf("."));
                    photoUrls = photoUrls + " /user/" + uid + "/inquiry/" + inqID + "/photo/" + newFlName;
                }
                FileUtils.moveFile(photo.getFile(), new File(photoDir, newFlName));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return photoUrls;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean testLock(){
        Inquiry inq = inquiryRepo.findOneWithLock(1l);
        if (inq != null){
            return true;
        } else {
            return false;
        }
    }
}
