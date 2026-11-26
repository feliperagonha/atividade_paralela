import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Sequencial {

    public static void main(String[] args) {

        Scanner teclado =
                new Scanner(System.in);

        Random random =
                new Random();


        System.out.println(
                "ORDENACAO SEM PARALELISMO COM MERGE SORT"
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


        } else {


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


        System.out.println();

        System.out.println(
                "[Sequencial] Iniciando Merge Sort..."
        );

        System.out.println(
                "[Sequencial] Elementos: "
                + vetor.length
        );


        long inicio =
                System.currentTimeMillis();


        vetor =
                mergeSort(vetor);


        long fim =
                System.currentTimeMillis();


        long tempoTotal =
                fim - inicio;


        System.out.println(
                "[Sequencial] Ordenacao finalizada."
        );


        if (estaOrdenado(vetor)) {


            System.out.println(
                    "[Sequencial] Verificacao: vetor ordenado corretamente."
            );


        } else {


            System.out.println(
                    "[Sequencial] Verificacao: o vetor possui erro de ordenacao."
            );
        }


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
                        vetor,
                        0,
                        vetor.length - 1
                );


            } else if (menu == 2) {


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
                        || fimTrecho >= vetor.length
                        || inicioTrecho > fimTrecho
                ) {


                    System.out.println(
                            "Intervalo invalido. "
                            + "As posicoes vao de 0 ate "
                            + (vetor.length - 1)
                            + "."
                    );


                } else {


                    imprimirVetor(
                            vetor,
                            inicioTrecho,
                            fimTrecho
                    );
                }


            } else if (menu == 3) {


                System.out.println(
                        "Tempo total da ordenacao sem paralelismo: "
                        + tempoTotal
                        + " ms"
                );


            } else if (menu != 0) {


                System.out.println(
                        "Opcao invalida."
                );
            }


        } while (menu != 0);


        teclado.close();
    }


    private static byte[] mergeSort(
            byte[] vetor) {


        if (vetor.length <= 1) {

            return vetor;
        }


        int meio =
                vetor.length / 2;


        byte[] esquerda =
                new byte[meio];


        byte[] direita =
                new byte[
                        vetor.length - meio
                ];


        for (int i = 0;
             i < meio;
             i++) {


            esquerda[i] =
                    vetor[i];
        }


        for (int i = meio;
             i < vetor.length;
             i++) {


            direita[i - meio] =
                    vetor[i];
        }


        esquerda =
                mergeSort(esquerda);


        direita =
                mergeSort(direita);


        return merge(
                esquerda,
                direita
        );
    }


    private static byte[] merge(
            byte[] a,
            byte[] b) {


        byte[] resultado =
                new byte[
                        a.length + b.length
                ];


        int i = 0;
        int j = 0;
        int k = 0;


        while (
                i < a.length
                && j < b.length
        ) {


            if (a[i] <= b[j]) {


                resultado[k] =
                        a[i];

                i++;


            } else {


                resultado[k] =
                        b[j];

                j++;
            }


            k++;
        }


        while (i < a.length) {


            resultado[k] =
                    a[i];

            i++;
            k++;
        }


        while (j < b.length) {


            resultado[k] =
                    b[j];

            j++;
            k++;
        }


        return resultado;
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