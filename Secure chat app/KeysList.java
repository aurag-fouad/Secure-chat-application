import java.security.PublicKey;

public class KeysList {
    public String name;
    public PublicKey key;

    public KeysList(String name, PublicKey key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public PublicKey getKey() {
        return key;
    }
}
