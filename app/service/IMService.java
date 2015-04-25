package service;

import actor.IMScheduler;
import actor.message.IMSenderMessage;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import dto.*;
import exception.IMServerException;
import models.*;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import play.Play;
import play.mvc.Http;
import utils.ConfigureManager;
import utils.MD5Generator;
import utils.UUIDGenerator;

import java.io.File;

/**
 * Created by Ronald on 2015/3/24.
 */
@Service
public class IMService extends AuthenticationService {
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse doctorSendNormalChat(Doctor doctor, NormalChatMessage msg, Http.MultipartFormData body) throws Exception {
        User user = userRepo.findOne(msg.receiver.id);
        String chatFileStore = Play.application().path() + "/store/chats";
        if (user != null) {
            try {
                ChatParticipantInfo sender = new ChatParticipantInfo();
                sender.id = doctor.id;
                sender.role = 0;
                sender.name = doctor.name;
                sender.avatar = doctor.avatar;
                ChatParticipantInfo receiver = new ChatParticipantInfo();
                receiver.id = user.id;
                receiver.role = 1;
                receiver.name = user.name;
                String fileDirPath = "";
                String id = MD5Generator.getMd5Value(UUIDGenerator.getUUID()+"doctor"+doctor.id + "user" + user.id);
                fileDirPath = chatFileStore + "/" + id;
                File filesDir = null;
                switch (msg.subCategory) {
                    case 1:
                        if (msg.content == null || msg.content.isEmpty()) {
                            return new GeneralResponse(53, "Can't send empty content");
                        } else {

                            NormalChatMessageResponse response = new NormalChatMessageResponse();
                            response.category = 1;
                            response.sender = sender;
                            response.receiver = receiver;
                            response.subCategory = msg.subCategory;
                            response.content = msg.content;
                            response.inquiryID = msg.inquiryID;
                            IMScheduler.defaultSender.tell(new IMSenderMessage(user.jid, 1, user.clientID, user.deviceType, response), null);
                            ChatRecord record = new ChatRecord();
                            record.category = msg.category;
                            record.content = msg.content;
                            record.subCategory = msg.subCategory;
                            record.senderID = doctor.id;
                            record.receiverID = user.id;
                            record.inquiryID = msg.inquiryID;
                            record.createTime = System.currentTimeMillis();
                            chatRepo.save(record);
                        }
                        break;
                    case 2:
                        filesDir = new File(fileDirPath);
                        if (!filesDir.exists()) {
                            filesDir.mkdirs();
                        }
                        Http.MultipartFormData.FilePart imgFlPart = body.getFile("file");
                        if (imgFlPart != null) {
                            NormalChatMessageResponse response = new NormalChatMessageResponse();
                            response.sender = sender;
                            response.receiver = receiver;
                            response.category = 1;
                            response.subCategory = msg.subCategory;
                            response.inquiryID = msg.inquiryID;
                            String fileName = imgFlPart.getFilename();
                            String newFlName;
                            if (!fileName.contains(".")) {
                                newFlName = MD5Generator.getMd5Value(fileName + System.currentTimeMillis());
                            } else {
                                newFlName = MD5Generator.getMd5Value(fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()) + fileName.substring(fileName.lastIndexOf("."));
                            }
                            FileUtils.moveFile(imgFlPart.getFile(), new File(filesDir, newFlName));
                            response.content = "/chat/" + id + "/image/" + newFlName;
                            IMScheduler.defaultSender.tell(new IMSenderMessage(user.jid, 1, user.clientID, user.deviceType, response), null);
                            ChatRecord record = new ChatRecord();
                            record.category = msg.category;
                            record.content =response.content;
                            record.subCategory = msg.subCategory;
                            record.senderID = doctor.id;
                            record.receiverID = user.id;
                            record.inquiryID = msg.inquiryID;
                            record.createTime = System.currentTimeMillis();
                            chatRepo.save(record);
                        } else {
                            return new GeneralResponse(56, "Not has file");
                        }
                        break;
                    case 3:
                        filesDir = new File(fileDirPath);
                        if (!filesDir.exists()) {
                            filesDir.mkdirs();
                        }
                        Http.MultipartFormData.FilePart sndFlPart = body.getFile("file");
                        if (sndFlPart != null) {
                            NormalChatMessageResponse response = new NormalChatMessageResponse();
                            response.category = 1;
                            response.sender = sender;
                            response.receiver = receiver;
                            response.subCategory = msg.subCategory;
                            response.inquiryID = msg.inquiryID;
                            String fileName = sndFlPart.getFilename();
                            String newFlName;
                            if (!fileName.contains(".")) {
                                newFlName = MD5Generator.getMd5Value(fileName + System.currentTimeMillis());
                            } else {
                                newFlName = MD5Generator.getMd5Value(fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()) + fileName.substring(fileName.lastIndexOf("."));
                            }
                            FileUtils.moveFile(sndFlPart.getFile(), new File(filesDir, newFlName));
                            response.content = "/chat/" + id + "/audio/" + newFlName;
                            IMScheduler.defaultSender.tell(new IMSenderMessage(user.jid, 1,user.clientID,user.deviceType,  response), null);
                            ChatRecord record = new ChatRecord();
                            record.category = msg.category;
                            record.content = response.content;
                            record.subCategory = msg.subCategory;
                            record.senderID = doctor.id;
                            record.receiverID = user.id;
                            record.inquiryID = msg.inquiryID;
                            record.createTime = System.currentTimeMillis();
                            chatRepo.save(record);
                        } else {
                            return new GeneralResponse(56, "Not has file");
                        }
                        break;
                    default:
                        return new GeneralResponse(54, "Not indicate subcategory");
                }
                return new GeneralResponse();
            } catch (Exception e) {
                Logger.error("Send message failed", e);
                throw new IMServerException(e.getMessage());
            }
        } else {
            return new GeneralResponse(11, "Please push necessary data");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse doctorSendReceptMessage(Doctor doctor, BaseChatMessage msg) throws Exception {
        try {
            User user = userRepo.findOne(msg.receiver.id);
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
            response.inquiryID = msg.inquiryID;
            IMScheduler.defaultSender.tell(new IMSenderMessage(user.jid,  1, user.clientID, user.deviceType, response), null);
            ChatRecord record = new ChatRecord();
            record.category = msg.category;
            record.senderID = doctor.id;
            record.receiverID = user.id;
            record.doctorID = doctor.id;
            record.inquiryID = msg.inquiryID;
            record.createTime = System.currentTimeMillis();
            chatRepo.save(record);
            return new GeneralResponse();
        } catch (Exception e) {
            Logger.error("Send message failed", e);
            throw new IMServerException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse doctorSendReportMessage(Doctor doctor, ReportChatMessage msg) throws Exception {
        try {
            User user = userRepo.findOne(msg.receiver.id);
            ChatParticipantInfo sender = new ChatParticipantInfo();
            sender.id = doctor.id;
            sender.role = 0;
            sender.name = doctor.name;
            sender.avatar = doctor.avatar;
            ChatParticipantInfo receiver = new ChatParticipantInfo();
            receiver.id = user.id;
            receiver.role = 1;
            receiver.name = user.name;
            ReportChatMessageResponse response = new ReportChatMessageResponse();
            response.category = 3;
            response.description = msg.description;
            response.suggestion = msg.suggestion;
            response.sender = sender;
            response.receiver =receiver;
            response.inquiryID = msg.inquiryID;
            IMScheduler.defaultSender.tell(new IMSenderMessage(user.jid, 1, user.clientID, user.deviceType, response), null);
            InquiryReport report = new InquiryReport();
            report.description = msg.description;
            report.suggestion = msg.suggestion;
            report.createTime = System.currentTimeMillis();
            Inquiry inquiry = inquiryRepo.findOne(msg.inquiryID);
            inquiry.report = report;
            inquiry.finished = true;
            inquiryRepo.save(inquiry);
            ChatRecord record = new ChatRecord();
            record.category = msg.category;
            record.senderID = doctor.id;
            record.receiverID = user.id;
            record.reportID = report.id;
            record.inquiryID = msg.inquiryID;
            record.createTime = System.currentTimeMillis();
            chatRepo.save(record);
            return new GeneralResponse();
        } catch (Exception e) {
            Logger.error("Send message failed", e);
            throw new IMServerException(e.getMessage());
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse userSendNormalMessage(User user, NormalChatMessage msg, Http.MultipartFormData body) throws Exception{
        Doctor doctor = doctorRepo.findOne(msg.receiver.id);
        String chatFileStore = Play.application().path() + "/store/chats";
        if (doctor != null) {
            try {
                ChatParticipantInfo sender = new ChatParticipantInfo();
                sender.id = user.id;
                sender.role = 1;
                sender.name = user.name;
                sender.avatar = user.avatar;
                ChatParticipantInfo receiver = new ChatParticipantInfo();
                receiver.id = doctor.id;
                receiver.role = 0;
                receiver.name = doctor.name;
                String fileDirPath = "";
                String id = MD5Generator.getMd5Value(UUIDGenerator.getUUID()+"doctor"+doctor.id + "user" + user.id);
                fileDirPath = chatFileStore + "/" + id;
                File filesDir = null;
                switch (msg.subCategory) {
                    case 1:
                        if (msg.content == null || msg.content.isEmpty()) {
                            return new GeneralResponse(53, "Can't send empty content");
                        } else {

                            NormalChatMessageResponse response = new NormalChatMessageResponse();
                            response.category = 1;
                            response.sender = sender;
                            response.receiver = receiver;
                            response.subCategory = msg.subCategory;
                            response.content = msg.content;
                            response.inquiryID  = msg.inquiryID;
                            IMScheduler.defaultSender.tell(new IMSenderMessage(doctor.jid, 0, doctor.clientID, doctor.deviceType,response), null);
                            ChatRecord record = new ChatRecord();
                            record.category = msg.category;
                            record.content = msg.content;
                            record.subCategory = msg.subCategory;
                            record.senderID = user.id;
                            record.receiverID = doctor.id;
                            record.inquiryID = msg.inquiryID;
                            record.createTime = System.currentTimeMillis();
                            chatRepo.save(record);
                        }
                        break;
                    case 2:
                        filesDir = new File(fileDirPath);
                        if (!filesDir.exists()) {
                            filesDir.mkdirs();
                        }
                        Http.MultipartFormData.FilePart imgFlPart = body.getFile("file");
                        if (imgFlPart != null) {
                            NormalChatMessageResponse response = new NormalChatMessageResponse();
                            response.category = 1;
                            response.sender = sender;
                            response.receiver = receiver;
                            response.subCategory = msg.subCategory;
                            response.inquiryID = msg.inquiryID;
                            String fileName = imgFlPart.getFilename();
                            String newFlName;
                            if (!fileName.contains(".")) {
                                newFlName = MD5Generator.getMd5Value(fileName + System.currentTimeMillis());
                            } else {
                                newFlName = MD5Generator.getMd5Value(fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()) + fileName.substring(fileName.lastIndexOf("."));
                            }
                            msg.content = "/chat/" + id + "/image/" + newFlName;
                            FileUtils.moveFile(imgFlPart.getFile(), new File(filesDir, newFlName));
                            response.content = msg.content;
                            IMScheduler.defaultSender.tell(new IMSenderMessage(doctor.jid, 0, doctor.clientID, doctor.deviceType, response), null);
                            ChatRecord record = new ChatRecord();
                            record.category = msg.category;
                            record.content = msg.content;
                            record.subCategory = msg.subCategory;
                            record.senderID = user.id;
                            record.receiverID = doctor.id;
                            record.inquiryID = msg.inquiryID;
                            record.createTime = System.currentTimeMillis();
                            chatRepo.save(record);
                        } else {
                            return new GeneralResponse(56, "Not has file");
                        }
                        break;
                    case 3:
                        filesDir = new File(fileDirPath);
                        if (!filesDir.exists()) {
                            filesDir.mkdirs();
                        }
                        Http.MultipartFormData.FilePart sndFlPart = body.getFile("file");
                        if (sndFlPart != null) {
                            NormalChatMessageResponse response = new NormalChatMessageResponse();
                            response.category = 1;
                            response.sender = sender;
                            response.receiver = receiver;
                            response.subCategory = msg.subCategory;
                            response.inquiryID = msg.inquiryID;
                            String fileName = sndFlPart.getFilename();
                            String newFlName;
                            if (!fileName.contains(".")) {
                                newFlName = MD5Generator.getMd5Value(fileName + System.currentTimeMillis());
                            } else {
                                newFlName = MD5Generator.getMd5Value(fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()) + fileName.substring(fileName.lastIndexOf("."));
                            }
                            msg.content = "/chat/" + id + "/audio/" + newFlName;
                            FileUtils.moveFile(sndFlPart.getFile(), new File(filesDir, newFlName));
                            response.content = msg.content;
                            IMScheduler.defaultSender.tell(new IMSenderMessage(doctor.jid, 0, doctor.clientID, doctor.deviceType, response), null);
                            ChatRecord record = new ChatRecord();
                            record.category = msg.category;
                            record.content = msg.content;
                            record.subCategory = msg.subCategory;
                            record.senderID = user.id;
                            record.receiverID = doctor.id;
                            record.inquiryID = msg.inquiryID;
                            record.createTime = System.currentTimeMillis();
                            chatRepo.save(record);
                        } else {
                            return new GeneralResponse(56, "Not has file");
                        }
                        break;
                    default:
                        return new GeneralResponse(54, "Not indicate subcategory");
                }
                return new GeneralResponse();
            } catch (Exception e) {
                Logger.error("Send message failed", e);
                throw new IMServerException(e.getMessage());
            }
        } else {
            return new GeneralResponse(11, "Please push necessary data");
        }
    }

}
