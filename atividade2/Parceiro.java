import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Parceiro {
    private final ObjectInputStream receptor;
    private final ObjectOutputStream transmissor;

    public Parceiro(ObjectInputStream receptor, ObjectOutputStream transmissor) {
        this.receptor = receptor;
        this.transmissor = transmissor;
    }

    public Comunicado envie() throws IOException, ClassNotFoundException {
        Object recebido = receptor.readObject();
        if (recebido instanceof Comunicado comunicado) {
            return comunicado;
        }
        throw new IOException("A mensagem recebida precisa ser um Comunicado");
    }

    public void receba(Comunicado comunicado) throws IOException {
        transmissor.writeObject(comunicado);
        transmissor.reset();
        transmissor.flush();
    }
}
