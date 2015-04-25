package utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
/**
 * Created by Ronald on 2015/3/19.
 */
public class ConfigureManager {
    public static Config config = ConfigFactory.load();
    public static String xmppAdress = config.getString("xmpp.address");
    public static int xmppPort = config.getInt("xmpp.port");
    public static String xmppServer = config.getString("xmpp.server");
    public static int senderCount = config.getInt("im.sender.count");
    public static int senderJidStart = config.getInt("im.sender.jidstart");
    public static String aesKey = config.getString("aes.key");
    public static int reminderSenderCount = config.getInt("reminder.sender.count");
    public static int smsSenderCount = config.getInt("sms.sender.count");
    public static String pushDocAppID = config.getString("push.doctor.appid");
    public static String pushDocAppKey = config.getString("push.doctor.appkey");
    public static String pushDocMaster = config.getString("push.doctor.master");
    public static String pushUserAppID = config.getString("push.user.appid");
    public static String pushUserAppKey = config.getString("push.user.appkey");
    public static String pushUserMaster = config.getString("push.user.master");
}
