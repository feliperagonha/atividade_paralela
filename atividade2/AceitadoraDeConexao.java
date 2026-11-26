import java.io.IOException;
import java.net.ServerSocket;

public class AceitadoraDeConexao extends Thread {
    private final ServerSocket servidor;

    public AceitadoraDeConexao(ServerSocket servidor) {
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            while (!servidor.isClosed()) {
                SupervisoraDeConexao supervisora = new SupervisoraDeConexao(servidor.accept());
                supervisora.start();
                // A próxima sessão só começa quando a atual termina.
                supervisora.join();
            }
        } catch (IOException erro) {
            if (!servidor.isClosed()) {
                System.err.println("Erro ao aceitar conexão: " + erro.getMessage());
            }
        } catch (InterruptedException interrupcao) {
            Thread.currentThread().interrupt();
        }
    }
}
