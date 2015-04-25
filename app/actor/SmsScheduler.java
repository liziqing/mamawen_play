package actor;

import actor.message.SmsMessage;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.libs.Akka;
import utils.ConfigureManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ronald on 2015/4/10.
 */
public class SmsScheduler extends UntypedActor{
    public int senderCount;
    public int curSender;
    public List<ActorRef> senders;
    public SmsScheduler() {
        senderCount = ConfigureManager.smsSenderCount;
        curSender = 0;
        senders = new ArrayList<>();
        for (int i = 0; i < senderCount; i++){
            senders.add(Akka.system().actorOf(Props.create(SmsSender.class)));
        }
    }
    public static ActorRef scheduler = Akka.system().actorOf(Props.create(SmsScheduler.class));
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof SmsMessage){
            SmsMessage msg = (SmsMessage)message;
            senders.get(curSender).tell(msg, null);
            curSender++;
            if (curSender >= senderCount){
                curSender = 0;
            }
        }
    }
}
