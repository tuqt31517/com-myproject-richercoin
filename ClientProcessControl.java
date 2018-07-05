package com.richercoin.client;

import java.io.IOException;

import com.richercoin.control.message.ASerializeMessage;
import com.richercoin.control.message.ControlMessage;
import com.richercoin.control.message.MessageContants;
import com.richercoin.control.message.ResultsResponseMessage;
import com.richercoin.socket.ClientConnection;
import com.richercoin.util.StringUtils;

public class ClientProcessControl {
	public static ClientProcessControl instance;
	private ClientConnection client;
	private AuthenticationProcess authProcess;
	private KeyboardProcess keyboard;
	public ParseCommand parseCommand;
	public long lastCommandTime;

	private ClientProcessControl() {
		keyboard = KeyboardProcess.getInstance();
		parseCommand = ParseCommand.getInstance();
	}

	public static ClientProcessControl getInstance() {
		if (null == instance) {
			instance = new ClientProcessControl();
		}
		return instance;
	}

	public void start() {
		try {
			client = new ClientConnection("localhost", 12345);

			authProcess = new AuthenticationProcess(client);

			if (authProcess.verifyValidUser()) {
				PrintComment.toTerminal("Opened Control session.Please input some control:\n");
				keyboard.clear();
				do {
					lastCommandTime = System.currentTimeMillis();
					String command = keyboard.getNexLine();
					if (StringUtils.nullorBlank(command)) {
						continue;
					}
					ControlMessage controlMsg = parseCommand.parse(command);

					if (command.equals("quit") || command.equals("exit")) {
						controlMsg = new ControlMessage(MessageContants.CONTROL_EXIT_CONNECTION);
						controlMsg.setContent(new Boolean(true).toString());
						client.writeObject(controlMsg);
						break;
					} else if (null == controlMsg) {
						PrintComment.toTerminal("Wrong with format control message");
					} else {
						client.writeObject(controlMsg);
						ASerializeMessage msg = (ASerializeMessage) client.readObject();
						if (msg instanceof ResultsResponseMessage) {
							PrintComment.toTerminal(((ResultsResponseMessage) msg).getContent());
						}
					}

				} while (true);
				PrintComment.toTerminal("Exit Control session.");
			}

			if (null != client) {
				keyboard.close();
				client.close();
			}

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getLastCommandTime() {
		return lastCommandTime;
	}

	protected void finalize() {
		System.out.println("DEBUG tuqt: test finalize");
		client.close();
	}
}
