package handler;

import client.PortForwarderClientFactory;
import connection.ConnectionManager;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import java.util.Map;

public class PortForwardServerHandler extends IoHandlerAdapter {
    private String serverHost;
    private int port;

    public PortForwardServerHandler(){
        this("127.0.0.1",8080);
    }

    public PortForwardServerHandler(String serverHost, int port){
        this.serverHost = serverHost;
        this.port = port;
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        IoSession serverSession = PortForwarderClientFactory.createClientAndGetSession(serverHost,port);
        if(serverSession == null){
            session.closeNow();
            return;
        }
        ConnectionManager.getInstance().getcToSConnections().put(session,serverSession);
        ConnectionManager.getInstance().getsToCConnections().put(serverSession,session);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        Map<IoSession,IoSession> cToSConnections = ConnectionManager.getInstance().getcToSConnections();
        if(cToSConnections.containsKey(session)){
            IoSession serverSession = cToSConnections.get(session);
            if(serverSession == null){
                serverSession = PortForwarderClientFactory.createClientAndGetSession(serverHost,port);
                if(serverSession == null){
                    session.closeNow();
                    return;
                }
            }
            //TODO 打印消息体

            serverSession.write(message);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        Map<IoSession,IoSession> cToSConnection = ConnectionManager.getInstance().getcToSConnections();
        Map<IoSession,IoSession> sToCConnection = ConnectionManager.getInstance().getsToCConnections();

        if(cToSConnection.containsKey(session)) {
            IoSession serverSession = cToSConnection.get(session);
            if (serverSession != null) {
                if(sToCConnection.containsKey(serverSession)){
                    sToCConnection.remove(serverSession);
                    serverSession.closeOnFlush();
                }
            }
            cToSConnection.remove(session);
        }
    }
}
