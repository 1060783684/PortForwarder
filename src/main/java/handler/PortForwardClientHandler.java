package handler;

import connection.ConnectionManager;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import java.util.Map;

public class PortForwardClientHandler extends IoHandlerAdapter {

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        //TODO 打印消息
        Map<IoSession,IoSession> sToCConnections = ConnectionManager.getInstance().getsToCConnections();
        if(sToCConnections.containsKey(session)){
            IoSession clientSession = sToCConnections.get(session);
            if(clientSession != null){
                clientSession.write(message);
            }
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
