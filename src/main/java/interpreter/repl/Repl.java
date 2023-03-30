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
 * 命令行工具
 */
public class Repl {
    private static String prompt = ">>> ";
    public void run() throws IOException {

        // 1. 创建terminal
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        // 2. 命令补全
        Completer completer = new StringsCompleter("fn", "let", "true", "false", "if", "else", "return");

        // 3. 创建命令行
        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
                .build();

        // 4. 输出欢迎语
        final String WELCOME_MESSAGE = "Welcome, Monkey Language Interpreter!\n\n";
        terminal.writer().append(WELCOME_MESSAGE);

        // 4. 创建环境
        Environment env = new Environment();
        while (true) {
            try {
                String line = lineReader.readLine(prompt);

                // 1. get lexer
                Lexer lexer = new Lexer(line);
                // 2. get parser
                Parser parser = new Parser(lexer);
                ProgramNode program = parser.parseProgram();
                if (parser.getErrors().size()>0) {
                    System.out.println(parser.getErrors().stream().collect(Collectors.joining("\n")));
                    continue;
                }

                // 3. evaluate the ast;
                Evaluator evaluator = new Evaluator();
                ValueObject value = evaluator.eval(program, env);
                System.out.println(value.inspect());
            } catch (UserInterruptException e) {// Ctrl + C
                System.out.println("KeyboardInterrupt");
            } catch (EndOfFileException e2) {// Ctrl + D
                System.out.println("\nBye!");
                return;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Repl().run();
    }
}
