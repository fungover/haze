package org.fungover.haze;

public class Commands {
	public void command(Command cmd) {
		switch (cmd) {
			case SET -> SetCommand();
			case GET -> GetCommand();
			case DEL -> DelCommand();
			case PING -> PingCommand();
			default -> System.out.println("Invalid Command");
		}
	}

	private void handleSetCommand() {
	}

	private void handleGetCommand() {
	}

	private void handleDelCommand() {
	}

	private void handlePingCommand() {
	}
}

