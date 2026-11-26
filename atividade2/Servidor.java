import java.util.Scanner;

public class Servidor {

    public static void main(String[] args) {
        int porta = args.length > 0 ? lerPorta(args[0]) : 12345;

        AceitadoraDeConexao aceitadora =
                new AceitadoraDeConexao(porta);
        aceitadora.setName("AceitadoraDeConexao");
        aceitadora.start();

        System.out.println("Servidor iniciado na porta " + porta);
        System.out.println("Digite DESATIVAR para encerrar.");

        Scanner teclado = new Scanner(System.in);
        while (true) {
            try {
                String comando = teclado.nextLine().trim();
                if ("DESATIVAR".equalsIgnoreCase(comando)) {
                    aceitadora.ativarDesligamento();
                    try {
                        aceitadora.join();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    break;
                }
                System.out.println("Comando invalido.");
            } catch (java.util.NoSuchElementException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        teclado.close();
        System.out.println("Servidor desativado.");
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
}
