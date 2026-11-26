import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class SupervisoraDeConexao extends Thread {
    private final Socket conexao;

    public SupervisoraDeConexao(Socket conexao) {
        this.conexao = conexao;
    }

    @Override
    public void run() {
        try (Socket socket = conexao;
             ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream())) {
            saida.flush();

            try (ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {
                entrada.setObjectInputFilter(SupervisoraDeConexao::filtrarEntrada);
                atender(new Parceiro(entrada, saida));
            }
        } catch (EOFException fimDaConexao) {
            // O cliente fechou a conexão antes de enviar PedidoParaSair.
        } catch (IOException | ClassNotFoundException erro) {
            System.err.println("Erro na sessão do cliente: " + erro.getMessage());
        } catch (InterruptedException interrupcao) {
            Thread.currentThread().interrupt();
        }
    }

    private void atender(Parceiro usuario)
            throws IOException, ClassNotFoundException, InterruptedException {
        byte[] ultimoResultado = null;
        while (true) {
            Comunicado pedido = usuario.envie();
            Comunicado resposta;

            if (pedido instanceof Comunicado.PedidoParaSair) {
                return;
            } else if (pedido instanceof Comunicado.PedidoDeOrdenacao ordenacao) {
                ultimoResultado = ordenar(validarVetor(ordenacao.vetor));
                resposta = new Comunicado.OrdenacaoPronta();
            } else if (pedido instanceof Comunicado.PedidoDeMergeamento mergeamento) {
                ultimoResultado = misturar(
                        validarVetor(mergeamento.esquerda),
                        validarVetor(mergeamento.direita));
                resposta = new Comunicado.MergeamentoPronto();
            } else if (pedido instanceof Comunicado.PedidoDeResultado && ultimoResultado != null) {
                resposta = new Comunicado.Resultado(ultimoResultado);
            } else {
                throw new IOException("Pedido inválido ou resultado indisponível");
            }

            usuario.receba(resposta);
        }
    }

    private static byte[] ordenar(byte[] vetor) throws InterruptedException {
        if (vetor.length < 2) {
            return vetor;
        }

        int quantidade = Math.min(vetor.length, Runtime.getRuntime().availableProcessors());
        Ordenadora[] ordenadoras = new Ordenadora[quantidade];
        int inicio = 0;
        for (int i = 0; i < quantidade; i++) {
            int fim = (int) ((long) vetor.length * (i + 1) / quantidade);
            ordenadoras[i] = new Ordenadora(Arrays.copyOfRange(vetor, inicio, fim));
            ordenadoras[i].start();
            inicio = fim;
        }

        byte[][] partes = new byte[quantidade][];
        for (int i = 0; i < quantidade; i++) {
            ordenadoras[i].join();
            partes[i] = ordenadoras[i].getResultado();
        }

        // Em cada rodada, mistura pares em paralelo e carrega a parte ímpar.
        while (partes.length > 1) {
            Misturadora[] misturadoras = new Misturadora[partes.length / 2];
            for (int i = 0; i < misturadoras.length; i++) {
                misturadoras[i] = new Misturadora(partes[2 * i], partes[2 * i + 1]);
                misturadoras[i].start();
            }

            byte[][] proximasPartes = new byte[partes.length - misturadoras.length][];
            for (int i = 0; i < misturadoras.length; i++) {
                misturadoras[i].join();
                proximasPartes[i] = misturadoras[i].getResultado();
            }
            if (partes.length % 2 != 0) {
                proximasPartes[proximasPartes.length - 1] = partes[partes.length - 1];
            }
            partes = proximasPartes;
        }
        return partes[0];
    }

    private static byte[] misturar(byte[] esquerda, byte[] direita) throws InterruptedException {
        Misturadora misturadora = new Misturadora(esquerda, direita);
        misturadora.start();
        misturadora.join();
        return misturadora.getResultado();
    }

    private static byte[] validarVetor(byte[] vetor) throws IOException {
        if (vetor == null) {
            throw new IOException("O pedido precisa incluir o vetor");
        }
        return vetor;
    }

    private static ObjectInputFilter.Status filtrarEntrada(ObjectInputFilter.FilterInfo info) {
        if (info.depth() > 4 || info.arrayLength() > 16_000_000) {
            return ObjectInputFilter.Status.REJECTED;
        }

        Class<?> tipo = info.serialClass();
        if (tipo == null) {
            return ObjectInputFilter.Status.UNDECIDED;
        }
        if (tipo == byte[].class || tipo == Comunicado.class
                || tipo == Comunicado.PedidoDeOrdenacao.class
                || tipo == Comunicado.PedidoDeMergeamento.class
                || tipo == Comunicado.PedidoDeResultado.class
                || tipo == Comunicado.PedidoParaSair.class) {
            return ObjectInputFilter.Status.ALLOWED;
        }
        return ObjectInputFilter.Status.REJECTED;
    }
}
