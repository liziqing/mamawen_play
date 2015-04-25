package actor;

import actor.message.InternalSenderMsg;
import akka.actor.UntypedActor;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import dto.BaseChatMessageResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import play.Logger;
import play.libs.Json;
import utils.ConfigureManager;

/**
 * Created by Ronald on 2015/3/29.
 */
public class IMSender extends UntypedActor{
    XMPPConnection connection;

    public IMSender(XMPPConnection connection) {
        this.connection = connection;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof InternalSenderMsg){
            try {
                InternalSenderMsg msg = (InternalSenderMsg) message;
                Message payload = new Message(msg.jid + "@" + ConfigureManager.xmppServer, Message.Type.chat);
                String content = Json.stringify(Json.toJson(msg.message));
                payload.setBody(content);
                connection.sendPacket(payload);
                if (userIsOnline(msg.jid) == 1 && msg.clientID != null) {
                    pushSinglePush(msg.role, msg.deviceType, msg.clientID, msg.message);
                } else  if (msg.clientID == null && userIsOnline(msg.jid) == 1){
                    Logger.info("user: " + msg.jid + "have no clientID");
                }
            }catch (Exception e){
                Logger.error("Send one message failed", e);
            }
        }
    }
    void pushSinglePush(int role, int deviceType, String clientID, BaseChatMessageResponse response){
        try {
            String appID = "";
            String appKey = "";
            String master = "";
            String content = "";
            if (role == 0) {
                appKey = ConfigureManager.pushDocAppKey;
                appID = ConfigureManager.pushDocAppID;
                master = ConfigureManager.pushDocMaster;
            } else {
                appKey = ConfigureManager.pushUserAppKey;
                appID = ConfigureManager.pushUserAppID;
                master = ConfigureManager.pushUserMaster;
            }
            switch (response.category){
                case 1:
                    content = "新消息";
                    break;
                case 2:
                    content = "医生" + response.sender.name + "接诊";
                    break;
                case 3:
                    content = "医生" + response.sender.name + "发来问诊报告";
                    break;
                case 4:
                    content = "用户" + response.sender.name + "发来问诊";
                    break;
                case 5:
                    content = "医生" + response.sender.name + "发来提醒";
                    break;
                case 6:
                    content = "用户" + response.sender.name + "发来评分";
                    break;
                default:
                    content = "新消息";
                    break;

            }
            IGtPush push = new IGtPush(appKey, master);
            push.connect();
            TransmissionTemplate template = new TransmissionTemplate();
            template.setAppId(appID);
            template.setAppkey(appKey);
            template.setTransmissionType(1);
            template.setTransmissionContent(content);
            if (deviceType == 1){
                template.setPushInfo("", 1, content, "default", content, "", "", "");
            }
            SingleMessage message = new SingleMessage();
            message.setOffline(true);
            message.setOfflineExpireTime(24 * 3600 * 1000);
            message.setData(template);
            Target target = new Target();
            target.setAppId(appID);
            target.setClientId(clientID);
            IPushResult ret = push.pushMessageToSingle(message, target);
        } catch (Exception e){

        }
    }
    /*void pushApn(int role, String deviceToken, BaseChatMessageResponse response){
        ApnsService service = getDoctorApnsService(role);
        String payload;
        if (service != null) {
            switch (response.category) {
                case 1:
                    NormalChatMessageResponse nChatResp = (NormalChatMessageResponse) response;
            }
            IGtPush push = new IGtPush(appKey, master);
            push.connect();
            TransmissionTemplate template = new TransmissionTemplate();
            template.setAppId(appID);
            template.setAppkey(appKey);
            template.setTransmissionType(1);
            template.setTransmissionContent(content);
            if (deviceType == 1){
                template.setPushInfo("", 1, "有用户问诊", "default", content, "", "", "");
            }
            SingleMessage message = new SingleMessage();
            message.setOffline(true);
            message.setOfflineExpireTime(24 * 3600 * 1000);
            message.setData(template);
            Target target = new Target();
            target.setAppId(appID);
            target.setClientId(clientID);
            IPushResult ret = push.pushMessageToSingle(message, target);
        } catch (Exception e){

        }
    }
    /*void pushApn(int role, String deviceToken, BaseChatMessageResponse response){
        ApnsService service = getDoctorApnsService(role);
        String payload;
        if (service != null) {
            switch (response.category) {
                case 1:
                    NormalChatMessageResponse nChatResp = (NormalChatMessageResponse) response;
                    if (nChatResp.subCategory == 1) {
                        payload = APNS.newPayload().forNewsstand().alertBody(nChatResp.content).build();
                        service.push(deviceToken, payload);
                    } else if (nChatResp.subCategory == 2) {
                        payload = APNS.newPayload().forNewsstand().alertBody("��ͼƬ��").build();
                        service.push(deviceToken, payload);
                    } else {
                        payload = APNS.newPayload().forNewsstand().alertBody("��������").build();
                        service.push(deviceToken, payload);
                    }
                    break;
                case 2:
                    payload = APNS.newPayload().forNewsstand().alertBody("ҽ��" + response.sender.name + "����").build();
                    service.push(deviceToken, payload);
                    break;
                case 3:
                    payload = APNS.newPayload().forNewsstand().alertBody("ҽ��" + response.sender.name + "����Ԥ�ﱨ��").build();
                    service.push(deviceToken, payload);
                    break;
            }
        }
    }
    private ApnsService getDoctorApnsService(int role){
        try{
            String certPath;
            String certPwd;
            if (role == 1) {
                certPath = Play.application().path() + "/certification/" + ConfigureManager.apnDoctorCertFile;
                certPwd = ConfigureManager.apnDoctorCertPwd;
            } else {
                certPath = Play.application().path() + "/certification/" + ConfigureManager.apnUserCertFile;
                certPwd = ConfigureManager.apnUserCertPwd;
            }
            if (ConfigureManager.apnMode.equals("debug")){
                return APNS.newService().withCert(certPath, certPwd).withSandboxDestination().build();

            } else {
                return APNS.newService().withCert(certPath, certPwd).withProductionDestination().build();
            }
        }catch(Exception e){
            Logger.error("Get apn service failed", e);
            return  null;
        }
    }*/
    int userIsOnline(String jid) throws Exception{
        String url = "http://"+ ConfigureManager.xmppAdress + ":9090/plugins/presence/status?jid=" + jid + "@" + ConfigureManager.xmppServer + "&type=xml";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        try{
            CloseableHttpResponse response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200){
                String msg = EntityUtils.toString(response.getEntity());
                if(msg.contains("type=\"unavailable\"")){
                    return 1;
                } else if (msg.contains("to")){
                    return 2;
                } else {
                    return 3;
                }
            } else {
                return 3;
            }
        } catch (Exception e){
            Logger.error("Get im user status failed", e);
            return 3;
        }finally {
            get.releaseConnection();
            client.close();
        }
    }
}
