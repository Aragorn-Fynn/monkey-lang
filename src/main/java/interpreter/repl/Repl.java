package interpreter.repl;

import interpreter.lexer.Lexer;
import interpreter.lexer.Token;
import interpreter.lexer.TokenTypeEnum;
import org.jline.reader.*;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

/**
 * 命令行工具
 */
public class Repl {
    private static String prompt = ">>";
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

        while (true) {
            try {
                String line = lineReader.readLine(prompt);

                // 1. 生成词法分析器
                Lexer lexer = new Lexer(line);
                Token token = lexer.nextToken();
                while (token.getType() != TokenTypeEnum.EOF) {
                    System.out.println(token);
                    token = lexer.nextToken();
                }

            } catch (UserInterruptException e) {
                // TODO
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
