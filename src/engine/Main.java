package engine;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		Server server = new Server(13131);
		try {
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
