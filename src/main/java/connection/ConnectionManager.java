package connection;

import org.apache.mina.core.session.IoSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by root on 18-1-18.
 */
public class ConnectionManager {
    volatile private static ConnectionManager instance;
    private static ReentrantLock lock;
    private Map<IoSession,IoSession> cToSConnections;
    private Map<IoSession,IoSession> sToCConnections;
    static{
        lock = new ReentrantLock();
    }

    private ConnectionManager(){
        cToSConnections = new ConcurrentHashMap<IoSession, IoSession>();
        sToCConnections = new ConcurrentHashMap<IoSession, IoSession>();
    }

    public static ConnectionManager getInstance(){
        if(instance == null) {
            lock.tryLock();
            if (instance == null) {
                instance = new ConnectionManager();
            }
            lock.unlock();
        }
        return instance;
    }


    public Map<IoSession, IoSession> getcToSConnections() {
        return cToSConnections;
    }

    public Map<IoSession, IoSession> getsToCConnections() {
        return sToCConnections;
    }
}
