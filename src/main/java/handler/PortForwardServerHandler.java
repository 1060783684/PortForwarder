package handler;

import client.PortForwarderClientFactory;
import connection.ConnectionManager;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import java.util.Map;

/**
 * 端口转发器服务器的IoHandler
 * @author Worry
 */
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
            //打印消息体
            if(message instanceof IoBuffer){
                IoBuffer buffer = (IoBuffer)message;
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes, 0, bytes.length);
                StringBuffer stringBuffer = new StringBuffer();
                for(int i = 0;i < bytes.length;i++){
                    String high = Integer.toHexString(bytes[i]);
                    if(high.length() < 2){
                        stringBuffer.append(0);
                    }
                    stringBuffer.append(high);

                    String low = Integer.toHexString(bytes[++i]);
                    if(low.length() < 2){
                        stringBuffer.append(0);
                    }
                    stringBuffer.append(low);
                    stringBuffer.append(" ");
                }
                System.out.println("[" + session.getRemoteAddress() + "to" + serverSession.getRemoteAddress() + "] : " + stringBuffer);
                buffer.position(0);
            }
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
