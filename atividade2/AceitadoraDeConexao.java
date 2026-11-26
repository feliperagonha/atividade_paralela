import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class AceitadoraDeConexao extends Thread {

    private final int porta;
    private volatile boolean desligando;
    private ServerSocket servidor;

    public AceitadoraDeConexao(int porta) {
        this.porta = porta;
    }

    @Override
    public void run() {
        try {
            servidor = new ServerSocket(porta);
            servidor.setReuseAddress(true);

            while (!desligando) {
                try {
                    Socket conexao = servidor.accept();
                    System.out.println(
                            "[AceitadoraDeConexao] Conexao recebida."
                    );

                    SupervisionadoraDeConexao supervisora =
                            new SupervisionadoraDeConexao(conexao);
                    supervisora.setName("SupervisoraDeConexao");
                    supervisora.start();
                    supervisora.join();
                } catch (SocketException e) {
                    if (!desligando) {
                        System.out.println(
                                "[AceitadoraDeConexao] Erro no socket: "
                                + e.getMessage()
                        );
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "[AceitadoraDeConexao] Nao foi possivel abrir a porta: "
                    + e.getMessage()
            );
        } finally {
            fecharServidor();
        }
    }

    public void ativarDesligamento() {
        desligando = true;
        fecharServidor();
    }

    private void fecharServidor() {
        if (servidor != null && !servidor.isClosed()) {
            try {
                servidor.close();
            } catch (IOException e) {
                // O socket ja esta sendo encerrado.
            }
        }
    }
}
