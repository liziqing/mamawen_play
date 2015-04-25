package utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import play.Logger;

import java.net.URLEncoder;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
/**
 * Created by Ronald on 2015/4/21.
 */
public class SMSUtils {
    public static boolean sendValidateCode(String phone, String code) throws Exception{
        String url = "http://www.ztsms.cn:8800/sendXSms.do?";
        String param = "";
        param += "username=netshow&password=" + md5Hex("t50H7ok8".getBytes()) + "&";
        param += "mobile="+ phone + "&content=" + URLEncoder.encode("您的验证码是:" + code + " 【妈妈问】", "utf-8") + "&";
        param += "productid=676767&xh=";
        System.out.println(url + param);
        HttpGet get = new HttpGet(url + param);
        CloseableHttpClient client = HttpClients.createDefault();
        try{
            CloseableHttpResponse response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200){
                HttpEntity retEntity = response.getEntity();
                String result = EntityUtils.toString(retEntity);
                EntityUtils.consume(retEntity);
                get.releaseConnection();
                String[] spltResult = result.split(",");
                if (spltResult.length == 2){
                    if (spltResult[0].equals("1")){
                        return true;
                    } else {
                        Logger.info("code from sms server is " + spltResult[0]);
                        return false;
                    }
                }else {
                    Logger.info("value from sms server is " + result);
                    return false;
                }
            } else {
                System.out.println(status);
                return false;
            }
        } catch (Exception e){
            Logger.info(e.getMessage());
            return false;
        }finally {
            client.close();
        }
    }
}
