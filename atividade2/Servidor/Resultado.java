public class Resultado extends Comunicado {
    private byte[] vetorOrdenado;
    private long tempoOrdenacao;

    public Resultado(
            byte[] vetorOrdenado,
            long tempoOrdenacao) {
        this.vetorOrdenado = vetorOrdenado;
        this.tempoOrdenacao = tempoOrdenacao;
    }

    public byte[] getVetorOrdenado() {
        return this.vetorOrdenado;
    }

    public long getTempoOrdenacao() {
        return this.tempoOrdenacao;
    }
}
