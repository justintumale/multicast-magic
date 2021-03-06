/*File: ParticipantListener.java
 * Authors: Montana Wong, Justin Tumale, Matthew Haneburger
 * Listens to commands coming from Coordinator. Runs on an infinite loop.
 * */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//description given above
public class ParticipantListener extends Thread{
	
	private int listenPort;
	private ServerSocket sock;
	private Socket coordinatorSocket;
	private ExecutorService threadPool;
	private String logFileName;
	private Queue <String> messageQueue;
	//constructor for the participant listener
	public ParticipantListener(int listenPort, String logFileName, ExecutorService threadPool){
		this.listenPort = listenPort;
		this.threadPool = threadPool;
		this.logFileName = logFileName;
	}
	
	@Override
	public void run(){
		try {
		this.sock = new ServerSocket(this.listenPort);
		this.messageQueue = new LinkedList<String>();
		//System.out.println("Listener running on port: " + this.listenPort);
			while(true){
					this.coordinatorSocket = this.sock.accept();
					BufferedReader br = new BufferedReader(new InputStreamReader(coordinatorSocket.getInputStream()));
					String message;
					while ((message = br.readLine() )!= null){
						this.messageQueue.add(message);
					}
					//System.out.println("message to log " +  this.messageQueue.peek());
					//Receive message from coordinator here
					//create logger thread

					Logger logger = new Logger(this.logFileName, this.messageQueue);	
					this.threadPool.execute(logger);
	
					//may reconsider this design chice for queue message send
					if (this.coordinatorSocket != null){
						br.close();
						this.coordinatorSocket.close();
					}	
			}
		}
		catch (IOException e) {
			//e.printStackTrace();
			//System.out.println("Listener disconnected by Participant");
		}
	}
	//closes connection to participant listener
	//@throws IOException
	public void shutdown() throws IOException{
		//System.out.println("Shutting down listener.");
		this.sock.close();
		
	}
	
	public void setListenPort(int port){
		this.listenPort = port;
	}

}
