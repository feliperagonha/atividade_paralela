import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread
{
    private Parceiro            usuario;
    private Socket              conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisoraDeConexao
    (Socket conexao, ArrayList<Parceiro> usuarios)
    throws Exception
    {
        if (conexao==null)
            throw new Exception ("Conexao ausente");

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        this.conexao  = conexao;
        this.usuarios = usuarios;
    }

    public void run ()
    {

        ObjectOutputStream transmissor;
        try
        {
            transmissor =
            new ObjectOutputStream(
            this.conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            return;
        }
        
        ObjectInputStream receptor=null;
        try
        {
            receptor=
            new ObjectInputStream(
            this.conexao.getInputStream());
        }
        catch (Exception err0)
        {
            try
            {
                transmissor.close();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread
            
            return;
        }

        try
        {
            this.usuario =
            new Parceiro (this.conexao,
                          receptor,
                          transmissor);
        }
        catch (Exception erro)
        {} // sei que passei os parametros corretos

        try
        {
            synchronized (this.usuarios)
            {
                this.usuarios.add (this.usuario);
            }


            for(;;)
            {
                Comunicado comunicado = this.usuario.envie ();

                if (comunicado==null)
                    return;
                else if (comunicado instanceof PedidoDeOperacao)
                {
                    PedidoDeOperacao pedido =
                            (PedidoDeOperacao) comunicado;
                }
                else if (comunicado instanceof PedidoDeResultado)
                {
                    // nao utilizado nesta atividade
                }
                else if (comunicado instanceof PedidoDeOrdenacao)
                {
                    PedidoDeOrdenacao pedido =
                            (PedidoDeOrdenacao) comunicado;
                    long inicio =
                            System.currentTimeMillis();
                    byte[] vetorOrdenado =
                            ordenar(pedido.getVetor());
                    long tempo =
                            System.currentTimeMillis() - inicio;
                    this.usuario.receba(
                            new Resultado(
                                    vetorOrdenado,
                                    tempo
                            )
                    );
                }
                else if (comunicado instanceof PedidoParaSair)
                {
                    synchronized (this.usuarios)
                    {
                        this.usuarios.remove (this.usuario);
                    }
                    this.usuario.adeus();
                }
            }
        }
        catch (Exception erro)
        {
            try
            {
                transmissor.close ();
                receptor   .close ();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread

            return;
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
            int tamanhoParte =
                    tamanhoBase + (i < resto ? 1 : 0);
            byte[] parte =
                    new byte[tamanhoParte];
            System.arraycopy(
                    vetor,
                    posicao,
                    parte,
                    0,
                    tamanhoParte
            );
            posicao += tamanhoParte;

            ordenadoras[i] =
                    new Ordenadora(parte);
            ordenadoras[i].setName(
                    "Ordenadora-" + (i + 1)
            );
            ordenadoras[i].start();
        }

        for (Ordenadora ordenadora : ordenadoras) {
            esperar(ordenadora);
        }

        byte[][] partes =
                new byte[quantidadeOrdenadoras][];
        for (int i = 0; i < ordenadoras.length; i++) {
            partes[i] =
                    ordenadoras[i].getResultado();
        }

        int rodada = 1;
        while (partes.length > 1) {
            int quantidadeMisturadoras =
                    partes.length / 2;
            Misturadora[] misturadoras =
                    new Misturadora[quantidadeMisturadoras];

            for (int i = 0; i < quantidadeMisturadoras; i++) {
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
                misturadoras[i].start();
            }

            for (Misturadora misturadora : misturadoras) {
                esperar(misturadora);
            }

            int quantidadeNovasPartes =
                    quantidadeMisturadoras
                    + (partes.length % 2);
            byte[][] novasPartes =
                    new byte[quantidadeNovasPartes][];

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
        } catch (InterruptedException erro) {
            Thread.currentThread().interrupt();
        }
    }
}
