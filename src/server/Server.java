package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import engine.UnsupportedCommandException;

class Server extends Thread{
	private ServerSocket server;
	private int port;
	private ArrayList<Channel> clients;
	private boolean[] halt = new boolean[1];

	public Server(int port) {
		this.port = port;
		this.clients = new ArrayList<Channel>();
	}

	public void serve() throws IOException, InterruptedException, UnsupportedCommandException {
		this.server = new ServerSocket(this.port);
		this.start();
		while(!halt[0]){
			synchronized(clients){
				Iterator<Channel> iterator = clients.iterator();
				while(iterator.hasNext()){
					Channel next = iterator.next();
					if(!next.isAlive()){
				        int alive = clients.size()-1;
						System.out.println("Client disconnected. "+alive+" client(s) are alive.");
						iterator.remove();
					}
				}
			}
			Thread.sleep(50);
		}
        server.close();
	}

	public void run() {
		while(!halt[0]){
			try {
				Socket client = this.server.accept();
				try{
					Channel chan = new Channel(client, halt);
					chan.start();
					synchronized(clients){
						clients.add(chan);
					}
				}catch(OutOfMemoryError e){
					e.printStackTrace();
					continue;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}