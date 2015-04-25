package actor.message;

import dto.BaseChatMessageResponse;

/**
 * Created by Ronald on 2015/3/25.
 */
public class IMSenderMessage {
    public String jid;
    public int role;
    public String clientID;
    public Integer deviceType;
    public BaseChatMessageResponse message;

    public IMSenderMessage(String jid, int role, String clientID, Integer deviceType, BaseChatMessageResponse message) {
        this.jid = jid;
        this.role = role;
        this.message = message;
        this.deviceType = deviceType;
        this.clientID = clientID;
    }
}
