import java.util.Scanner;

/**
 * Entorno de ejecución: menú para ingresar y analizar expresiones aritméticas.
 */
public class Main {

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        PilaExpresion pila = new PilaExpresion();

        menu(entrada, pila);

        entrada.close();
    }

    /** Muestra el menú y atiende las opciones hasta que el usuario salga. */
    private static void menu(Scanner entrada, PilaExpresion pila) {
        String opcion = "";

        while (!opcion.equals("0")) {
            System.out.println();
            System.out.println("=== ANÁLISIS DE EXPRESIONES ARITMÉTICAS ===");
            System.out.println("1. Ingresar expresión");
            System.out.println("2. Analizar expresión");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = entrada.nextLine().trim();
            System.out.println();

            switch (opcion) {
                case "1":
                    System.out.println("Puede usar números, variables, + - * / y paréntesis.");
                    System.out.print("Expresión: ");
                    if (pila.ingresarExpresion(entrada.nextLine())) {
                        System.out.println("Expresión guardada. Use la opción 2 para analizarla.");
                    } else {
                        System.out.println("No se guardó la expresión. Intente de nuevo.");
                    }
                    break;

                case "2":
                    pila.analizarExpresion();
                    break;

                case "0":
                    System.out.println("¡Hasta luego!");
                    break;

                default:
                    System.out.println("Opción inválida. Digite 1, 2 o 0.");
            }
        }
    }
}
