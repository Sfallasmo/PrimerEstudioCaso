import java.util.ArrayList;

/**
 * Pila dinámica de tokens para el análisis de expresiones aritméticas.
 * La clase nodo de la pila es Token (archivo Token.java).
 *
 * Flujo de trabajo:
 *   1. ingresarExpresion(): recibe el texto y lo tokeniza.
 *   2. analizarExpresion(): convierte los tokens a notación postfija con el
 *      algoritmo Shunting Yard y luego recorre la postfija con la misma pila.
 *      Si la pila termina vacía, la expresión está bien construida.
 */
public class PilaExpresion {

    // ------------------------------------------------------------------
    // Atributos
    // ------------------------------------------------------------------

    private ArrayList<Token> pila;    // la pila como tal
    private ArrayList<Token> tokens;  // expresión tokenizada, en notación infija
    private String expresion;         // texto original ingresado por el usuario

    // ------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------

    public PilaExpresion() {
        pila = new ArrayList<>();
        tokens = new ArrayList<>();
        expresion = "";
    }

    // Operaciones
    private boolean estaVacia() {
        return pila.isEmpty();
    }

    public void push(Token nodo) {
        pila.add(nodo);
    }

    public Token pop() {
        if (estaVacia()) {
            System.out.println("La pila esta vacia");
            return null;
        }
        return pila.removeLast();
    }

    public Token peek() {
        if (estaVacia()) {
            System.out.println("La pila esta vacia");
            return null;
        }
        return pila.getLast();
    }

    // ------------------------------------------------------------------
    // Operaciones públicas de la aplicación
    // ------------------------------------------------------------------

    /** Indica si hay una expresión tokenizada lista para analizar. */
    public boolean hayExpresion() {
        return !tokens.isEmpty();
    }

    /**
     * Recibe la expresión del usuario y la tokeniza.
     * @return true si todos los caracteres pudieron convertirse en tokens.
     */
    public boolean ingresarExpresion(String texto) {
        expresion = texto.trim();
        tokens = tokenizar(expresion);

        if (tokens == null) {
            tokens = new ArrayList<>();
            return false;
        }
        if (tokens.isEmpty()) {
            System.out.println("La expresión está vacía.");
            return false;
        }
        return true;
    }

    /** Analiza la expresión ingresada e informa si está bien construida. */
    public void analizarExpresion() {
        if (!hayExpresion()) {
            System.out.println("Primero debe ingresar una expresión.");
            return;
        }
        System.out.println("Expresión: " + expresion);
        System.out.println("Tokens (" + tokens.size() + "): " + enTexto(tokens));

        ArrayList<Token> postfija = convertirAPostfija();
        if (postfija == null) {
            System.out.println("Resultado: la expresión es INCORRECTA.");
            return;
        }
        System.out.println("Postfija:  " + enTexto(postfija));

        if (evaluarPostfija(postfija)) {
            System.out.println("Resultado: la expresión es CORRECTA.");
        } else {
            System.out.println("Resultado: la expresión es INCORRECTA.");
        }
    }

    // ------------------------------------------------------------------
    // Métodos de apoyo (privados)
    // ------------------------------------------------------------------

