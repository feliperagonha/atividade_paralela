import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        Random random = new Random();

        System.out.println("Atividade Programacao Paralela e Distribuida");
        System.out.println("Programa CLIENTE");

        String host = args.length > 0 ? args[0] : "localhost";
        int porta = args.length > 1 ? lerPorta(args[1]) : 12345;

        int tamanho = lerPositivo(teclado, "Quantos elementos deseja no vetor? ");
        byte[] vetor;
        try {
            vetor = new byte[tamanho];
        } catch (OutOfMemoryError e) {
            System.out.println("Nao foi possivel criar um vetor desse tamanho.");
            teclado.close();
            return;
        }

        int opcao;
        do {
            System.out.println("1 - Digitar os elementos");
            System.out.println("2 - Gerar aleatoriamente");
            opcao = lerInteiro(teclado, "Opcao: ");
        } while (opcao != 1 && opcao != 2);

        if (opcao == 1) {
            for (int i = 0; i < vetor.length; i++) {
                int valor;
                do {
                    valor = lerInteiro(teclado, "vetor[" + i + "] = ");
                    if (valor < -128 || valor > 127) {
                        System.out.println("Digite um valor entre -128 e 127.");
                    }
                } while (valor < -128 || valor > 127);
                vetor[i] = (byte) valor;
            }
        } else {
            for (int i = 0; i < vetor.length; i++) {
                vetor[i] = (byte) (random.nextInt(256) - 128);
            }
            System.out.println("Vetor gerado aleatoriamente.");
        }

        long inicio = System.currentTimeMillis();
        byte[] vetorOrdenado;
        try (
                Socket conexao = new Socket(host, porta);
                DataOutputStream saida =
                        new DataOutputStream(conexao.getOutputStream());
                DataInputStream entrada =
                        new DataInputStream(conexao.getInputStream())
        ) {
            saida.writeInt(vetor.length);
            saida.write(vetor);
            saida.flush();

            vetorOrdenado = new byte[entrada.readInt()];
            entrada.readFully(vetorOrdenado);
        } catch (IOException e) {
            System.out.println("Nao foi possivel comunicar com o servidor "
                    + host + ":" + porta);
            System.out.println(e.getMessage());
            teclado.close();
            return;
        }
        long tempoTotal = System.currentTimeMillis() - inicio;

        if (!estaOrdenado(vetorOrdenado)) {
            System.out.println("[Cliente] O servidor retornou um vetor nao ordenado.");
        }

        int menu;
        do {
            System.out.println();
            System.out.println("1 - Imprimir todo o vetor");
            System.out.println("2 - Imprimir uma parte do vetor");
            System.out.println("3 - Mostrar o tempo total");
            System.out.println("0 - Sair");

            menu = lerInteiro(teclado, "Opcao: ");
            if (menu == 1) {
                imprimirVetor(vetorOrdenado, 0, vetorOrdenado.length - 1);
            } else if (menu == 2) {
                int inicioTrecho =
                        lerInteiro(teclado, "Posicao inicial: ");
                int fimTrecho =
                        lerInteiro(teclado, "Posicao final: ");
                if (inicioTrecho < 0
                        || fimTrecho >= vetorOrdenado.length
                        || inicioTrecho > fimTrecho) {
                    System.out.println("Intervalo invalido. As posicoes vao de 0 ate "
                            + (vetorOrdenado.length - 1) + ".");
                } else {
                    imprimirVetor(vetorOrdenado, inicioTrecho, fimTrecho);
                }
            } else if (menu == 3) {
                System.out.println("Tempo total, incluindo rede: "
                        + tempoTotal + " ms");
            } else if (menu != 0) {
                System.out.println("Opcao invalida.");
            }
        } while (menu != 0);

        teclado.close();
    }

    private static int lerInteiro(Scanner teclado, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return teclado.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Digite um numero inteiro valido.");
                teclado.nextLine();
            }
        }
    }

    private static int lerPositivo(Scanner teclado, String mensagem) {
        int valor;
        do {
            valor = lerInteiro(teclado, mensagem);
            if (valor <= 0) {
                System.out.println("O valor deve ser maior que zero.");
            }
        } while (valor <= 0);
        return valor;
    }

    private static int lerPorta(String texto) {
        try {
            int porta = Integer.parseInt(texto);
            if (porta >= 1 && porta <= 65535) {
                return porta;
            }
        } catch (NumberFormatException e) {
            // Usa a porta padrao abaixo.
        }
        return 12345;
    }

    private static void imprimirVetor(
            byte[] vetor,
            int inicio,
            int fim) {
        for (int i = inicio; i <= fim; i++) {
            System.out.print(vetor[i] + " ");
        }
        System.out.println();
    }

    private static boolean estaOrdenado(byte[] vetor) {
        for (int i = 1; i < vetor.length; i++) {
            if (vetor[i - 1] > vetor[i]) {
                return false;
            }
        }
        return true;
    }
}
