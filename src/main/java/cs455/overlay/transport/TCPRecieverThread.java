package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.EventType;
import cs455.overlay.wireformats.Register;

public class TCPRecieverThread implements Runnable{

	private Socket socket;
	private DataInputStream inputStream;
	private Node node;
	
	public TCPRecieverThread(Socket socket, Node node) throws IOException {
		this.node = node;
		this.socket = socket;
		this.inputStream = new DataInputStream(socket.getInputStream());
		
	}

	private boolean verifyRegistrationRequest(Register registrationRequest) {
		//TODO: Implement this method correctly!!!
		byte[] declaredInRequest =registrationRequest.getRegisteringIp();
		byte[] actualIp= socket.getInetAddress().getAddress();
		if (!java.util.Arrays.equals(declaredInRequest,actualIp)) {
			System.err.println("Host "+socket.getInetAddress().getHostAddress()+" did not send its real IP: "+registrationRequest);
			//return false;
			return true;
		}		
		return true;
	}
	
	@Override
	public void run() {
		int dataLength;
		
		while (socket!=null && !socket.isClosed()) {
			try {
				dataLength =  inputStream.readInt();
				if(dataLength ==0 )
					continue;
				byte[] data = new byte[dataLength];
				inputStream.readFully(data, 0, dataLength);		
				Event recievedEvent = EventFactory.getEvent(data);
				if(recievedEvent.getType()==EventType.REGISTER_REQUEST) {
					((Register)recievedEvent).IPverified = verifyRegistrationRequest((Register)recievedEvent);
				}
				node.onEvent(recievedEvent);
				
			}
			catch(SocketException | EOFException e) {
				// TODO Auto-generated catch block
				System.err.println("Connection to host "+socket.getInetAddress().getHostAddress()+":"+socket.getPort()+" did not terminate gracefully");
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
				e.printStackTrace();
			}			
		}
	}

}
