import java.io.*;

public class Calculator {
   
   int ch, token, value;
   private PushbackInputStream input;
   final int NUMBER = 256;
   boolean isBool = false;
   
   public Calculator(PushbackInputStream is) {
      input = is;
   }
   
   void error() {
        System.out.println("syntax error");
        System.exit(1);
    }
    
   String booleanValue() {
       if(token == 't') {
           match('t');
           match('r');
           match('u');
           match('e');
           return "true";
       } else {
           match('f');
           match('a');
           match('l');
           match('s');
           match('e');
           return "false";
       }
   }
   
    void match(int c) {
        if(token == c) token = getToken();
        else error();
    }
    
    void command() {
        //expr();
       String a = expr();
       
        if(token == '\n') System.out.println(a);
        else error();
    }
    
    int getToken() {
        while(true) {
            try {
                ch = input.read();
                if(ch == ' ' || ch == '\t' || ch == '\r');
                else {
                   if(Character.isDigit(ch)) {
                      value = number();
                      input.unread(ch);
                      return NUMBER;
                   } else return ch;
                }
            } catch(IOException e) {
                System.err.println(e);
            }
        }
    }
    
    void parse() {
        token = getToken(); // get the first character
        command();          // call the parsing command
    }
    
    String expr() {
       if(token == '!') {
          match('!');
          return Boolean.toString(!Boolean.valueOf(expr()));
       }
       if(token == 't' || token == 'f') {
    	   String re = booleanValue();
    	   if(re == "true") return "true";
    	   else if(re == "false") return "false";
       }
       String result = bexpr();
       //System.out.println("expr : " + result);
       
       if(result == "true" || result == "false") {
          while(token == '&') {
             match('&');
             String bexpr = bexpr();
             
             result = Boolean.toString(Boolean.valueOf(result) & Boolean.valueOf(bexpr));
          }
          while(token == '|') {
             match('|');
             String bexpr = bexpr();
             
             result = Boolean.toString(Boolean.valueOf(result) | Boolean.valueOf(bexpr));
          }
       }
       
       return result;
    }
    
    String bexpr() {
       int result = aexpr();
       
       if(isBool == true) {
          if(result == 1) return "true";
          else if(result == 0) return "false";
       }
       //System.out.println("bexpr1 : " + result);
       
       if(token == '=' || token == '!' || (token >= '<' && token <= '>')) {
          int relop = relop(result);
          if(relop == 1) {
             return "true";
          } else if(relop == 0) {
             return "false";
          }
       }
       //System.out.println("bexpr2 : " + result);
       return Integer.toString(result);
    }
    
    int relop(int origin) {
       //System.out.println(token);
       if(token == '=') {
          match('=');
          match('=');
          int aexp1 = aexpr();
          if(origin == aexp1) return 1;
          else return 0;
       } else if(token == '!') {
          match('!');
          if(token == '=') {
             match('=');
             int aexp1 = aexpr();
             if(origin != aexp1) return 1;
             else return 0;
          }
       } else if(token == '<') {
          match('<');
          
          if(token == '=') {
             match('=');
             int aexp1 = aexpr();
             if(origin <= aexp1) return 1;
             else return 0;
          }
          int aexp1 = aexpr();
          //System.out.println("relop aexp1 : " + aexp1);
          if(origin < aexp1) return 1;
          else return 0;
       } else if(token == '>') {
          match('>');
          
          if(token == '=') {
             match('=');
             int aexp1 = aexpr();
             if(origin >= aexp1) return 1;
             else return 0;
          }
          int aexp1 = aexpr();
          if(origin > aexp1) return 1;
          else return 0;
       }
       return -1;
    }
    
    int aexpr() {
       int result = term();
       
       while(token == '+') {
          match('+');
          if(token == '+') error();
          result += term();
       }
       
       while(token == '-') {
          match('-');
          result -= term();
       }
       
       if(token == '=' || token == '!' || (token >= '<' && token <= '>')) {
          result = relop(result);
          isBool = true;
       }
       
       //System.out.println("aexpr" + result);
       return result;
    }
    
    int term() {
       int result = factor();
       while(token == '*') {
          match('*');
          result *= factor();
       }
       while(token == '/') {
          match('/');
          result /= factor();
       }
       
       //System.out.println("term" + result);
       return result;
    }
    
    int factor() {
       //System.out.println(token);
       int result = 0;
       boolean minus = false;
       if(token == '-') {
          match('-');
          minus = true;
       }
       if(token == '(') {
          match('(');
          //System.out.println(token);
          //if()
          result = aexpr();
          match(')');
       } else if(token == NUMBER) {
          result = value;
          match(NUMBER);
       }
       
       if(minus == true) {
          result *= -1;
       }
       //System.out.println("factor" + result);
       //System.out.println("factor" + result);
       return result;
    }
    
    int number() {
       /* number -> digit { digit } */
        int result = ch - '0';
        try  {
            ch = input.read();
            while (Character.isDigit(ch)) {
                result = 10 * result + ch -'0';
                ch = input.read(); 
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return result;
    }
    
    public static void main(String[] args) {
       Calculator calc = new Calculator(new PushbackInputStream(System.in));
       while(true) {
            System.out.print(">> ");
            calc.parse();
        }
    }
}