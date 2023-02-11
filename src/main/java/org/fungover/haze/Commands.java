package org.fungover.haze;

public class Commands {
    public void command(Command cmd) {
        switch (cmd) {
            case SET -> setCommand();
            case GET -> getCommand();
            case DEL -> delCommand();
            case PING -> pingCommand();
            default -> System.out.println("Invalid Command");
        }
    }

    private void setCommand() {
    }

    private void getCommand() {
    }

    private void delCommand() {
    }

    private void pingCommand() {
    }
}

