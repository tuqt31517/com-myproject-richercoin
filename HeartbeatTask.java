package com.richercoin.client;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.richercoin.control.message.HeartBeatMessage;
import com.richercoin.log.ManagerLogger;
import com.richercoin.socket.ClientConnection;
import com.richercoin.tasks.AContextTask;
import com.richercoin.util.TimeUtils;

public class HeartbeatTask extends AContextTask {
	private static Logger logger = ManagerLogger.getInstance().getLogger(HeartbeatTask.class);
	ClientConnection client;

	public HeartbeatTask(ClientConnection client) {
		state = TASK_STATUS_NOTSTART;
	}

	@Override
	public void run() {
		state = TASK_STATUS_RUNNING;
		while (true) {
			try {
				HeartBeatMessage msg = new HeartBeatMessage(null, ClientProcessControl.getInstance().getLastCommandTime());
				client.writeObject(msg);

				Thread.sleep(TimeUtils._5_MIN);
			} catch (InterruptedException | IOException e) {
				break;
			}
		}
		state = TASK_STATUS_DONE;
		logger.info("Done. ");
	}

	@Override
	public void stop() {

	}

}
