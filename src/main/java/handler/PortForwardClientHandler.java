package handler;

import connection.ConnectionManager;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import java.util.Map;

/**
 * 端口转发器连接远程服务的client的IoHandler
 * @author Worry
 */
public class PortForwardClientHandler extends IoHandlerAdapter {

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        //获取消息
        StringBuffer stringBuffer = new StringBuffer();
        if(message instanceof IoBuffer){
            IoBuffer buffer = (IoBuffer)message;
            byte[] bytes = new byte[buffer.limit()];
            buffer.get(bytes,0,bytes.length);
            for(int i = 0; i < bytes.length; i++){
                String high = Integer.toHexString(bytes[i]);
                if(high.length() < 2){
                    stringBuffer.append(0);
                }
                stringBuffer.append(high);

                String low = Integer.toHexString(bytes[++i]);
                if(high.length() < 2){
                    stringBuffer.append(0);
                }
                stringBuffer.append(low);
                stringBuffer.append(" ");
            }
            buffer.position(0);
        }
        Map<IoSession,IoSession> sToCConnections = ConnectionManager.getInstance().getsToCConnections();
        if(sToCConnections.containsKey(session)){
            IoSession clientSession = sToCConnections.get(session);
            //打印消息体
            if(clientSession != null){
                clientSession.write(message);
                System.out.println("[" + session.getRemoteAddress() + "to" + clientSession.getRemoteAddress() + "] : " + stringBuffer);
            }else {
                System.out.println("[" + session.getRemoteAddress() + "to Unknow Address" + "] : " + stringBuffer);
            }
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
