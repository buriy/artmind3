package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import util.Base64;
import engine.NetState;
import engine.Network;
import engine.Options;
import engine.UnsupportedCommandException;
import engine.UnsupportedDataException;

public class Channel extends Thread {
	private final Socket client;

	public Network network;

	private final boolean[] halt;

	public Channel(Socket client, boolean[] halt) {
		this.client = client;
		this.halt = halt;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(client.getInputStream());
			OutputStreamWriter osr = new OutputStreamWriter(client.getOutputStream());
			BufferedReader in = new BufferedReader(isr);
			BufferedWriter out = new BufferedWriter(osr);

			while (command(in, out))
				;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCommandException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		network = null;
	}

	public boolean command(BufferedReader in, BufferedWriter out) throws IOException,
			UnsupportedCommandException {
		String response = in.readLine();
		if (response == null) {
			return false;
		}
		String[] command = response.split(" ");
		try {
			String action = command[0];
			if ("TRAIN".equals(action)) {
				if (network == null) {
					throw new UnsupportedCommandException(
							"Network was not created. Please use CREATE command.");
				}
				NetState state = train(command, in);
				if (state == NetState.RESTART) {
					out.append("RESTART\n");
					out.flush();
				} else if (state == NetState.TESTING) {
					out.append("LEARNED\n");
					out.flush();
				}
			} else if ("TEST".equals(action)) {
				if (network == null) {
					throw new UnsupportedCommandException(
							"Network was not created. Please use CREATE command.");
				}
				String value = test(command, in);
				out.append("RESULT " + value + "\n");
				out.flush();
			} else if ("OPTIONS".equals(action)) {
				Options options = new Options();
				String[] fields = options.fields();
				out.append("OPTIONS " + fields.length + "\n");
				for (String f : fields) {
					out.append(f + "\n");
				}
				out.flush();
				System.out.println("Options sent!");
			} else if ("CREATE".equals(action)) {
				try {
					create(command, in);
				} catch (Exception e) {
					e.printStackTrace();
					throw new UnsupportedCommandException("Command failed.");
				}
				System.out.println("Network created!");
				out.append("READY\n");
				out.flush();
			} else if ("QUIT".equals(action)) {
				halt[0] = true;
				return false;
			}
		} catch (UnsupportedDataException ude) {
			System.err.println(ude);
		}
		return true;
	}

	private void create(String[] command, BufferedReader in) throws IOException, IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		int lines = Integer.parseInt(command[1]);
		Options options = new Options();
		for (int i = 0; i < lines; i++) {
			String opt = in.readLine();
			String[] parts = opt.split("=", 2);
			options.setOption(parts[0].trim(), parts[1].trim());
		}
		this.network = null;
		this.network = new Network(options);
	}

	private NetState train(String[] command, BufferedReader in) throws IOException, UnsupportedDataException {
		if ("chars".equals(command[1])) {
			byte[] data = readData(command, in);
			String supervised = command[4];
			return network.train(data, supervised);
		}
		return NetState.LEARNING;
	}

	private String test(String[] command, BufferedReader in) throws IOException, UnsupportedDataException {
		if ("chars".equals(command[1])) {
			byte[] data = readData(command, in);
			// slot = Integer.parseInt(command[4]);
			String value = network.run(data);
			return value.replace('\n', ' ');
		}
		return null;
	}

	private byte[] readData(String[] command, BufferedReader in) throws IOException, UnsupportedDataException {
		int width = Integer.parseInt(command[2]);
		int height = Integer.parseInt(command[3]);
		byte[] matrix = Base64.decode(in.readLine().replace("!", "\n"));
		if (width * height != matrix.length) {
			String dimensions = width + "*" + height;
			throw new UnsupportedDataException("Expected matrix of " + dimensions + ", got " + matrix.length
					+ " instead.");
		}
		return matrix;
	}
}