package engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

class Server {
	private ServerSocket server;
	private int port;
	public Network network;

	public Server(int port) {
		this.port = port;
		try {
			this.server = new ServerSocket(this.port);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void serve() throws IOException {
		Socket client = this.server.accept();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				client.getOutputStream()));

		while (command(in, out))
			;
	}

	public boolean command(BufferedReader in, BufferedWriter out)
			throws IOException {
		String response = in.readLine();
		if (response == null)
			return false;
		String[] command = response.split(" ");
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
		return true;
	}

	private State train(String[] command, BufferedReader in) throws IOException {
		if ("chars".equals(command[1])) {
			int[] data = readData(command, in);
			String supervised = command[4];
			return network.train(data, supervised);
		}
		return State.TRAIN;
	}

	private String test(String[] command, BufferedReader in) throws IOException {
		if ("chars".equals(command[1])) {
			int[] data = readData(command, in);
			// slot = Integer.parseInt(command[4]);
			String value = network.run(data);
			return value.replace('\n',' ');
		}
		return null;
	}

	private int[] readData(String[] command, BufferedReader in)
			throws IOException {
		int width = Integer.parseInt(command[2]);
		int height = Integer.parseInt(command[3]);
		int[] matrix = new int[width * height];
		int pos = 0;
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				matrix[pos++] = in.read() & 0xFF;
			}
		}
		return matrix;
	}
}