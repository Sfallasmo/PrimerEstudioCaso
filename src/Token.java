
public class Token {

    // Atributos
    private final String texto;    // contenido del token, por ejemplo "15" o "+"
    private final TipoToken tipo;  // categoría a la que pertenece

    // Constructor
    public Token(String texto, TipoToken tipo) {
        this.texto = texto;
        this.tipo = tipo;
    }

    // Getters (no hay setters: un token no cambia después de creado)
    public String getTexto() {
        return texto;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    /** Un operando es un número literal o una variable numérica. */
    public boolean esOperando() {
        return tipo == TipoToken.NUMERO || tipo == TipoToken.VARIABLE;
    }

    @Override
    public String toString() {
        return texto;
    }
}

/** Categorías de token contempladas por la aplicación. */
enum TipoToken {
    NUMERO, VARIABLE, OPERADOR, PARENTESIS_IZQ, PARENTESIS_DER
}
