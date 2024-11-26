

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JList;
import java.awt.Color;

public class Client extends JFrame {

	private JPanel contentPane;
	private JTextField txtMessage;
	private static  String name="";

	private String ip;
	private int port;
	private String secretKey;
	private String secretKeyEncrypted;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private String PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHXKE1uS5Tt4sgS9ZDe4Zu7+puq8Kadlrly8LWKtIZq6joe4sKbFPxRRMsvhUTmpHOyFPaM6bGof6LwMxtArN383B2341zA3sa7pEEfQ1L5rYZQbLmGBS84kU8lxyIN2v+PeldcKjpEZZm0MX5rsZPp/icnGARIjk2f5i4SjY9dQIDAQAB";
	private String PRIVATE_KEY_STRING = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIdcoTW5LlO3iyBL1kN7hm7v6m6rwpp2WuXLwtYq0hmrqOh7iwpsU/FFEyy+FROakc7IU9ozpsah/ovAzG0Cs3fzcHbfjXMDexrukQR9DUvmthlBsuYYFLziRTyXHIg3a/496V1wqOkRlmbQxfmuxk+n+JycYBEiOTZ/mLhKNj11AgMBAAECgYAMOoMNCzpMyw0UXgvws40bVcZ6nwu8NpFs2lgzG7hrvauxuc9/ocLnmtNQb6lDYlH/01TRjQI14AnSu37JIYnvwTVKVdNcr6/LCrGmC50CjfilxKz1ms55x1AgQsOjQ3guH3b6mqiERfw3dQiZljQkhzD4tvvHEpwhePGcD9tDlQJBAPxQtyeGrx7vyFJ6bJC/rNGgn/+0ML4vAftfhzreOOtCGWmbz7IHh3FBOZynGQb4ap/96QYtJ2DC15CGLjPMLccCQQCJVq6XXA2n+MfYA26/kxTq7z6WAt1QYaCaxDVGNphBDtpEuDZbeEXWK3vcjzhKtsyltdodTsPhBs+eqXFaISrjAkARJNbdC6y4eGhvGjxoNW3jrHtSn4a0VXNYBiylFlZdvdcGMv0QxqwwSNNGTqBHtfdguZrsvYKwc2y8ODHJeTUfAkEAhnYbkY1KwX3NCyyxLVcvT6TUvcIoNB6hQ2V+UzP4dRm5kTZZ4cwrc7g3GIYQMvYt/r/AwJjxJkuo0kpK9FcsyQJAKJNZXrZurfM+pPu6CqXKR3Kyhcuyphjpmgntfl15g9p+y+T9u4fMIliz4tmq+7iNqkjVXSGOjhedaMDHKMZPGQ==";

	private JTextPane textArea;
	private Thread recieve;
	public List<OnlineUsers> users=new ArrayList<OnlineUsers>();
	private JPanel panel;
	private JList<String> online_users;
	public DefaultListModel<String> model;
	private Socket socket = null;
	String msg = null;
	String imgname = "";
	String imgtype="";
	BufferedImage buffimg;
	ObjectInputStream is=null;
	int id;
	boolean f = true;
	public Client(String name,String ip,int port) throws Exception {
		setTitle(name);
		this.name=name;
		this.ip=ip;
		this.port=port;

		boolean c = connection();
		if(!c){
			consol("Connection failed");
			System.out.println("Connection failed");
		}



		Create();
		recieveThread();
	}

