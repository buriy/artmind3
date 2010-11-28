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

	public void serve() throws IOException, InterruptedException, UnsupportedCommandException {
		this.server = new ServerSocket(this.port);
		Socket client = this.server.accept();
		accept_client(client);
		Thread.sleep(500);
        server.close();
        System.out.println("Work completed.");
	}

	private void accept_client(Socket client) throws IOException, UnsupportedCommandException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				client.getOutputStream()));

		while (command(in, out))
			;
	}

	public boolean command(BufferedReader in, BufferedWriter out)
			throws IOException, UnsupportedCommandException {
		String response = in.readLine();
		if (response == null)
			return false;
		String[] command = response.split(" ");
		try{
			String action = command[0];
			if ("TRAIN".equals(action)) {
				if(network == null){
					throw new UnsupportedCommandException("Network was not created. Please use CREATE command.");
				}
				State state = train(command, in);
				if (state == State.RESTART) {
					out.append("RESTART\n");
					out.flush();
				} else if (state == State.TESTING) {
					out.append("LEARNED\n");
					out.flush();
				}
			} else if ("TEST".equals(action)) {
				if(network == null){
					throw new UnsupportedCommandException("Network was not created. Please use CREATE command.");
				}
				String value = test(command, in);
				out.append("RESULT " + value + "\n");
				out.flush();
			} else if ("OPTIONS".equals(action)) {
				String[] fields = Options.fields();
				out.append("OPTIONS "+fields.length+"\n");
				for(String f: fields){
					out.append(f+"\n");
				}
				out.flush();
				System.out.println("Options sent!");
			} else if ("CREATE".equals(action)) {
				if(network != null){
					throw new UnsupportedCommandException("Network already exists!");
				}
				try {
					create(command, in);
				} catch (Exception e) {
					e.printStackTrace();
					throw new UnsupportedDataException("Command failed.");
				}
				System.out.println("Network created!");
			}
		}catch(UnsupportedDataException ude){
			System.err.println(ude);
		}
		return true;
	}

	private void create(String[] command, BufferedReader in) throws IOException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		int lines = Integer.parseInt(command[1]);
		Options options = new Options();
		for(int i=0; i<lines; i++){
			String opt = in.readLine();
			String[] parts = opt.split(" ", 1);
			options.setOption(parts[0], parts[1]);
		}
		this.network = new Network(options);
	}

	private State train(String[] command, BufferedReader in) throws IOException, UnsupportedDataException {
		if ("chars".equals(command[1])) {
			byte[] data = readData(command, in);
			String supervised = command[4];
			return network.train(data, supervised);
		}
		return State.LEARNING;
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