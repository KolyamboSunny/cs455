package cs455.scaling.server;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Task {

    public long socketId;

    public SocketChannel replySocket = null;
    public Selector selector =null;
    
    private byte[] challenge;
    public byte[] getChallenge() {
    	return challenge;
    }
    
    public Task(SocketChannel replySocket,Selector selector, byte[] challenge) {
        this.replySocket = replySocket;
        this.challenge = challenge;
        this.selector = selector;
    }


}
