public class Ordenadora extends Thread {

    private byte[] vetor;
    private byte[] resultado;

    public Ordenadora(byte[] vetor) {
        this.vetor = vetor;
    }

    @Override
    public void run() {

        System.out.println(
                "[" + getName() + "] iniciou - "
                + vetor.length + " elementos"
        );

        // Mostra o pedaço somente se for pequeno
        if (vetor.length <= 20) {

            System.out.println(
                    "[" + getName() + "] recebeu: "
                    + vetorParaTexto(vetor)
            );
        }

        resultado = mergeSort(vetor);

        // Mostra o resultado somente se for pequeno
        if (resultado.length <= 20) {

            System.out.println(
                    "[" + getName() + "] ordenado: "
                    + vetorParaTexto(resultado)
            );
        }

        System.out.println(
                "[" + getName() + "] terminou"
        );
    }


    public byte[] getResultado() {

        return resultado;
    }


    private byte[] mergeSort(byte[] vetor) {

        // Caso base
        if (vetor.length <= 1) {

            return vetor;
        }


        int meio = vetor.length / 2;

        byte[] esquerda =
                new byte[meio];

        byte[] direita =
                new byte[vetor.length - meio];


        for (int i = 0;
             i < meio;
             i++) {

            esquerda[i] =
                    vetor[i];
        }


        for (int i = meio;
             i < vetor.length;
             i++) {

            direita[i - meio] =
                    vetor[i];
        }


        esquerda =
                mergeSort(esquerda);

        direita =
                mergeSort(direita);


        return merge(
                esquerda,
                direita
        );
    }


    private byte[] merge(
            byte[] a,
            byte[] b) {

        byte[] resultado =
                new byte[a.length + b.length];

        int i = 0;
        int j = 0;
        int k = 0;


        while (
                i < a.length
                && j < b.length
        ) {

            if (a[i] <= b[j]) {

                resultado[k] = a[i];
                i++;

            } else {

                resultado[k] = b[j];
                j++;
            }

            k++;
        }


        while (i < a.length) {

            resultado[k] = a[i];

            i++;
            k++;
        }


        while (j < b.length) {

            resultado[k] = b[j];

            j++;
            k++;
        }


        return resultado;
    }


    private String vetorParaTexto(
            byte[] vetor) {

        String texto = "";


        for (int i = 0;
             i < vetor.length;
             i++) {

            texto += vetor[i];

            if (i < vetor.length - 1) {

                texto += " ";
            }
        }


        return texto;
    }
}