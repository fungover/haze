package org.fungover.haze;

public class CommandSwitch {

    public void command(Command cmd) {

        switch (cmd) {
            case SET -> setCommand(Command.SET.getText());
            case GET -> getCommand(Command.GET.getText());
            case DEL -> delCommand(Command.DEL.getText());
            case PING -> pingCommand(Command.PING.getText());
            case LPUSH -> lpushCommand(Command.LPUSH.getText());
            case RPUSH -> rpushCommand(Command.RPUSH.getText());
            case LPOP -> lpopCommand(Command.LPOP.getText());
            case RPOP -> rpopCommand(Command.RPOP.getText());
            case LLEN -> llenCommand(Command.LLEN.getText());
            case LMOVE -> lmoveCommand(Command.LMOVE.getText());
            case LTRIM -> ltrimCommand(Command.LTRIM.getText());
            default -> System.out.println("Invalid Command");
        }
    }

    private void setCommand(String text) {
        System.out.println(text);
    }

    private void getCommand(String text) {
    }

    private void delCommand(String text) {
    }

    private void pingCommand(String text) {
    }

    private void lpushCommand(String text) {
    }

    private void rpushCommand(String text) {
    }

    private void lpopCommand(String text) {
    }

    private void rpopCommand(String text) {
    }

    private void llenCommand(String text) {
    }

    private void lmoveCommand(String text) {
    }

    private void ltrimCommand(String text) {
    }
}
