package interpreter;

import interpreter.repl.Repl;

import java.io.IOException;

/**
 * main class
 */
public class Interpreter {
    public static void main(String[] args) throws IOException {
        new Repl().run();
    }
}
