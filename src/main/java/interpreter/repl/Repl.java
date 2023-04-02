package interpreter.repl;

import interpreter.ast.ProgramNode;
import interpreter.eval.Environment;
import interpreter.eval.Evaluator;
import interpreter.lexer.Lexer;
import interpreter.object.ValueObject;
import interpreter.parser.Parser;
import org.jline.reader.*;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * command line
 */
public class Repl {
    private static String prompt = ">>> ";
    public void run() throws IOException {

        // 1. create terminal
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        // 2. command completer
        Completer completer = new StringsCompleter("fn", "let", "true", "false", "if", "else", "return");

        // 3. create reader
        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
                .build();

        // 4. output the welcome message
        final String WELCOME_MESSAGE = "Welcome, Monkey Language Interpreter!\n\n";
        terminal.writer().append(WELCOME_MESSAGE);

        // 5. create the env
        Environment env = new Environment();

        /**
         * loop util Ctrl+D
         */
        while (true) {
            try {
                String line = lineReader.readLine(prompt);

                // 1. get lexer
                Lexer lexer = new Lexer(line);
                // 2. get parser
                Parser parser = new Parser(lexer);
                ProgramNode program = parser.parseProgram();
                // System.out.println(program.toString());
                if (parser.getErrors().size()>0) {
                    System.out.println(parser.getErrors().stream().collect(Collectors.joining("\n")));
                    continue;
                }

                // 3. evaluate the ast;
                Evaluator evaluator = new Evaluator();
                ValueObject value = evaluator.eval(program, env);
                if (value != null) {
                    terminal.writer().println(value.inspect());
                }
            } catch (UserInterruptException e) {// Ctrl + C
                System.out.println("KeyboardInterrupt");
            } catch (EndOfFileException e2) {// Ctrl + D
                System.out.println("\nBye!");
                return;
            }
        }
    }

}
