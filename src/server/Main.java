package server;

import java.io.IOException;

import engine.UnsupportedCommandException;

public class Main {
	public static void main(String[] args) {
		Server server = new Server(13131);
		try {
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (UnsupportedCommandException e) {
			e.printStackTrace();
		}
	}
}
