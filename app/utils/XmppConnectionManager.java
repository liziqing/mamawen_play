package utils;

import com.typesafe.config.ConfigMergeable;
import exception.IMServerException;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import play.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ronald on 2015/3/20.
 */
public class XmppConnectionManager {
    public static List<XMPPConnection> connections = null;

    public static void initializeConnection() {
        connections = new ArrayList<>();
        int count = ConfigureManager.senderCount;
        int start = ConfigureManager.senderJidStart;
        for (int i = 0; i < count; i++) {
            try {
                ConnectionConfiguration config = new ConnectionConfiguration(ConfigureManager.xmppAdress, ConfigureManager.xmppPort);
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                SASLAuthentication.supportSASLMechanism("PLAIN", 0);
                config.setReconnectionAllowed(true);
                XMPPConnection connection = new XMPPTCPConnection(config);
                connection.connect();
                connection.login("proxy" + (i + start), "123456");
                connections.add(connection);

            } catch (Exception e) {
                Logger.error("Init XMP connection fail", e);
            }
        }
    }

    public static void disconnect() {
        for (XMPPConnection connection : connections) {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                Logger.error("Close XMP connection fail", e);
            }
        }
    }

    public static boolean register(String jid, String password) throws Exception {
        try {
            ConnectionConfiguration config = new ConnectionConfiguration(ConfigureManager.xmppAdress, ConfigureManager.xmppPort, ConfigureManager.xmppServer);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setReconnectionAllowed(true);
            XMPPConnection connection = new XMPPTCPConnection(config);
            connection.connect();
            AccountManager manager = AccountManager.getInstance(connection);
            manager.createAccount(jid, password);
            connection.disconnect();
            return true;
        } catch (Exception e) {
            Logger.error("Register im server failed", e);
            throw new IMServerException("Register: " + e.getMessage());
        }
    }

}
