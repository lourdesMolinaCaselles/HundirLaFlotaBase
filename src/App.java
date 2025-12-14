import java.util.Random;
import java.util.Scanner;

public class App {
    /**
     * Scanner para la entrada de datos por teclado.
     */
    private static Scanner sc = new Scanner(System.in);

    /**
     * Tamaño del tablero de juego.
     */
    private static int TAM = 10;

    /**
     * Tablero del jugador 1.
     * Cada posición contiene:
     * 0 = agua, 1-5 = barco sin tocar, 6 = barco tocado, 7 = disparo a agua.
     */
    private static int barcosJ1[][] = new int[TAM][TAM];

    /**
     * Tablero del jugador 2.
     * Igual que {@link #barcosJ1}.
     */
    private static int barcosJ2[][] = new int[TAM][TAM];

    /**
     * Número de casillas de barco que quedan por hundir del jugador 1.
     */
    private static int nBarcos1;

    /**
     * Número de casillas de barco que quedan por hundir del jugador 2.
     */
    private static int nBarcos2;

    /**
     * Matriz auxiliar para colocar barcos temporalmente al generar el tablero.
     */
    private static int matrizAux[][] = new int[TAM][TAM];

    /**
     * Cantidad de barcos por tipo (índice 0 = tamaño 1, índice 4 = tamaño 5).
     */
    private static final int cantidad[] = { 5, 4, 3, 2, 1 };

    /**
     * Tamaño de los barcos (1 a 5 casillas).
     */
    private static final int tamanios[] = { 1, 2, 3, 4, 5 };

    /**
     * Nombres de los barcos según su tamaño.
     */
    private static final String[] nombres = { "Lancha", "Crucero", "Submarino", "Buque", "Portaaviones" };

    /**
     * Direcciones posibles para colocar los barcos: arriba, derecha, abajo,
     * izquierda.
     */
    private static final int direcciones[][] = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

