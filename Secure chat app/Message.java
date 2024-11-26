import java.io.Serializable;

public class Message implements Serializable {
    byte[] data;

    public Message(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}