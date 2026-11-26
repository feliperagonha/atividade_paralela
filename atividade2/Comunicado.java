import java.io.Serializable;

public abstract class Comunicado implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final class PedidoDeOrdenacao extends Comunicado {
        private static final long serialVersionUID = 1L;
        public final byte[] vetor;

        public PedidoDeOrdenacao(byte[] vetor) {
            this.vetor = vetor;
        }
    }

    public static final class PedidoDeMergeamento extends Comunicado {
        private static final long serialVersionUID = 1L;
        public final byte[] esquerda;
        public final byte[] direita;

        public PedidoDeMergeamento(byte[] esquerda, byte[] direita) {
            this.esquerda = esquerda;
            this.direita = direita;
        }
    }

    public static final class OrdenacaoPronta extends Comunicado {
        private static final long serialVersionUID = 1L;
    }

    public static final class MergeamentoPronto extends Comunicado {
        private static final long serialVersionUID = 1L;
    }

    public static final class PedidoDeResultado extends Comunicado {
        private static final long serialVersionUID = 1L;
    }

    public static final class Resultado extends Comunicado {
        private static final long serialVersionUID = 1L;
        public final byte[] vetor;

        public Resultado(byte[] vetor) {
            this.vetor = vetor;
        }
    }

    public static final class PedidoParaSair extends Comunicado {
        private static final long serialVersionUID = 1L;
    }
}