	public String decryptSecretKey(String encryptedMessage) throws Exception {
		byte[] encryptedBytes = decode(encryptedMessage);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE,privateKey);
		byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
		return new String(decryptedMessage,"UTF8");
	}

	private String encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	private byte[] decode(String data) {
		return Base64.getDecoder().decode(data);
	}

	private boolean connection(){
		try {
			socket = new Socket(ip, port);
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader bf = new BufferedReader(isr);

			PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(PRIVATE_KEY_STRING));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			privateKey = keyFactory.generatePrivate(keySpecPrivate);

			secretKeyEncrypted = bf.readLine();
			secretKey = decryptSecretKey(secretKeyEncrypted);
			id=UniqueID.getid();
			System.out.println(name+" Connected | id :"+id);
			System.out.println("Clé publique génerer est :" +PUBLIC_KEY_STRING);
			System.out.println("AES Encrypted Key Is : "+secretKeyEncrypted+" \n");
			System.out.println("AES Decrypted Key Is : "+secretKey+" \n");

			//System.out.println("Private Key : " +encode(privateKey.getEncoded()));

			ObjectOutputStream ous=new ObjectOutputStream(socket.getOutputStream());
			Packet pckt=new Packet(id,name,false);
			ous.writeObject(pckt);

		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
		return true;

	}

	public String encryptMsg(String msg) throws Exception{
		String key = secretKey;
		System.out.println(secretKey);
        SecretKeySpec key_AES = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key_AES);
        byte[] encrypted = cipher.doFinal(msg.getBytes());
        StringBuilder strb = new StringBuilder();
        for (byte b: encrypted) {
            strb.append((char)b);
        }
        String emsg = strb.toString();
		return emsg;
	}
	public String decryptMsg(String msg) throws Exception {
		String key = secretKey;
        SecretKeySpec aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cphr = Cipher.getInstance("AES");
		byte[] bb = new byte[msg.length()];
        for (int i=0; i<msg.length(); i++) {
            bb[i] = (byte) msg.charAt(i);
        }
        cphr.init(Cipher.DECRYPT_MODE, aesKey);
        String dmsg = new String(cphr.doFinal(bb));
	    return dmsg;
	}

	private void recieveThread(){

		recieve=new Thread("Recieve"){
			public void run(){

				while(true){


					is=null;
					try {
						is = new ObjectInputStream(socket.getInputStream());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Packet obj = null;
				    try {
				    	obj = (Packet) is.readObject();
					} catch ( Exception e) {

					}
				    if(obj.samename){
				    	dispose();
				    	Login frame = new Login();
						frame.setVisible(true);
				    }
				    //new user..
				    if(obj.getImg()==null && obj.getMsg()==null && obj.getid()!=-1 && obj.getname()!=null && obj.remove == false){
				    	users.add(new OnlineUsers(obj.getname(),obj.getid()));
				    	model.addElement(obj.getname());
				    }
				    //remove user
				    if(obj.getImg()==null && obj.getMsg()==null && obj.getid()!=-1 && obj.getname()!=null && obj.remove == true){
				    	for(int i=0;i<users.size();i++){
				    		if(users.get(i).getId() == obj.getid()){
				    			users.remove(i);
				    		}
						}
				    	for(int i=0;i<model.size();i++){
				    		if(model.get(i).equals(obj.getname())){
				    			model.remove(i);
				    		}
				    	}
				    }
				    //recieve message

					if(obj.getMsg()!=null){

						String rec = "";
						String rmsg = obj.getMsg();
						int i=0;
						for(i=0;rmsg.charAt(i)!=':';i++){
							rec+=rmsg.charAt(i);
						}
						rmsg = rmsg.substring(i+2);
						String dmsg = null;
						try {
							dmsg = decryptMsg(rmsg);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String encodedKey = Base64.getEncoder().encodeToString(obj.getKey().getEncoded());
						System.out.println(encodedKey);
						consol1(rec+": "+rmsg);
				    	consol1(rec+": "+dmsg);

					}

				}

			}
		};
		recieve.start();
	}

	private void Create() throws Exception{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(683,500);
		setLocationRelativeTo(null);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		panel = new JPanel();
		panel.setBounds(496, 54, 169, 366);
		contentPane.add(panel);
		panel.setLayout(null);
		model = new DefaultListModel<>();
		online_users = new JList<String>();
		online_users.setForeground(Color.BLACK);
		online_users.setFont(new Font("Verdana", Font.PLAIN, 14));
		online_users.setBounds(0, 0, 169, 366);
		panel.add(online_users);
		online_users.setModel(model);
		txtMessage = new JTextField();
		txtMessage.setBounds(10, 431, 308, 29);

		txtMessage.addKeyListener(new KeyAdapter() {
			//sending the message
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					if(online_users!=null && online_users.getSelectedValue()!=null){
						msg = txtMessage.getText();
						String emsg = null;

						try {
							emsg = encryptMsg(msg);
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}

						if(!msg.equals("")){
							ObjectOutputStream out=null;
							try {
								out = new ObjectOutputStream(socket.getOutputStream());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							Packet obj = new Packet(name+" : "+emsg+":"+online_users.getSelectedValue());
							//Packet obj = new Packet(name+" envoyer le Message : "+emsg+"| a "+online_users.getSelectedValue());
							try {
								out.writeObject (obj);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							consol(msg);
							txtMessage.setText("");
						}
					}
				}
			}

		});
		contentPane.setLayout(null);
		contentPane.add(txtMessage);
		txtMessage.setColumns(10);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.setBounds(328, 434, 61, 23);

		//sending the message
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(online_users!=null && online_users.getSelectedValue()!=null){
					msg = txtMessage.getText();
					String emsg = null;

					try {
						emsg = encryptMsg(msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(!msg.equals("")){
					ObjectOutputStream out=null;
					try {
						out = new ObjectOutputStream(socket.getOutputStream());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					Packet obj = new Packet(name+" : "+emsg+":"+online_users.getSelectedValue());
					try {
						out.writeObject (obj);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					txtMessage.setText("");
					consol(msg);
				}
			}
			}
		});
		contentPane.add(btnNewButton);
		textArea = new JTextPane();
		textArea.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 13));
		textArea.setBackground(Color.WHITE);
		textArea.setForeground(Color.BLACK);
		textArea.setEditable(false);

		JScrollPane scroll=new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(10, 11, 474, 409);

		contentPane.add(scroll);


		//closing the window
		addWindowListener(new WindowAdapter() {
			@SuppressWarnings("deprecation")
			public void windowClosing(WindowEvent e){
			try {


					ObjectOutputStream out=null;
					try {
						out = new ObjectOutputStream(socket.getOutputStream());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					Packet obj = new Packet("EXIT");
					try {
						out.writeObject (obj);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					recieve.stop();
					socket.close();


				} catch (IOException e1) {
					// TODO Auto-generated catch block
						e1.printStackTrace();
			}

			}
		});

		JLabel lblOnlineUsers = new JLabel("Online Users");
		lblOnlineUsers.setBounds(530, 4, 104, 37);
		lblOnlineUsers.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblOnlineUsers);
		txtMessage.requestFocusInWindow();
	}

	public static String getname() {
		return name;
	}

	//printing the message
	public void consol(String massege){
		textArea.setText(textArea.getText()+name+" : "+massege+"\n");
	}
	public void consol1(String massege){
		textArea.setText(textArea.getText()+massege+"\n");
	}

	public static BufferedImage scaleImage(BufferedImage image, int imageType,
            int newWidth, int newHeight) {
			// Make sure the aspect ratio is maintained, so the image is not distorted
			double thumbRatio = (double) newWidth / (double) newHeight;
			int imageWidth = image.getWidth(null);
			int imageHeight = image.getHeight(null);
			double aspectRatio = (double) imageWidth / (double) imageHeight;

			if (thumbRatio < aspectRatio) {
				newHeight = (int) (newWidth / aspectRatio);
			} else {
				newWidth = (int) (newHeight * aspectRatio);
			}

			// Draw the scaled image
			BufferedImage newImage = new BufferedImage(newWidth, newHeight,imageType);
			Graphics2D graphics2D = newImage.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);

			return newImage;
}
}
