package edu.uob;

import java.io.Serial;

public class GameException extends Exception {
    @Serial
    private static final long serialVersionUID = -2405736440969523511L;
    GameException(String message) {
        super("[ERROR]: " + message);
    }
}
