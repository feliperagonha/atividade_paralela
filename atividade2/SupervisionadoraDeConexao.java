import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SupervisionadoraDeConexao extends Thread {

    private final Socket conexao;

    public SupervisionadoraDeConexao(Socket conexao) {
        this.conexao = conexao;
    }

    @Override
    public void run() {
        try (
                DataInputStream entrada =
                        new DataInputStream(conexao.getInputStream());
                DataOutputStream saida =
                        new DataOutputStream(conexao.getOutputStream())
        ) {
            int tamanho = entrada.readInt();
            if (tamanho <= 0) {
                throw new IOException("Tamanho de vetor invalido.");
            }

            byte[] vetor = new byte[tamanho];
            entrada.readFully(vetor);
            byte[] vetorOrdenado = ordenar(vetor);

            saida.writeInt(vetorOrdenado.length);
            saida.write(vetorOrdenado);
            saida.flush();

            System.out.println(
                    "[SupervisoraDeConexao] Vetor de "
                    + tamanho + " elementos ordenado."
            );
        } catch (IOException e) {
            System.out.println(
                    "[SupervisoraDeConexao] Conexao encerrada: "
                    + e.getMessage()
            );
        } finally {
            try {
                conexao.close();
            } catch (IOException e) {
                // Nada adicional a fazer.
            }
        }
    }

    private byte[] ordenar(byte[] vetor) {
        int processadores =
                Runtime.getRuntime().availableProcessors();
        int quantidadeOrdenadoras =
                Math.max(1, processadores - 1);
        quantidadeOrdenadoras =
                Math.min(quantidadeOrdenadoras, vetor.length);

        Ordenadora[] ordenadoras =
                new Ordenadora[quantidadeOrdenadoras];
        int tamanhoBase =
                vetor.length / quantidadeOrdenadoras;
        int resto =
                vetor.length % quantidadeOrdenadoras;
        int posicao = 0;

        for (int i = 0; i < quantidadeOrdenadoras; i++) {
            int tamanhoParte = tamanhoBase + (i < resto ? 1 : 0);
            byte[] parte = new byte[tamanhoParte];
            System.arraycopy(vetor, posicao, parte, 0, tamanhoParte);
            posicao += tamanhoParte;

            ordenadoras[i] = new Ordenadora(parte);
            ordenadoras[i].setName("Ordenadora-" + (i + 1));
            ordenadoras[i].start();
        }

        for (Ordenadora ordenadora : ordenadoras) {
            esperar(ordenadora);
        }

        byte[][] partes =
                new byte[quantidadeOrdenadoras][];
        for (int i = 0; i < ordenadoras.length; i++) {
            partes[i] = ordenadoras[i].getResultado();
        }

        int rodada = 1;
        while (partes.length > 1) {
            int quantidadeMisturadoras = partes.length / 2;
            Misturadora[] misturadoras =
                    new Misturadora[quantidadeMisturadoras];

            for (int i = 0; i < quantidadeMisturadoras; i++) {
                misturadoras[i] =
                        new Misturadora(
                                partes[i * 2],
                                partes[i * 2 + 1]
                        );
                misturadoras[i].setName(
                        "Misturadora-" + rodada + "-" + (i + 1)
                );
                misturadoras[i].start();
            }

            for (Misturadora misturadora : misturadoras) {
                esperar(misturadora);
            }

            int novasPartesQuantidade =
                    quantidadeMisturadoras
                            + (partes.length % 2);
            byte[][] novasPartes =
                    new byte[novasPartesQuantidade][];
            for (int i = 0; i < misturadoras.length; i++) {
                novasPartes[i] =
                        misturadoras[i].getResultado();
            }
            if (partes.length % 2 != 0) {
                novasPartes[novasPartes.length - 1] =
                        partes[partes.length - 1];
            }
            partes = novasPartes;
            rodada++;
        }

        return partes[0];
    }

    private void esperar(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
