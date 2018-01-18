package client;

import handler.PortForwardClientHandler;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import java.net.InetSocketAddress;

/**
 * 端口转发器用于连接远程服务器的Client创建者
 * @author Worry
 */
public class PortForwarderClientFactory {

    public static IoSession createClientAndGetSession(String host,int port){
        IoConnector connector = new NioSocketConnector();
        connector.setHandler(new PortForwardClientHandler());
        ConnectFuture future = connector.connect(new InetSocketAddress(host,port));
        future.awaitUninterruptibly();
        if(!future.isConnected()){
            return null;
        }
        return future.getSession();
    }

}
