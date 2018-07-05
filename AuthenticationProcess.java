package com.richercoin.client;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.richercoin.control.AuthenticationVerifier.AuthMechanism;
import com.richercoin.control.message.AckMessage;
import com.richercoin.control.message.AuthenticationRequest;
import com.richercoin.control.message.AuthenticationResponse;
import com.richercoin.control.message.MessageContants;
import com.richercoin.log.ManagerLogger;
import com.richercoin.socket.ClientConnection;

public class AuthenticationProcess {
	private static Logger logger = ManagerLogger.getInstance().getLogger(AuthenticationProcess.class);
	private boolean isByTerminal;
	private KeyboardProcess keyboard;
	private ClientConnection client;

	public AuthenticationProcess(ClientConnection client) {
		isByTerminal = true;
		keyboard = KeyboardProcess.getInstance();
		this.client = client;
	}

	public boolean verifyValidUser() throws IOException {
		boolean isValid = false;
		AuthenticationRequest request;
		int numberTry = 0;
		do {
			StringBuilder strB = new StringBuilder();
			request = new AuthenticationRequest(AuthMechanism.BASIC);
			request.setHaveAck(true);
			++numberTry;
			if (numberTry > 3) {
				PrintComment.toTerminal("Your username/password is incorrect many times.");
				PrintComment.toTerminal("Please try again after 5 minutes");
				AckMessage ack = new AckMessage(MessageContants.ACKNOWLEDGE_AUTHENTICATION, AckMessage.NOTHING_REFID);
				ack.setDesciption(MessageContants.USERCONTROL_ISVALID_WRONGMANYTIMES);
				ack.setSuccess(false);
				client.writeObject(ack);
			}
			if (isByTerminal) {

				PrintComment.toTerminal("Please input \nUsername:");
				strB.append("username=").append(keyboard.getString());
				PrintComment.toTerminal("Password:");

				// "1234" is password prefix. It same from prefix server to
				// avoid hack for attacker;
				strB.append(" ");
				strB.append("password=").append("1234" + keyboard.getString());
				request.setContent(strB.toString());
				client.writeObject(request);

				try {
					Object obj = client.readObject();
					if (obj instanceof AuthenticationResponse) {
//						AuthenticationResponse resp = (AuthenticationResponse) obj;
						// TODO tuqt: implement other case authentication;
					} else if (obj instanceof AckMessage) {
						AckMessage ack = (AckMessage) obj;
						if (ack.isSuccess() && ack.getAckForMsg() == request.getId()) {
							isValid = true;
						} else {
							PrintComment.toTerminal("Can not access. " + ack.getError());
						}
					}
					if (isValid) {
						break;
					}
				} catch (ClassNotFoundException e) {
					logger.error("Exception:" + e.getMessage(), e);
				}
			}

		} while (true);

		return isValid;
	}

	public boolean isByTerminal() {
		return isByTerminal;
	}

	public void setByTerminal(boolean isByTerminal) {
		this.isByTerminal = isByTerminal;
	}

	public ClientConnection getClient() {
		return client;
	}

	public void setClient(ClientConnection client) {
		this.client = client;
	}

}
