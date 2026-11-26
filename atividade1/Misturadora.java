public class Misturadora extends Thread {

    private byte[] vetor1;
    private byte[] vetor2;
    private byte[] resultado;


    public Misturadora(
            byte[] vetor1,
            byte[] vetor2) {

        this.vetor1 = vetor1;
        this.vetor2 = vetor2;
    }


    @Override
    public void run() {

        System.out.println(
                "[" + getName()
                + "] iniciou - juntando "
                + vetor1.length
                + " + "
                + vetor2.length
                + " elementos"
        );


        resultado =
                merge(
                        vetor1,
                        vetor2
                );


        // Mostra o vetor somente quando for pequeno
        if (resultado.length <= 40) {

            System.out.println(
                    "[" + getName()
                    + "] resultado: "
                    + vetorParaTexto(resultado)
            );
        }


        System.out.println(
                "[" + getName()
                + "] terminou - resultado com "
                + resultado.length
                + " elementos"
        );
    }


    public byte[] getResultado() {

        return resultado;
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

                resultado[k] =
                        a[i];

                i++;

            } else {

                resultado[k] =
                        b[j];

                j++;
            }

            k++;
        }


        while (i < a.length) {

            resultado[k] =
                    a[i];

            i++;
            k++;
        }


        while (j < b.length) {

            resultado[k] =
                    b[j];

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