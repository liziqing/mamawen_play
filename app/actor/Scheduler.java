package actor;

import actor.message.AnswerRecept;
import actor.message.GetAnchor;
import actor.message.PushAnchor;
import actor.message.ReceptInquiry;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.Doctor;
import models.Inquiry;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import play.Logger;
import play.libs.Akka;
import repo.RepositoryManager;
import scala.concurrent.duration.Duration;

import java.util.*;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Ronald on 2015/3/5.
 */
public class Scheduler extends UntypedActor {
    public static RepositoryManager repoManager;
    Cancellable cancel = Akka.system().scheduler().schedule(
            Duration.create(3, SECONDS),
            Duration.create(30, SECONDS),
            new Runnable() {
                @Override
                public void run() {
                    if (repoManager != null && repoManager.inqRepo != null) {
                        Date cur = new Date();
                        Calendar twoHalfAgo = new GregorianCalendar();
                        twoHalfAgo.setTime(cur);
                        twoHalfAgo.add(Calendar.MINUTE, -2);
                        twoHalfAgo.add(Calendar.SECOND, -30);
                        twoHalfAgo.set(Calendar.MILLISECOND, 0);
                        Calendar fourHalfAgo = new GregorianCalendar();
                        fourHalfAgo.setTime(cur);
                        fourHalfAgo.add(Calendar.MINUTE, -4);
                        fourHalfAgo.add(Calendar.SECOND, -30);
                        fourHalfAgo.set(Calendar.MILLISECOND, 0);
                        repoManager.inqRepo.updateOldLeve1Inquiry(twoHalfAgo.getTime().getTime(), cur.getTime());
                        repoManager.inqRepo.updateOldLeve2Inquiry(fourHalfAgo.getTime().getTime(), cur.getTime());
                        Calendar sixHalfAgo = new GregorianCalendar();
                        sixHalfAgo.setTime(cur);
                        sixHalfAgo.add(Calendar.MINUTE, -6);
                        sixHalfAgo.add(Calendar.SECOND, -30);
                        sixHalfAgo.set(Calendar.MILLISECOND, 0);
                        repoManager.inqRepo.updateOldLeve3Inquiry(sixHalfAgo.getTime().getTime(), cur.getTime());
                    } else {
                        if (repoManager == null){
                            System.out.println("repo manager cant be created");
                        } else {
                            if (repoManager.inqRepo == null){
                                System.out.println("inquiry repo cant be created");
                            }
                        }

                    }

                }

            },
            Akka.system().dispatcher()
    );
    public static ActorRef defaultMonitor = Akka.system().actorOf(Props.create(Scheduler.class));

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ReceptInquiry){
            try {
                ReceptInquiry req = (ReceptInquiry) message;
                Inquiry inquiry = repoManager.inqRepo.findOne(req.inquiryID);
                if (!inquiry.recepted){
                    inquiry.doctor = repoManager.doctorRepo.findOne(req.doctorID);
                    inquiry.recepted = true;
                    repoManager.inqRepo.save(inquiry);
                    sender().tell(new AnswerRecept(0), self());
                } else {
                    sender().tell(new AnswerRecept(1), self());
                }
            }catch (Exception e){
                Logger.info(e.getMessage());
                sender().tell(new AnswerRecept(2), self());
            }
        }
    }


}
