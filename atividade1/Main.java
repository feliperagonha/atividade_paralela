import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner teclado =
                new Scanner(System.in);

        Random random =
                new Random();


        System.out.println(
                "Atividade Programação Paralela e Distribuida"
        );

        System.out.println();


        int tamanho =
                lerPositivo(
                        teclado,
                        "Quantos elementos deseja no vetor? "
                );


        byte[] vetor;


        try {

            vetor =
                    new byte[tamanho];

        } catch (OutOfMemoryError e) {

            System.out.println(
                    "Nao foi possivel criar um vetor desse tamanho."
            );

            teclado.close();

            return;
        }


        int opcao;


        do {

            System.out.println(
                    "1 - Digitar os elementos"
            );

            System.out.println(
                    "2 - Gerar aleatoriamente"
            );

            opcao =
                    lerInteiro(
                            teclado,
                            "Opcao: "
                    );

        } while (
                opcao != 1
                && opcao != 2
        );


        /*
         * Preenchimento manual
         */

        if (opcao == 1) {

            for (int i = 0;
                 i < vetor.length;
                 i++) {

                int valor;


                do {

                    valor =
                            lerInteiro(
                                    teclado,
                                    "vetor[" + i + "] = "
                            );


                    if (
                            valor < -128
                            || valor > 127
                    ) {

                        System.out.println(
                                "Digite um valor entre -128 e 127."
                        );
                    }

                } while (
                        valor < -128
                        || valor > 127
                );


                vetor[i] =
                        (byte) valor;
            }

        }

        /*
         * Preenchimento automatico
         */

        else {

            for (int i = 0;
                 i < vetor.length;
                 i++) {

                vetor[i] =
                        (byte)
                        (random.nextInt(256) - 128);
            }


            System.out.println(
                    "Vetor gerado aleatoriamente."
            );
        }


        /*
         * Quantidade de processadores
         */

        int processadores =
                Runtime
                        .getRuntime()
                        .availableProcessors();


        int quantidadeOrdenadoras =
                processadores - 1;


        if (quantidadeOrdenadoras < 1) {

            quantidadeOrdenadoras = 1;
        }


        System.out.println();

        System.out.println(
                "[Main] Processadores encontrados: "
                + processadores
        );

        System.out.println(
                "[Main] Threads ordenadoras: "
                + quantidadeOrdenadoras
        );

        System.out.println(
                "[Main] Dividindo o vetor em partes..."
        );


        /*
         * Inicio da medicao do tempo
         */

        long inicio =
                System.currentTimeMillis();


        /*
         * Cria as ordenadoras
         */

        Ordenadora[] ordenadoras =
                new Ordenadora[
                        quantidadeOrdenadoras
                ];


        int tamanhoBase =
                vetor.length
                        / quantidadeOrdenadoras;


        int resto =
                vetor.length
                        % quantidadeOrdenadoras;


        int posicao = 0;


        for (int i = 0;
             i < quantidadeOrdenadoras;
             i++) {


            int tamanhoParte =
                    tamanhoBase;


            if (i < resto) {

                tamanhoParte++;
            }


            byte[] parte =
                    new byte[tamanhoParte];


            for (int j = 0;
                 j < tamanhoParte;
                 j++) {

                parte[j] =
                        vetor[posicao];

                posicao++;
            }


            ordenadoras[i] =
                    new Ordenadora(parte);


            ordenadoras[i].setName(
                    "Ordenadora-" + (i + 1)
            );


            System.out.println(
                    "[Main] "
                    + ordenadoras[i].getName()
                    + " preparada com "
                    + tamanhoParte
                    + " elementos"
            );
        }


        /*
         * Start das ordenadoras
         */

        System.out.println();

        System.out.println(
                "=== THREADS ORDENADORAS ==="
        );


        System.out.println(
                "[Main] Iniciando todas as ordenadoras..."
        );


        for (int i = 0;
             i < ordenadoras.length;
             i++) {

            ordenadoras[i].start();
        }


        /*
         * Join das ordenadoras
         */

        System.out.println(
                "[Main] Aguardando as ordenadoras terminarem..."
        );


        for (int i = 0;
             i < ordenadoras.length;
             i++) {

            try {

                ordenadoras[i].join();

            } catch (
                    InterruptedException e
            ) {

                System.out.println(
                        "[Main] Erro ao esperar "
                        + ordenadoras[i].getName()
                );


                Thread
                        .currentThread()
                        .interrupt();


                teclado.close();

                return;
            }
        }


        System.out.println(
                "[Main] Todas as ordenadoras terminaram."
        );


        /*
         * Recupera os resultados
         */

        byte[][] partes =
                new byte[
                        ordenadoras.length
                ][];


        for (int i = 0;
             i < ordenadoras.length;
             i++) {

            partes[i] =
                    ordenadoras[i]
                            .getResultado();
        }


        /*
         * Rodadas de mistura
         */

        int rodada = 1;


        while (partes.length > 1) {


            int quantidadeMisturadoras =
                    partes.length / 2;


            System.out.println();

            System.out.println(
                    "=== RODADA "
                    + rodada
                    + " DE MISTURA ==="
            );


            System.out.println(
                    "[Main] Partes no inicio da rodada: "
                    + partes.length
            );


            System.out.println(
                    "[Main] Threads misturadoras: "
                    + quantidadeMisturadoras
            );


            Misturadora[] misturadoras =
                    new Misturadora[
                            quantidadeMisturadoras
                    ];


            /*
             * Cria as misturadoras
             */

            for (int i = 0;
                 i < quantidadeMisturadoras;
                 i++) {


                misturadoras[i] =
                        new Misturadora(
                                partes[i * 2],
                                partes[i * 2 + 1]
                        );


                misturadoras[i].setName(
                        "Misturadora-"
                        + rodada
                        + "-"
                        + (i + 1)
                );
            }


            /*
             * Start de todas
             * as misturadoras da rodada
             */

            for (int i = 0;
                 i < misturadoras.length;
                 i++) {

                misturadoras[i].start();
            }


            System.out.println(
                    "[Main] Aguardando o fim da rodada "
                    + rodada
                    + "..."
            );


            /*
             * Join da rodada
             */

            for (int i = 0;
                 i < misturadoras.length;
                 i++) {

                try {

                    misturadoras[i].join();

                } catch (
                        InterruptedException e
                ) {

                    System.out.println(
                            "[Main] Erro ao esperar "
                            + misturadoras[i].getName()
                    );


                    Thread
                            .currentThread()
                            .interrupt();


                    teclado.close();

                    return;
                }
            }


            /*
             * Verifica se existe
             * uma parte sem par
             */

            boolean sobrou =
                    partes.length % 2 != 0;


            int quantidadeNovasPartes =
                    quantidadeMisturadoras;


            if (sobrou) {

                quantidadeNovasPartes++;
            }


            byte[][] novasPartes =
                    new byte[
                            quantidadeNovasPartes
                    ][];


            /*
             * Guarda os resultados
             * das misturadoras
             */

            for (int i = 0;
                 i < misturadoras.length;
                 i++) {

                novasPartes[i] =
                        misturadoras[i]
                                .getResultado();
            }


            /*
             * Se sobrou uma parte,
             * ela passa para a proxima rodada
             */

            if (sobrou) {


                novasPartes[
                        novasPartes.length - 1
                ] =
                        partes[
                                partes.length - 1
                        ];


                System.out.println(
                        "[Main] Uma parte com "
                        + partes[
                                partes.length - 1
                        ].length
                        + " elementos ficou sem par "
                        + "e passou para a proxima rodada."
                );
            }


            partes =
                    novasPartes;


            System.out.println(
                    "[Main] Rodada "
                    + rodada
                    + " finalizada."
            );


            System.out.println(
                    "[Main] Partes restantes: "
                    + partes.length
            );


            rodada++;
        }


        /*
         * Vetor final
         */

        byte[] vetorOrdenado =
                partes[0];


        /*
         * Fim da medicao
         */

        long fim =
                System.currentTimeMillis();


        long tempoTotal =
                fim - inicio;


        System.out.println();

        System.out.println(
                "=== FIM DA ORDENACAO ==="
        );


        System.out.println(
                "[Main] Vetor final montado."
        );


        System.out.println(
                "[Main] Rodadas de mistura realizadas: "
                + (rodada - 1)
        );


        if (estaOrdenado(vetorOrdenado)) {

            System.out.println(
                    "[Main] Verificacao: vetor ordenado corretamente."
            );

        } else {

            System.out.println(
                    "[Main] Verificacao: o vetor possui erro de ordenacao."
            );
        }


        /*
         * Menu
         */

        int menu;


        do {

            System.out.println();

            System.out.println(
                    "1 - Imprimir todo o vetor"
            );

            System.out.println(
                    "2 - Imprimir uma parte do vetor"
            );

            System.out.println(
                    "3 - Mostrar o tempo de ordenacao"
            );

            System.out.println(
                    "0 - Sair"
            );


            menu =
                    lerInteiro(
                            teclado,
                            "Opcao: "
                    );


            if (menu == 1) {


                imprimirVetor(
                        vetorOrdenado,
                        0,
                        vetorOrdenado.length - 1
                );
            }


            else if (menu == 2) {


                int inicioTrecho =
                        lerInteiro(
                                teclado,
                                "Posicao inicial: "
                        );


                int fimTrecho =
                        lerInteiro(
                                teclado,
                                "Posicao final: "
                        );


                if (
                        inicioTrecho < 0
                        || fimTrecho
                        >= vetorOrdenado.length
                        || inicioTrecho
                        > fimTrecho
                ) {


                    System.out.println(
                            "Intervalo invalido. "
                            + "As posicoes vao de 0 ate "
                            + (vetorOrdenado.length - 1)
                            + "."
                    );


                } else {


                    imprimirVetor(
                            vetorOrdenado,
                            inicioTrecho,
                            fimTrecho
                    );
                }
            }


            else if (menu == 3) {


                System.out.println(
                        "Tempo total da ordenacao paralela: "
                        + tempoTotal
                        + " ms"
                );
            }


            else if (menu != 0) {


                System.out.println(
                        "Opcao invalida."
                );
            }


        } while (menu != 0);


        teclado.close();
    }


    private static int lerInteiro(
            Scanner teclado,
            String mensagem) {


        while (true) {


            try {


                System.out.print(
                        mensagem
                );


                return teclado.nextInt();


            } catch (
                    InputMismatchException e
            ) {


                System.out.println(
                        "Digite um numero inteiro valido."
                );


                teclado.nextLine();
            }
        }
    }


    private static int lerPositivo(
            Scanner teclado,
            String mensagem) {


        int valor;


        do {


            valor =
                    lerInteiro(
                            teclado,
                            mensagem
                    );


            if (valor <= 0) {


                System.out.println(
                        "O valor deve ser maior que zero."
                );
            }


        } while (valor <= 0);


        return valor;
    }


    private static void imprimirVetor(
            byte[] vetor,
            int inicio,
            int fim) {


        for (int i = inicio;
             i <= fim;
             i++) {


            System.out.print(
                    vetor[i] + " "
            );
        }


        System.out.println();
    }


    /*
     * Apenas para verificar
     * se o resultado esta correto.
     */

    private static boolean estaOrdenado(
            byte[] vetor) {


        for (int i = 1;
             i < vetor.length;
             i++) {


            if (
                    vetor[i - 1]
                    > vetor[i]
            ) {


                return false;
            }
        }


        return true;
    }
}