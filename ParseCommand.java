package com.richercoin.client;

import java.io.IOException;

import com.richercoin.control.message.ControlMessage;
import com.richercoin.control.message.DataControlMessage;
import com.richercoin.control.message.MessageContants;
import com.richercoin.util.FileUtils;
import com.richercoin.util.StringUtils;

public class ParseCommand {
	public static String ROOT_PREFIX = "rc";

	public static ParseCommand instance;

	private ParseCommand() {
	}

	public static ParseCommand getInstance() {
		if (null == instance) {
			instance = new ParseCommand();
		}
		return instance;
	}

	public ControlMessage parse(String command) {

		command = command.trim();
		if (!command.startsWith(ROOT_PREFIX)) {
			return null;
		}

		ControlMessage controlMsg = null;

		command = command.substring(command.indexOf(ROOT_PREFIX) + ROOT_PREFIX.length()).trim();
		String[] arrayCommand = command.split(" ");
		if (arrayCommand[0].equals("write")) {
			if (arrayCommand[1].equals("allcoin")) {
				controlMsg = new ControlMessage(MessageContants.CONTROL_WRITE_ALLCOIN);
				controlMsg.setContent(new Boolean(true).toString());
			} else if (arrayCommand[1].equals("alluser")) {
				controlMsg = new ControlMessage(MessageContants.CONTROL_WRITE_ALL_USER);
				if (arrayCommand.length > 2) {
					// TODO tuqt: check boolean type
					if (arrayCommand[2].equals("true") || arrayCommand[2].equals("false")) {
						controlMsg.setContent(arrayCommand[2]);
					}
				} else {
					controlMsg.setContent("Write all users");
				}
			} else if (arrayCommand[1].equals("-someuser")) {
				controlMsg = new ControlMessage(MessageContants.CONTROL_WRITE_SOME_USER);
				controlMsg.setContent("Write some users");
			}
		} else if (arrayCommand[0].equals("get")) {
			controlMsg = new ControlMessage(MessageContants.CONTROL_GET_USER);
			controlMsg.setContent(arrayCommand[1]);
		} else if (arrayCommand[0].equals("addUser")) {
			String xmlUserNode = null;
			if (arrayCommand[1].equals("-input")) {
				PrintComment.toTerminal("Please input User Notification follow XML Format:");
				xmlUserNode = KeyboardProcess.getInstance().getAllInput();
			} else if (arrayCommand[1].equals("-file")) {
				try {
					xmlUserNode = FileUtils.readFileToString(arrayCommand[2]);
				} catch (IOException e) {
					PrintComment.toTerminal("Failed to load file: " + arrayCommand[2]);
				}
			}
			xmlUserNode = StringUtils.removeSpaceTab(xmlUserNode).trim();
			if (StringUtils.notNullorBlank(xmlUserNode)) {
				DataControlMessage dataMsg = new DataControlMessage(MessageContants.CONTROL_UPDATE_USER);
				dataMsg.setContent(MessageContants.CONTROL_UPDATE_USER_ADD);
				dataMsg.addMutilpleContent(MessageContants.CONTROL_UPDATE_USER_ADD, xmlUserNode);
				controlMsg = dataMsg;
			}
		} else if (arrayCommand[0].equals("stopUser") || arrayCommand[0].equals("removeUser") || arrayCommand[0].equals("resumeUser")||arrayCommand[0].equals("deleteUser")) {
			DataControlMessage dataMsg = new DataControlMessage(MessageContants.CONTROL_UPDATE_USER);
			if (arrayCommand[0].equals("stopUser")) {
				dataMsg.setContent(MessageContants.CONTROL_UPDATE_USER_STOP);
			} else if (arrayCommand[0].equals("removeUser")) {
				dataMsg.setContent(MessageContants.CONTROL_UPDATE_USER_REMOVE);
			} else if (arrayCommand[0].equals("resumeUser")) {
				dataMsg.setContent(MessageContants.CONTROL_UPDATE_USER_RESUME);
			} else if (arrayCommand[0].equals("deleteUser")) {
				dataMsg.setContent(MessageContants.CONTROL_UPDATE_USER_DELETE);
			}
			String xmlString = null;
			if (arrayCommand[1].equals("-input")) {
				PrintComment.toTerminal("Please input User Name or User Id follow \",\" for each separator elements:");
				xmlString = KeyboardProcess.getInstance().getAllInput();
			} else if (arrayCommand[1].equals("-file")) {
				try {
					xmlString = FileUtils.readFileToString(arrayCommand[2]);
				} catch (IOException e) {
					PrintComment.toTerminal("Failed to load file: " + arrayCommand[2]);
				}
			} else if (arrayCommand[1].equals("-all")) {
				dataMsg.addMutilpleContent("all", "all");
				controlMsg = dataMsg;
			}
			
			if (StringUtils.notNullorBlank(xmlString)) {
				xmlString = StringUtils.removeSpaceTab(xmlString).trim();
				if (xmlString.startsWith("Name")) {
					xmlString = xmlString.substring("Name".length());
					dataMsg.addMutilpleContent("Name", xmlString);
					controlMsg = dataMsg;
				} else if (xmlString.startsWith("Id")) {
					xmlString = xmlString.substring("Id".length());
					dataMsg.addMutilpleContent("Id", xmlString);
					controlMsg = dataMsg;
				} 
			}
		} else if (arrayCommand[0].equals("updatealllink")) {
			controlMsg = new ControlMessage(MessageContants.CONTROL_UPDATE_ALLLINKCOIN);
			controlMsg.setContent("Update all link");
		} else if (arrayCommand[0].equals("exit") || arrayCommand[0].equals("quit")) {
			controlMsg = new ControlMessage(MessageContants.CONTROL_EXIT_CONNECTION);
			controlMsg.setContent(new Boolean(true).toString());
		}
		return controlMsg;
	}

}