    /**
     * Separa el texto en tokens: números, variables, operadores y paréntesis.
     * @return la lista de tokens, o null si aparece un carácter no permitido.
     */
    private ArrayList<Token> tokenizar(String texto) {
        ArrayList<Token> resultado = new ArrayList<>();
        int i = 0;

        while (i < texto.length()) {
            char c = texto.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isDigit(c)) {
                // Número literal: dígitos con una parte decimal opcional
                int inicio = i;
                while (i < texto.length() && Character.isDigit(texto.charAt(i))) {
                    i++;
                }
                if (i + 1 < texto.length() && texto.charAt(i) == '.'
                        && Character.isDigit(texto.charAt(i + 1))) {
                    i++;
                    while (i < texto.length() && Character.isDigit(texto.charAt(i))) {
                        i++;
                    }
                }
                resultado.add(new Token(texto.substring(inicio, i), TipoToken.NUMERO));
            } else if (Character.isLetter(c) || c == '_') {
                // Variable numérica: letras, dígitos y guiones bajos
                int inicio = i;
                while (i < texto.length()
                        && (Character.isLetterOrDigit(texto.charAt(i)) || texto.charAt(i) == '_')) {
                    i++;
                }
                resultado.add(new Token(texto.substring(inicio, i), TipoToken.VARIABLE));
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                resultado.add(new Token(String.valueOf(c), TipoToken.OPERADOR));
                i++;
            } else if (c == '(') {
                resultado.add(new Token("(", TipoToken.PARENTESIS_IZQ));
                i++;
            } else if (c == ')') {
                resultado.add(new Token(")", TipoToken.PARENTESIS_DER));
                i++;
            } else {
                System.out.println("Error: carácter no permitido '" + c
                        + "' en la posición " + (i + 1) + ".");
                return null;
            }
        }
        return resultado;
    }

    /** Prioridad de un operador: * y / se resuelven antes que + y -. */
    private int prioridad(Token operador) {
        String simbolo = operador.getTexto();
        return (simbolo.equals("*") || simbolo.equals("/")) ? 2 : 1;
    }

    /**
     * Algoritmo Shunting Yard: reordena los tokens de notación infija a
     * postfija usando la pila para retener operadores y paréntesis.
     * @return la lista en postfija, o null si la expresión está mal construida.
     */
    private ArrayList<Token> convertirAPostfija() {
        ArrayList<Token> salida = new ArrayList<>();
        boolean esperaOperando = true; // al inicio debe venir un operando o un '('
        pila.clear();

        for (Token token : tokens) {
            switch (token.getTipo()) {
                case NUMERO:
                case VARIABLE:
                    if (!esperaOperando) {
                        return fallar("falta un operador antes de '" + token + "'.");
                    }
                    salida.add(token);
                    esperaOperando = false;
                    break;

                case OPERADOR:
                    if (esperaOperando) {
                        return fallar("al operador '" + token + "' le falta un operando.");
                    }
                    // Salen los operadores de igual o mayor prioridad
                    while (!estaVacia() && peek().getTipo() == TipoToken.OPERADOR
                            && prioridad(peek()) >= prioridad(token)) {
                        salida.add(pop());
                    }
                    push(token);
                    esperaOperando = true;
                    break;

                case PARENTESIS_IZQ:
                    if (!esperaOperando) {
                        return fallar("falta un operador antes de '('.");
                    }
                    push(token);
                    break;

                case PARENTESIS_DER:
                    if (esperaOperando) {
                        return fallar("falta un operando antes de ')'.");
                    }
                    // Salen los operadores hasta encontrar la pareja '('
                    while (!estaVacia() && peek().getTipo() != TipoToken.PARENTESIS_IZQ) {
                        salida.add(pop());
                    }
                    if (estaVacia()) {
                        return fallar("hay un ')' sin su correspondiente '('.");
                    }
                    pop(); // se descarta el '('
                    break;
            }
        }

        if (esperaOperando) {
            return fallar("la expresión termina sin un operando.");
        }

        // Se vacía la pila; un '(' residual indica que nunca se cerró
        while (!estaVacia()) {
            Token token = pop();
            if (token.getTipo() == TipoToken.PARENTESIS_IZQ) {
                return fallar("quedó un '(' en la pila sin su correspondiente ')'.");
            }
            salida.add(token);
        }
        return salida;
    }

    /**
     * Recorre la expresión postfija: los operandos entran a la pila y cada
     * operador saca sus dos operandos y deja un resultado. Al final se retira
     * el resultado y la pila debe quedar vacía.
     */
    private boolean evaluarPostfija(ArrayList<Token> postfija) {
        pila.clear();

        for (Token token : postfija) {
            if (token.esOperando()) {
                push(token);
            } else {
                if (pila.size() < 2) {
                    fallar("al operador '" + token + "' le faltan operandos.");
                    return false;
                }
                Token derecho = pop();
                Token izquierdo = pop();
                Token resultado = operar(izquierdo, token, derecho);
                if (resultado == null) {
                    fallar("división entre cero.");
                    return false;
                }
                push(resultado);
            }
        }

        Token resultado = pop();
        if (!estaVacia()) {
            fallar("quedaron " + pila.size() + " elementos residuales en la pila.");
            return false;
        }
        System.out.println("La pila quedó vacía al final del análisis.");
        if (resultado.getTipo() == TipoToken.NUMERO) {
            System.out.println("Valor:     " + resultado);
        } else {
            System.out.println("Valor:     depende de las variables.");
        }
        return true;
    }

    /**
     * Combina dos operandos. Si ambos son números se calcula el valor; si hay
     * alguna variable, el resultado se representa como un operando simbólico.
     * @return el token resultante, o null si se divide entre cero.
     */
    private Token operar(Token izquierdo, Token operador, Token derecho) {
        boolean ambosNumeros = izquierdo.getTipo() == TipoToken.NUMERO
                && derecho.getTipo() == TipoToken.NUMERO;

        if (!ambosNumeros) {
            String texto = "(" + izquierdo + " " + operador + " " + derecho + ")";
            return new Token(texto, TipoToken.VARIABLE);
        }

        double a = Double.parseDouble(izquierdo.getTexto());
        double b = Double.parseDouble(derecho.getTexto());
        double valor;

        switch (operador.getTexto()) {
            case "+": valor = a + b; break;
            case "-": valor = a - b; break;
            case "*": valor = a * b; break;
            default:
                if (b == 0) {
                    return null;
                }
                valor = a / b;
        }
        return new Token(formatear(valor), TipoToken.NUMERO);
    }

    /** Muestra los enteros sin parte decimal (9 en lugar de 9.0). */
    private String formatear(double valor) {
        if (valor == Math.rint(valor) && Math.abs(valor) < 1e15) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }

    /** Informa un error, limpia la pila y devuelve null. */
    private ArrayList<Token> fallar(String mensaje) {
        System.out.println("Error: " + mensaje);
        pila.clear();
        return null;
    }

    /** Une los tokens de una lista separados por espacios. */
    private String enTexto(ArrayList<Token> lista) {
        StringBuilder texto = new StringBuilder();
        for (Token token : lista) {
            texto.append(token).append(" ");
        }
        return texto.toString().trim();
    }
}