    // Colores ANSI para imprimir el tablero en consola
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREY = "\u001B[90m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String[] colores = { ANSI_BLACK, ANSI_CYAN, ANSI_BLUE, ANSI_YELLOW, ANSI_GREEN, ANSI_PURPLE,
            ANSI_RED, ANSI_GREY };

    /**
     * Método principal que inicia el juego.
     *
     * @param args Argumentos de la línea de comandos (no se usan).
     * @throws Exception Si ocurre algún error de ejecución inesperado.
     */
    public static void main(String[] args) throws Exception {
        prepararJuego();

        sc.close();
    }

    /**
     * Muestra un menú de selección de modo de juego y devuelve la opción elegida.
     * 
     * Muestra un menú en consola con opciones: 1 = PVP, 2 = PVE, 0 = salir.
     * Valida la entrada del usuario y repite la solicitud hasta que sea correcta.
     *
     * @return Opción elegida por el usuario: 0 = Salir, 1 = PVP, 2 = PVE.
     */
    public static int menuJuego() {

        int opcion = -1;

        do {

            System.out.println(ANSI_YELLOW + "---------------------------------");
            System.out.println(ANSI_YELLOW + "|" + ANSI_RED + "             MENÚ      " + ANSI_YELLOW + "        |");
            System.out.println("|                               |");
            System.out.println("---------------------------------");
            System.out.println(
                    ANSI_YELLOW + "|" + ANSI_PURPLE + "       1. Jugador vs Jugador  " + ANSI_YELLOW + " |");
            System.out.println(ANSI_YELLOW + "|" + ANSI_BLUE + "       2. Jugador vs Máquina  " + ANSI_YELLOW + " |");
            System.out.println(ANSI_YELLOW + "|" + ANSI_GREEN + "       0. Salir               " + ANSI_YELLOW + " |");
            System.out.println("---------------------------------" + ANSI_WHITE);
            System.out.println("Elija una opción :");

            opcion = sc.nextInt();
        } while (opcion < 0 || opcion > 2);

        return opcion;
    }

    /**
     * Inicializa los tableros de ambos jugadores, calcula los barcos y solicita el
     * modo de juego.
     * Dependiendo de la opción elegida, inicia PVP o PVE.
     * 
     * Este método realiza los siguientes pasos:
     * <ol>
     * <li>Llama a {@link #generarTablero()} para crear los tableros de ambos
     * jugadores.</li>
     * <li>Calcula el número total de casillas de barco con
     * {@link #calcularNBarcos(int[], int[])}.</li>
     * <li>Muestra ambos tableros completos.</li>
     * <li>Muestra el menú de selección de juego y ejecuta el modo
     * seleccionado.</li>
     * </ol>
     *
     * @postcondición Los tableros de los jugadores están generados y el juego
     *                inicia en el modo seleccionado.
     */
    public static void prepararJuego() {

        barcosJ1 = generarTablero();
        barcosJ2 = generarTablero();

        nBarcos1 = calcularNBarcos(cantidad, tamanios);
        nBarcos2 = calcularNBarcos(cantidad, tamanios);

        System.out.println("Tablero jugador 1");
        mostrarTablero(barcosJ1);
        System.out.println("Tablero jugador 2");
        mostrarTablero(barcosJ2);

        int opcion = menuJuego();

        if (opcion == 1) {
            jugarPVP();
        } else if (opcion == 2) {
            jugarPVE();
        } else {
            System.out.println("Salir del juego ");
        }
    }

    /**
     * Calcula el número total de casillas de barco dadas las cantidades y tamaños
     * de barcos.
     *
     * @param cantidades Array con la cantidad de barcos por tipo.
     * @param tamanios   Array con los tamaños de los barcos correspondientes.
     * @return Total de casillas de barco.
     * @precondición {@code cantidades.length == tamanios.length}.
     */
    public static int calcularNBarcos(int[] cantidades, int[] tamanios) {
        int total = 0;
        for (int i = 0; i < tamanios.length; i++) {
            total += cantidades[i] * tamanios[i];
        }

        return total;
    }

    /**
     * Ejecuta el modo Jugador vs Jugador.
     * Permite que ambos jugadores disparen alternativamente hasta que uno gane.
     *
     * @precondición Los tableros de ambos jugadores deben estar inicializados.
     * @postcondición El juego termina cuando {@link #nBarcos1} o {@link #nBarcos2}
     *                llega a 0.
     */
    public static void jugarPVP() {
        int x, y;
        boolean turnoJ1 = true;

        while (nBarcos1 > 0 && nBarcos2 > 0) {
            if (turnoJ1) {
                System.out.println("Turno Jugador 1");
                mostrarJugador1();

                System.out.print("Fila: ");
                x = sc.nextInt();
                System.out.print("Columna: ");
                y = sc.nextInt();

                if (disparar(barcosJ2, x, y)) {
                    nBarcos2--;
                }
            } else {
                System.out.println("Turno Jugador 2");
                mostrarJugador2();

                System.out.print("Fila: ");
                x = sc.nextInt();
                System.out.print("Columna: ");
                y = sc.nextInt();

                if (disparar(barcosJ1, x, y)) {
                    nBarcos1--;
                }
            }
            turnoJ1 = !turnoJ1;
        }

        if (nBarcos1 == 0) {
            System.out.println("¡¡Gana el jugador 2 :) !!");
        } else {
            System.out.println("¡¡Gana el jugador 1 ;) !!");
        }
    }

    /**
     * Ejecuta el modo Jugador vs Máquina.
     * La máquina dispara aleatoriamente.
     *
     * @precondición Los tableros de ambos jugadores deben estar inicializados.
     * @postcondición El juego termina cuando {@link #nBarcos1} o {@link #nBarcos2}
     *                llega a 0.
     */
    public static void jugarPVE() {

        Random r = new Random();
        int x, y;

        while (nBarcos1 > 0 && nBarcos2 > 0) {
            System.out.println("Tu turno");
            mostrarJugador1();

            System.out.print("Fila: ");
            x = sc.nextInt();
            System.out.print("Columna: ");
            y = sc.nextInt();

            if (disparar(barcosJ2, x, y)) {
                nBarcos2--;
            }

            x = r.nextInt(TAM);
            y = r.nextInt(TAM);

            System.out.println("La máquina dispara a " + x + "," + y);
            if (disparar(barcosJ1, x, y)) {
                nBarcos1--;
            }
        }

        if (nBarcos1 == 0) {
            System.out.println("¡¡Gana la máquina :( lo siento !!");
        } else {
            System.out.println("¡¡Ganas tú confiaba en ti y en la humanidad ;) !!");
        }
    }

    /**
     * Realiza un disparo sobre el tablero especificado.
     *
     * @param matriz Tablero donde se dispara.
     * @param x      Coordenada X (fila) del disparo.
     * @param y      Coordenada Y (columna) del disparo.
     * @return {@code true} si se tocó un barco, {@code false} si fue agua o disparo
     *         repetido.
     * @precondición {@code 0 <= x < TAM && 0 <= y < TAM}.
     * @postcondición La matriz queda actualizada con el resultado del disparo (6 =
     *                tocado, 7 = agua).
     */
    public static boolean disparar(int[][] matriz, int x, int y) {

        if (matriz[x][y] >= 1 && matriz[x][y] <= 5) {
            matriz[x][y] = 6;
            return true;
        } else if (matriz[x][y] == 0) {
            matriz[x][y] = 7;
        }
        return false;
    }

    /**
     * Determina si un barco ha sido tocado o hundido a partir de la casilla
     * disparada.
     *
     * @param matriz Tablero donde se encuentra el barco.
     * @param x      Coordenada X (fila) del disparo.
     * @param y      Coordenada Y (columna) del disparo.
     * @return {@code true} si el barco está completamente hundido, {@code false} si
     *         solo ha sido tocado.
     */
    public static boolean cantarDisparo(int[][] matriz, int x, int y) {
        // TODO OPCIONAL función cantarDisparo
        return false;
    }

    // #region Preparación del tablero

    /**
     * Genera un tablero aleatorio con los barcos colocados.
     * 
     * Para cada tipo de barco:
     * <ul>
     * <li>Se intenta colocar la cantidad correspondiente de barcos de ese
     * tamaño.</li>
     * <li>Se elige una posición aleatoria y una dirección válida usando
     * {@link #comprobarDirecciones(int, int, int)}.</li>
     * <li>Se coloca el barco con {@link #copiarBarcoEn(int, int, int, int)}.</li>
     * </ul>
     *
     * @return Matriz {@link int[][]} de tamaño {@link #TAM} x {@link #TAM} con los
     *         barcos colocados.
     *         0 = agua, 1-5 = barco sin tocar.
     * @precondición {@link #TAM} debe ser mayor que 0.
     * @postcondición {@link #matrizAux} queda reiniciada a cero y se devuelve un
     *                tablero completo.
     */
    public static int[][] generarTablero() {
        Random r = new Random();
        int x, y, direccion;

        for (int i = cantidad.length - 1; i >= 0; i--) {
            for (int j = 0; j < cantidad[i]; j++) {
                do {
                    x = r.nextInt(TAM);
                    y = r.nextInt(TAM);
                } while ((direccion = comprobarDirecciones(x, y, tamanios[i])) == -1);

                copiarBarcoEn(x, y, direccion, tamanios[i]);
            }
        }

        int[][] matrizJuego = new int[TAM][TAM];
        for (int i = 0; i < TAM; i++) {
            matrizJuego[i] = matrizAux[i].clone();
        }
        matrizAux = new int[TAM][TAM];
        return matrizJuego;
    }

    /**
     * Comprueba si una posición (x,y) está libre para colocar un barco. Comprueba
     * que todas las casillas del barco tengan espacio
     * para lo cual debe haber al menos una casilla vacía entre barco y barco.
     * 
     * La comprobación se realiza en la matriz matrizAux
     *
     * @param x Fila de la posición a comprobar.
     * @param y Columna de la posición a comprobar.
     * @return {@code true} si la posición y sus adyacentes están libres,
     *         {@code false} en caso contrario.
     */
    public static boolean comprobarPosicion(int x, int y) {

        boolean libre = true;

        if (x >= 0 && x < TAM && y >= 0 && y < TAM) {
            if (matrizAux[x][y] != 0) {
                libre = false;
            }

            for (int i = 0; i < direcciones.length; i++) {
                int xNueva = x + direcciones[i][0];
                int yNueva = y + direcciones[i][1];
                if (xNueva >= 0 && xNueva < TAM && yNueva >= 0 && yNueva < TAM) {
                    if (matrizAux[xNueva][yNueva] != 0) {
                        libre = false;
                    }
                }
            }
        } else {
            libre = false;
        }

        return libre;
    }

    /**
     * Determina una dirección viable para colocar un barco de tamaño dado desde
     * (x,y).
     *
     * @param x        Fila de inicio.
     * @param y        Columna de inicio.
     * @param tamBarco Tamaño del barco.
     * @return Índice de la dirección válida
     *         (0=arriba,1=derecha,2=abajo,3=izquierda), -1 si no hay direcciones
     *         válidas.
     * @precondición {@code 1 <= tamBarco <= 5} y
     *               {@code 0 <= x < TAM && 0 <= y < TAM}.
     */
    public static int comprobarDirecciones(int x, int y, int tamBarco) {
        Random r = new Random();
        int[] direccionesViables = new int[4];
        int nDireccionesViables = 0;
        boolean viable = true;
        if (!comprobarPosicion(x, y))
            return -1;

        if (tamBarco == 1)
            return 1;

        for (int i = 0; i < direcciones.length; i++) {
            viable = true;
            for (int j = 0; j < tamBarco; j++) {
                if (!comprobarPosicion(x + direcciones[i][0] * j, y + direcciones[i][1] * j)) {
                    viable = false;
                }
            }

            if (viable) {
                direccionesViables[nDireccionesViables] = i;
                nDireccionesViables++;
            }
        }

        if (nDireccionesViables == 0)
            return -1;
        else
            return direccionesViables[r.nextInt(nDireccionesViables)];
    }

    /**
     * Copia un barco en la posición (x,y) siguiendo la dirección indicada.
     *
     * @param x         Fila inicial.
     * @param y         Columna inicial.
     * @param direccion Dirección del barco
     *                  (0=arriba,1=derecha,2=abajo,3=izquierda).
     * @param tamanio   Tamaño del barco.
     * @precondición {@code 0 <= x,y < TAM}, {@code direccion ∈ [0,3]},
     *               {@code tamanio > 0}.
     * @postcondición {@link #matrizAux} queda modificada con el barco colocado.
     */
    public static void copiarBarcoEn(int x, int y, int direccion, int tamanio) {
        for (int i = 0; i < tamanio; i++) {
            matrizAux[x + direcciones[direccion][0] * i][y + direcciones[direccion][1] * i] = tamanio;
        }
    }

    // #endregion

    /**
     * Muestra por consola el tablero completo, incluyendo barcos y disparos.
     *
     * @param matriz Tablero a mostrar.
     */
    public static void mostrarTablero(int[][] matriz) {

          System.out.print("   ");
    for (int j = 0; j < TAM; j++) {
        System.out.print(j + " ");
    }
    System.out.println();

    // Filas
    for (int i = 0; i < TAM; i++) {

        System.out.print(i + "  ");

        for (int j = 0; j < TAM; j++) {
            int valor = matriz[i][j];
            String color = ANSI_WHITE;

            if (valor == 0) {
                color = ANSI_BLACK;
            } else if (valor == 1) {
                color = ANSI_CYAN;
            } else if (valor == 2) {
                color = ANSI_BLUE;
            } else if (valor == 3) {
                color = ANSI_YELLOW;
            } else if (valor == 4) {
                color = ANSI_GREEN;
            } else if (valor == 5) {
                color = ANSI_PURPLE;
            } else if (valor == 6) {
                color = ANSI_RED;
            } else if (valor == 7) {
                color = ANSI_GREY;
            }

            System.out.print(color + "▄ " + ANSI_WHITE);
        }
        System.out.println();
    }
}

    /**
     * Muestra el tablero del jugador 1 y el tablero rival, ocultando los barcos
     * enemigos.
     */
    public static void mostrarJugador1() {

    System.out.println("Tu tablero");
    mostrarTablero(barcosJ1);

    System.out.println("Tablero contrincante");

    System.out.print("   ");
    for (int j = 0; j < TAM; j++) {
        System.out.print(j + " ");
    }
    System.out.println();

    for (int i = 0; i < TAM; i++) {

        System.out.print(i + "  ");

        for (int j = 0; j < TAM; j++) {
            int valor = barcosJ2[i][j];

            if (valor == 6) {
                System.out.print(ANSI_RED + "▄ " + ANSI_WHITE);
            } else if (valor == 7) {
                System.out.print(ANSI_GREY + "▄ " + ANSI_WHITE);
            } else {
                System.out.print(ANSI_BLACK + "▄ " + ANSI_WHITE);
            }
        }
        System.out.println();
    }
}


    /**
     * Muestra el tablero del jugador 2 y el tablero rival, ocultando los barcos
     * enemigos.
     */
    public static void mostrarJugador2() {

    System.out.print("   ");
    for (int j = 0; j < TAM; j++) {
        System.out.print(j + " ");
    }
    System.out.println();

    for (int i = 0; i < TAM; i++) {

        System.out.print(i + "  ");

        for (int j = 0; j < TAM; j++) {
            int valor = barcosJ1[i][j];

            if (valor == 6) {
                System.out.print(ANSI_RED + "▄ " + ANSI_WHITE);
            } else if (valor == 7) {
                System.out.print(ANSI_GREY + "▄ " + ANSI_WHITE);
            } else {
                System.out.print(ANSI_BLACK + "▄ " + ANSI_WHITE);
            }
        }
        System.out.println();
    }
}
}
