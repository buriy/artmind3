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
			Iterator<Channel> iterator = clients.iterator();
			while(iterator.hasNext()){
				Channel next = iterator.next();
				if(!next.isAlive()){
			        System.out.println("Client disconnected.");
					iterator.remove();
				}
			}
			Thread.sleep(10);
		}
        server.close();
	}

	public void run() {
		while(!halt[0]){
			try {
				Socket client = this.server.accept();
				Channel chan = new Channel(client, halt);
				chan.start();
				clients.add(chan);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}