package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Ronald on 2015/3/11.
 */
public class MD5Generator {
  public static String getMd5Value(String seed) {
    try {
      MessageDigest md5Inst = MessageDigest.getInstance("MD5");

      md5Inst.update(seed.getBytes());

      byte[] bMD5 = md5Inst.digest();

      StringBuffer strbuf = new StringBuffer();
      int digt;
      for (int i = 0; i < bMD5.length; i++) {
        digt = bMD5[i];
        if (digt < 0) {
          digt += 256;
        }

        if (digt < 16) {
          strbuf.append("0");
        }
        strbuf.append(Integer.toHexString(digt));
      }
      return strbuf.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return "";
  }
}
