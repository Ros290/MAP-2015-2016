
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class MultiServer extends Thread{
	//public static final int PORT = 8080;
	private int PORT;
	
	private Socket socket = null;
	
	MultiServer(int port){
		this.PORT = port;
		super.start();
	}
		
	public void run(){
		ServerSocket s = null;
		try{
			System.out.println("Server Avviato...");
			try {
				s = new ServerSocket(this.PORT);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				while(true){
					Socket socket = null;
					try {
						socket = s.accept();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try{
						new ServeOneClient(socket);
					} catch(IOException e){
						try {
							socket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
		}
		finally{
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
	}
	
    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 1)
            {   try {
                 port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                // nessun bisogno di gestire questa eccezione
                  }
            }
               new MultiServer(port).run();
    }

	}