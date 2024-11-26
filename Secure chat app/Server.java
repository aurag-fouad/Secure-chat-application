import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Server implements Runnable{

	private SecretKey key ;
	private String keyString;
	private String keyStringSent;
	private int KEY_SIZE = 128;
	private PublicKey publicKey;
	private String PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHXKE1uS5Tt4sgS9ZDe4Zu7+puq8Kadlrly8LWKtIZq6joe4sKbFPxRRMsvhUTmpHOyFPaM6bGof6LwMxtArN383B2341zA3sa7pEEfQ1L5rYZQbLmGBS84kU8lxyIN2v+PeldcKjpEZZm0MX5rsZPp/icnGARIjk2f5i4SjY9dQIDAQAB";

	public static List<KeysList> keysList = new ArrayList<KeysList>();
	
	private int port;	
	private boolean running=false;
	private Thread run,clientThread;
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	BufferedImage bImageFromConvert=null;
	String msg = null;
	String name;

	
	public static void main(String[] args) throws IOException{
		new Server();
	}
	public Server() throws IOException {
		try {
			port=8002;
			serverSocket = new ServerSocket(port);
			System.out.println("Server stared successfully at port no "+port);
			run=new Thread(this,"Server");
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			generator.init(KEY_SIZE);
			key = generator.generateKey();
			String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
			keyString = encodedKey;
			System.err.println("Secret Key AES : "+encodedKey);

			X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(PUBLIC_KEY_STRING));

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			publicKey = keyFactory.generatePublic(keySpecPublic);

			run.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		running=true;
		getclient();
	}
	private String encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	private byte[] decode(String data) {
		return Base64.getDecoder().decode(data);
	}

	public String encryptSecretkey(String message) throws Exception {

			byte[] messaeToByte = message.getBytes();
			Cipher cipher = Cipher.getInstance("RSA/EcB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE,publicKey);
			byte[] encryptedByte = cipher.doFinal(messaeToByte);
			return encode(encryptedByte);
	}
	
	private void getclient() {
		clientThread=new Thread("clientThread"){
			public void run(){
				 while (true) {
			            try {
			                socket = serverSocket.accept();
							OutputStream os = socket.getOutputStream();
							PrintWriter pw = new PrintWriter(os,true);
							keyStringSent = encryptSecretkey(keyString);
							System.out.println("AES KEY ENCRYPTED IS : "+keyStringSent);
							pw.println(keyStringSent);

							/*ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

							Message message = (Message) ois.readObject();

							System.out.println(message.getData());
							String Test = encode(message.getData());
							System.out.println(Test);*/

						} catch (Exception e) {
			                System.out.println("I/O error: " + e);
			            }

			            // new thread for a client
			          
			            new ClientThread(socket,key).start();
			        }
			}
		};clientThread.start();
	}
}


