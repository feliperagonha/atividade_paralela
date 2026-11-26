import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Cliente {
    public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 3000;

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Uso esperado: java Cliente [HOST [PORTA]]\n");
            return;
        }

        String host = HOST_PADRAO;
        int porta = PORTA_PADRAO;
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length == 2) {
            try {
                porta = Integer.parseInt(args[1]);
            } catch (NumberFormatException erro) {
                System.err.println("Porta invalida!\n");
                return;
            }
        }

        Socket conexao;
        ObjectOutputStream transmissor;
        ObjectInputStream receptor;
        Parceiro servidor;
        try {
            conexao = new Socket(host, porta);
            transmissor =
                    new ObjectOutputStream(conexao.getOutputStream());
            receptor =
                    new ObjectInputStream(conexao.getInputStream());
            servidor =
                    new Parceiro(conexao, receptor, transmissor);
        } catch (Exception erro) {
            System.err.println("Indique o servidor e a porta corretos!\n");
            return;
        }

        try {
            new TratadoraDeComunicadoDeDesligamento(servidor).start();
        } catch (Exception erro) {
            System.err.println("Nao foi possivel iniciar a tratadora.\n");
        }

        try {
            System.out.println("Atividade 2 - Ordenacao paralela distribuida");
            System.out.print(
                    "Quantos elementos deseja no vetor? "
            );
            int tamanho = Teclado.getUmInt();
            if (tamanho <= 0) {
                System.err.println("Tamanho invalido!\n");
                servidor.adeus();
                return;
            }

            byte[] vetor = new byte[tamanho];
            System.out.print("1 - Digitar; 2 - Gerar aleatoriamente: ");
            int opcaoPreenchimento = Teclado.getUmInt();

            if (opcaoPreenchimento == 1) {
                for (int i = 0; i < vetor.length; i++) {
                    int valor;
                    do {
                        System.out.print("vetor[" + i + "] = ");
                        valor = Teclado.getUmInt();
                    } while (valor < -128 || valor > 127);
                    vetor[i] = (byte) valor;
                }
            } else if (opcaoPreenchimento == 2) {
                Random random = new Random();
                for (int i = 0; i < vetor.length; i++) {
                    vetor[i] = (byte) (random.nextInt(256) - 128);
                }
                System.out.println("Vetor gerado aleatoriamente.");
            } else {
                System.err.println("Opcao invalida!\n");
                servidor.adeus();
                return;
            }

            servidor.receba(new PedidoDeOrdenacao(vetor));

            Comunicado comunicado;
            do {
                comunicado = (Comunicado) servidor.espie();
            } while (!(comunicado instanceof Resultado));

            Resultado resultado =
                    (Resultado) servidor.envie();
            byte[] vetorOrdenado =
                    resultado.getVetorOrdenado();

            char opcao;
            do {
                System.out.println();
                System.out.println("1 - Imprimir todo o vetor");
                System.out.println("2 - Imprimir uma parte do vetor");
                System.out.println("3 - Mostrar o tempo de ordenacao");
                System.out.println("T - Terminar");
                System.out.print("Sua opcao: ");

                opcao = Character.toUpperCase(Teclado.getUmChar());
                if (opcao == '1') {
                    imprimirVetor(vetorOrdenado, 0, vetorOrdenado.length - 1);
                } else if (opcao == '2') {
                    System.out.print("Posicao inicial: ");
                    int inicio = Teclado.getUmInt();
                    System.out.print("Posicao final: ");
                    int fim = Teclado.getUmInt();

                    if (inicio < 0
                            || fim >= vetorOrdenado.length
                            || inicio > fim) {
                        System.out.println("Intervalo invalido.");
                    } else {
                        imprimirVetor(vetorOrdenado, inicio, fim);
                    }
                } else if (opcao == '3') {
                    System.out.println("Tempo de ordenacao no servidor: "
                            + resultado.getTempoOrdenacao() + " ms");
                } else if (opcao != 'T') {
                    System.out.println("Opcao invalida.");
                }
            } while (opcao != 'T');

            servidor.receba(new PedidoParaSair());
        } catch (Exception erro) {
            System.err.println("Erro de comunicacao com o servidor.");
        }

        System.out.println("Obrigado por usar este programa!");
        System.exit(0);
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
}
