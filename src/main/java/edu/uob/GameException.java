package edu.uob;

public class GameException extends Exception {
    private static final long serialVersionUID = -2405736440969523511L;
    GameException(String message) {
        super("[ERROR]: " + message);
    }
}
