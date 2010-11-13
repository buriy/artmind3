package engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import util.Base64;

class Server {
	private ServerSocket server;
	private int port;
	public Network network;

	public Server(int port) {
		this.port = port;
	}

	public void serve() throws IOException, InterruptedException {
		this.server = new ServerSocket(this.port);
		Socket client = this.server.accept();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				client.getOutputStream()));

		while (command(in, out))
			;
		Thread.sleep(500);
        server.close();
        System.out.println("Exiting");
	}

	public boolean command(BufferedReader in, BufferedWriter out)
			throws IOException {
		String response = in.readLine();
		if (response == null)
			return false;
		String[] command = response.split(" ");
		try{
			if ("TRAIN".equals(command[0])) {
				State state = train(command, in);
				if (state == State.RESTART) {
					out.append("RESTART\n");
					out.flush();
				} else if (state == State.LEARNED) {
					out.append("LEARNED\n");
					out.flush();
				}
			} else if ("TEST".equals(command[0])) {
				String value = test(command, in);
				out.append("RESULT " + value + "\n");
				out.flush();
			} else if ("OPTIONS".equals(command[0])) {
				Options options = new Options();
				this.network = new Network(options);
				System.out.println("Network created!");
			}
		}catch(UnsupportedDataException ude){
			System.err.println(ude);
		}
		return true;
	}

	private State train(String[] command, BufferedReader in) throws IOException, UnsupportedDataException {
		if ("chars".equals(command[1])) {
			byte[] data = readData(command, in);
			String supervised = command[4];
			return network.train(data, supervised);
		}
		return State.TRAIN;
	}

	private String test(String[] command, BufferedReader in) throws IOException, UnsupportedDataException {
		if ("chars".equals(command[1])) {
			byte[] data = readData(command, in);
			// slot = Integer.parseInt(command[4]);
			String value = network.run(data);
			return value.replace('\n',' ');
		}
		return null;
	}

	private byte[] readData(String[] command, BufferedReader in)
			throws IOException, UnsupportedDataException {
		int width = Integer.parseInt(command[2]);
		int height = Integer.parseInt(command[3]);
		byte[] matrix = Base64.decode(in.readLine().replace("!", "\n"));
		if(width * height != matrix.length)
			throw new UnsupportedDataException("Expected matrix of "+width+"*"+height+", got "+matrix.length+" instead.");
		return matrix;
	}
}