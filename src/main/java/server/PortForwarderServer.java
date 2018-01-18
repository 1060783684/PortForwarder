package server;

import handler.PortForwardServerHandler;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
/**
 * 端口转发器服务器
 * @author Worry
 */
/*
   _____________              _______________________________________________              __________________
  |             |            |                       |                       |            |                  |
  | user client | ---------> |                 端口转发服务器                  | ---------> |                  |
  |_____________|            |                       |                       |            |  Remote Server   |
                             |   PortForwardServer   |   PortForwardClient   |            |                  |
                             |                       |                       |            |                  |
                             | serverHost:serverPort | clientHost:clientPort |            |                  |
                             | ______________________|_______________________|            |__________________|
 */
public class PortForwarderServer {
    private String clientHost;
    private int clientPort;
    private String serverHost;
    private int serverPort;

    public PortForwarderServer(){
        this( "127.0.0.1", 8080, "127.0.0.1", 29511);
    }

    public PortForwarderServer(String clientHost, int clientPort){
        this(clientHost, clientPort, "127.0.0.1", 29511);
    }

    /**
     *
     * @param clientHost 端口转发器要连接的服务端的host
     * @param clientPort 端口转发器要连接的服务端的port
     * @param serverHost 端口转发器服务器的host
     * @param serverPort 端口转发器服务器的port
     */
    public PortForwarderServer(String clientHost, int clientPort, String serverHost, int serverPort){
        this.clientHost = clientHost;
        this.clientPort = clientPort;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * 开启服务器线程的方法
     * @throws IOException
     */
    public void run() throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setHandler(new PortForwardServerHandler());
        acceptor.bind(new InetSocketAddress(clientHost,clientPort));
    }
}
