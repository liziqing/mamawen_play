package actor;

import actor.message.IMSenderMessage;
import actor.message.InternalSenderMsg;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.libs.Akka;
import utils.ConfigureManager;
import utils.XmppConnectionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ronald on 2015/3/25.
 */
public class IMScheduler extends UntypedActor{
    List<ActorRef> senders = new ArrayList<>();
    int nextSender;
    int totalCount;

    public IMScheduler() {
        totalCount = ConfigureManager.senderCount;
        nextSender = 0;
        for (int i = 0; i < totalCount; i++){
            ActorRef sender = Akka.system().actorOf(Props.create(IMSender.class, XmppConnectionManager.connections.get(i)));
            senders.add(sender);
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof IMSenderMessage){
            IMSenderMessage msg = (IMSenderMessage)message;
            senders.get(nextSender).tell(new InternalSenderMsg(msg.jid, msg.role, msg.clientID, msg.deviceType, msg.message), self());
            nextSender++;
            if (nextSender >= totalCount) {
                nextSender = 0;
            }
        }
    }
    public static ActorRef defaultSender = Akka.system().actorOf(Props.create(IMScheduler.class));
}
