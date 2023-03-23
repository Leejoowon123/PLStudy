import java.io.IOException;
import java.io.PushbackInputStream;

public class RDParser {
    
    int token, ch;
    int x = 0;
    private PushbackInputStream input;
    
    RDParser(PushbackInputStream is){
        input = is;
    }
    
    void error() {
        System.out.println("syntax error");
        System.exit(1);
    }
    
    void match(int c) {
        if(token == c) token = getToken();
        else error();
    }
    
    void command() {
        expr();
        if(token == '\n') System.out.println("good syntax");
        else error();
    }
    
    void expr() {
        if(token == 't' || token == 'f') {
            booleanValue();
        } else {
            bexp();
            if(token == '&' || token == '|' || token == '!') {
                match(token);
                bexp();
            }
        }
    }

    void booleanValue() {
        if(token == 't') {
            match('t');
            match('r');
            match('u');
            match('e');
        } else {
            match('f');
            match('a');
            match('l');
            match('s');
            match('e');
        }
    }
    
    void bexp() {
        aexp();
        if(token == '!' || (token >= '<' && token <= '>') || token == '=') {
            relop();
            aexp();
        }
    }
    
    void relop() {
        if(token == '=') {
            match('=');
            match('=');
        } else if(token == '!') {
            match('!');
            if(token == '=') match('=');
        } else if(token == '<') {
            match('<');
            if(token == '=') match('=');
        } else if(token == '>') {
            match('>');
            if(token == '=') match('=');
        }
    }
    
    void aexp() {
        term();
        while(token == '+' || token == '-') {
            match(token);
            term();
        }
    }
    
    void term() {
        factor();
        while(token == '*' || token == '/') {
            match(token);
            factor();
        }
    }
    
    void factor() {
        if(token == '-') {
            match('-');
        }
        if(token == '(') {
            match('(');
            expr();
            match(')');
        } else {
            number();
        }
    }
    
    void number() {
        digit();
        while(Character.isDigit(token)) {
            digit();
        }
    }
    
    void digit() {
        if(Character.isDigit(token)) match(token);
        else error();
    }
    
    int getToken() {
        while(true) {
            try {
                ch = input.read();
                if(ch == ' ' || ch == '\t' || ch == '\r');
                else return ch;
            } catch(IOException e) {
                System.err.println(e);
            }
        }
    }
    
    void parse() {
        token = getToken(); // get the first character
        command();          // call the parsing command
    }
    
    public static void main(String[] args) {
        RDParser parser = new RDParser(new PushbackInputStream(System.in));
        while(true) {
            System.out.print(">> ");
            parser.parse();
        }
    }
}