package actor;

import actor.message.ReminderMessage;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.camel.CamelMessage;
import akka.camel.javaapi.UntypedConsumerActor;
import models.Doctor;
import models.Reminder;
import play.Logger;
import play.libs.Akka;
import repo.RepositoryManager;
import utils.ConfigureManager;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ronald on 2015/4/2.
 */
public class RemindDispatcher extends UntypedConsumerActor {
    public static RepositoryManager repoManager;
    public int senderCount;
    public int curSender;
    public List<ActorRef> senders;
    public RemindDispatcher() {
        senderCount = ConfigureManager.reminderSenderCount;
        curSender = 0;
        senders = new ArrayList<>();
        for(int i = 0; i < senderCount; i++){
            ActorRef sender = Akka.system().actorOf(Props.create(ReminderSender.class));
            senders.add(sender);
        }
    }

    @Override
    public String getEndpointUri() {
        return "quartz://reminder?cron=0+*+*+*+*+?";
    }

    //public static ActorRef dispatcher = Akka.system().actorOf(Props.create(RemindDispatcher.class));
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof CamelMessage){
            Logger.info("Search reminder");
            SimpleDateFormat smf = new SimpleDateFormat("HH:mm:ss");
            Calendar cur = new GregorianCalendar();
            cur.setTime(new Date());
            cur.add(Calendar.MINUTE, 1);
            cur.set(Calendar.SECOND, 0);
            String remindTime = smf.format(cur.getTime());
            List<Reminder> reminders = repoManager.reminderRepo.getCurrentReminders(cur.getTime(), remindTime);
            for (Reminder reminder : reminders){
                ReminderMessage remMsg = new ReminderMessage();
                remMsg.title = reminder.title;
                remMsg.content = reminder.content;
                remMsg.doctor = reminder.doctor;
                remMsg.user = reminder.user;
                remMsg.patientName = reminder.patientName;
                remMsg.remindMe = reminder.remindMe;
                senders.get(curSender).tell(remMsg, null);
                curSender++;
                if (curSender >= senderCount){
                    curSender = 0;
                }
            }
        }
    }
}
