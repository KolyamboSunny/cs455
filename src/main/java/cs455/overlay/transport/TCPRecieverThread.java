package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

public class TCPRecieverThread implements Runnable{

	private Socket socket;
	private DataInputStream inputStream;
	private Node node;
	
	public TCPRecieverThread(Socket socket, Node node) throws IOException {
		this.node = node;
		this.socket = socket;
		this.inputStream = new DataInputStream(socket.getInputStream());
		
	}

	@Override
	public void run() {
		int dataLength;
		
		while (socket!=null && !socket.isClosed()) {
			try {
				dataLength =  inputStream.readInt();
				byte[] data = new byte[dataLength];
				inputStream.readFully(data, 0, dataLength);		
				Event recievedEvent = EventFactory.getEvent(data);
				node.onEvent(recievedEvent);
			}
			catch(SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			catch(IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}

}
