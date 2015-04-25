package actor.message;

import dto.BaseChatMessageResponse;

/**
 * Created by Ronald on 2015/3/29.
 */
public class InternalSenderMsg {
    public String jid;
    public int role;
    public String clientID;
    public Integer deviceType;
    public BaseChatMessageResponse message;

    public InternalSenderMsg(String jid, int role, String clientID, Integer deviceType, BaseChatMessageResponse message) {
        this.jid = jid;
        this.role = role;
        this.clientID = clientID;
        this.message = message;
        this.deviceType = deviceType;
    }
}
