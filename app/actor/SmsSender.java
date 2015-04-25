package actor;

import actor.message.SmsMessage;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Created by Ronald on 2015/4/10.
 */
public class SmsSender extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof SmsMessage) {
            SmsMessage msg = (SmsMessage)message;
            sendSms(msg.content, msg.phoneNumber);
        }
    }

    public boolean sendSms(String content, String phoneNumber) throws Exception {
        String url = "http://www.ztsms.cn:8800/sendManySms.do";
        HttpPost post = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", "netshow"));
            params.add(new BasicNameValuePair("password", md5Hex("t50H7ok8".getBytes())));
            params.add(new BasicNameValuePair("content", content + "【妈妈问】"));
            params.add(new BasicNameValuePair("mobile", phoneNumber));
            params.add(new BasicNameValuePair("productid", "676767"));
            params.add(new BasicNameValuePair("dstime", ""));
            params.add(new BasicNameValuePair("xh", ""));
            post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            CloseableHttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity retEntity = response.getEntity();
                String result = EntityUtils.toString(retEntity);
                EntityUtils.consume(retEntity);
                post.releaseConnection();
                String[] spltResult = result.split(",");
                if (spltResult.length == 2) {
                    if (spltResult[0].equals("1")) {
                        return true;
                    } else {
                        Logger.info("code from sms server is " + spltResult[0]);
                        return false;
                    }
                } else {
                    Logger.info("value from sms server is " + result);
                    return false;
                }
            } else {
                Logger.info("Response status of sms is" + status);
                return false;
            }
        } catch (Exception e) {
            Logger.error("send sms failed", e);
            return false;

        } finally {
            client.close();
        }
    }
}
