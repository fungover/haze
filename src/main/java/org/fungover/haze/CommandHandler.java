package org.fungover.haze;

public class CommandHandler {
    public void handleCommand(Command cmd) {
        switch (cmd) {
            case SET -> handleSetCommand();
            case GET -> handleGetCommand();
            case DEL -> handleDelCommand();
            case PING -> handlePingCommand();
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