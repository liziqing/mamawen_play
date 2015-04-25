package actor;

import actor.message.IMSenderMessage;
import actor.message.ReminderMessage;
import akka.actor.UntypedActor;
import dto.ChatParticipantInfo;
import dto.ReminderMessageResponse;
import models.Reminder;

/**
 * Created by Ronald on 2015/4/2.
 */
public class ReminderSender extends UntypedActor{
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ReminderMessage){
            ReminderMessage msg = (ReminderMessage)message;
            ReminderMessageResponse response = new ReminderMessageResponse();
            response.content = msg.content;
            response.category = 5;
            response.title = msg.title;
            response.patientName = msg.patientName;
            response.remindMe = msg.remindMe;
            if (msg.remindMe){
                if (msg.doctor != null){
                    ChatParticipantInfo sender = new ChatParticipantInfo();
                    sender.id = msg.doctor.id;
                    sender.role = 0;
                    sender.name = msg.doctor.name;
                    sender.avatar = msg.doctor.avatar;
                    ChatParticipantInfo receiver = new ChatParticipantInfo();
                    receiver.id = msg.doctor.id;
                    receiver.role = 0;
                    receiver.name = msg.doctor.name;
                    receiver.avatar = msg.doctor.avatar;
                    response.sender = sender;
                    response.receiver = receiver;
                    IMScheduler.defaultSender.tell(new IMSenderMessage(msg.doctor.jid,  0, msg.doctor.clientID, msg.doctor.deviceType, response), null);
                }
            } else {
                if (msg.user != null){
                    ChatParticipantInfo sender = new ChatParticipantInfo();
                    sender.id = msg.doctor.id;
                    sender.role = 0;
                    sender.name = msg.doctor.name;
                    sender.avatar = msg.doctor.avatar;
                    ChatParticipantInfo receiver = new ChatParticipantInfo();
                    receiver.id = msg.user.id;
                    receiver.role = 1;
                    receiver.name = msg.user.name;
                    receiver.avatar = msg.user.avatar;
                    response.sender = sender;
                    response.receiver = receiver;
                    IMScheduler.defaultSender.tell(new IMSenderMessage(msg.user.jid,  1, msg.user.clientID, msg.user.deviceType, response), null);
                }
            }

        }
    }
}
