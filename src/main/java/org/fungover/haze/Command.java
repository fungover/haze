package org.fungover.haze;

import java.util.Arrays;
import java.util.Optional;

enum Command {

    SET("placeholderText1"),
    GET("placeHolderText2"),
    DEL("placeHolderText3"),
    PING("placeHolderText4"),
    LPUSH("placeHolderText5"),
    RPUSH("placeHolderText6"),
    LPOP("placeHolderText7"),
    RPOP("placeHolderText8"),
    LLEN("placeHolderText9"),
    LMOVE("placeHolderText10"),
    LTRIM("placeHolderText11");

    private String text;

    Command(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static Optional<Command> fromText(String text) {
        return Arrays.stream(values())
                .filter(cmd -> cmd.text.equalsIgnoreCase(text))
                .findFirst();
    }
}
