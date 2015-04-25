package utils;

import java.util.UUID;

/**
 * Created by Ronald on 2014/8/15.
 */
public class UUIDGenerator {
  public static String getUUID(){
    UUID raw = UUID.randomUUID();
    String str = raw.toString();
    String uuid = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
    return uuid;
  }
}
