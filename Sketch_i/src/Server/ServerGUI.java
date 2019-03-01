package Server;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ServerGUI extends JFrame{
	//basic interface
	private JTextArea ChatBox = new JTextArea(8,45);
	private JScrollPane ChatHistory = new JScrollPane(ChatBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JTextArea UserText = new JTextArea(2,40);
	private JScrollPane UserHistory = new JScrollPane(UserText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JButton Send = new JButton("Send");
	private JButton Start =  new JButton("Start Server");
	private server ChatServer;
	private InetAddress ServerAddress;
	//update. for setting the word to guess and starting the game
	private JTextField SetText = new JTextField(10);
	private JButton Set = new JButton("Start Game");
	//this string will store the word that is to be guessed by the groups
	private String guessguess = "Guess Guess"; 
	//for automatic scrolldown when a message is received //it doesnt work sometimes blyat
	private DefaultCaret scrolldown = (DefaultCaret)ChatBox.getCaret();
	//will be used for the randomization of the word
	private String[] wordpool = {"airplane",
			"alligator",
			"angel",
			"ant",
			"apple",
			"arm",
			"baby",
			"backpack",
			"ball",
			"balloon",
			"banana",
			"baseball",
			"basketball",
			"bat",
			"bathroom",
			"beach",
			"beak",
			"bear",
			"bed",
			"bee",
			"bell",
			"bench",
			"bike",
			"bird",
			"blanket",
			"blocks",
			"boat",
			"bone",
			"book",
			"bowl",
			"box",
			"boy",
			"bracelet",
			"branch",
			"bread",
			"bridge",
			"broom",
			"bug",
			"burger",
			"bus",
			"butterfly",
			"button",
			"camera",
			"candle",
			"candy",
			"car",
			"carrot",
			"cat",
			"caterpillar",
			"chair",
			"cheese",
			"cherry",
			"chicken",
			"chimney",
			"circle",
			"clock",
			"cloud",
			"coat",
			"coin",
			"comb",
			"computer",
			"cookie",
			"corn",
			"cow",
			"crab",
			"crayon",
			"cup",
			"cupcake",
			"diamond",
			"dinosaur",
			"dog",
			"doll",
			"door",
			"dragon",
			"drum",
			"duck",
			"ear",
			"earth",
			"egg",
			"elephant",
			"eye",
			"face",
			"family",
			"feather",
			"feet",
			"finger",
			"fire",
			"fish",
			"flag",
			"flower",
			"fly",
			"football",
			"fork",
			"frog",
			"ghost",
			"giraffe",
			"girl",
			"glasses",
			"grapes",
			"grass",
			"hair",
			"hand",
			"hat",
			"head",
			"heart",
			"helicopter",
			"hippopotamus",
			"hook",
			"horse",
			"house",
			"island",
			"jacket",
			"jail",
			"jar",
			"jellyfish",
			"key",
			"king",
			"kite",
			"kitten",
			"knee",
			"ladybug",
			"lamp",
			"leaf",
			"leg",
			"lemon",
			"light",
			"line",
			"lion",
			"lips",
			"lizard",
			"lollipop",
			"love",
			"man",
			"milk",
			"monkey",
			"monster",
			"moon",
			"motorcycle",
			"mouse",
			"mouth",
			"music",
			"nail",
			"neck",
			"night",
			"nose",
			"ocean",
			"octopus",
			"orange",
			"oval",
			"owl",
			"pants",
			"pen",
			"pencil",
			"pie",
			"pig",
			"pillow",
			"pizza",
			"plant",
			"pool",
			"rabbit",
			"rain",
			"rainbow",
			"ring",
			"river",
			"robot",
			"rock",
			"rocket",
			"sea",
			"seashell",
			"sheep",
			"ship",
			"shirt",
			"shoe",
			"skateboard",
			"slide",
			"smile",
			"snail",
			"snake",
			"snowflake",
			"snowman",
			"socks",
			"spider",
			"spoon",
			"square",
			"stairs",
			"star",
			"starfish",
			"suitcase",
			"sun",
			"sunglasses",
			"swing",
			"table",
			"tail",
			"train",
			"tree",
			"triangle",
			"truck",
			"turtle",
			"wallet",
			"water",
			"web",
			"whale",
			"wheel",
			"window",
			"woman",
			"worm",
			"xylophone",
			"xray",
			"yoyo",
			"zebra"};
	private Random randomgenerator = new Random();
	private int wordindex = randomgenerator.nextInt(wordpool.length);
	//will be used for the random selection of one client per group
	private Random randomAclientnum = new Random();
	private int clientAindex;
	private Random randomBclientnum = new Random();
	private int clientBindex;
	
	private BufferedImage buffImage;
			
	public ServerGUI() {
		try {
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setResizable(false);
		setTitle("Sketch-i Server");
		setSize(600,350);
		Container cp = getContentPane();
		cp.setLayout(new FlowLayout());
		cp.add(new JLabel("Server Event History"));
		ChatBox.setEditable(false); //this sets the ChatHistory as read-only
		cp.add(ChatHistory);
		cp.add(UserHistory);
		cp.add(Send);
		Send.setEnabled(false);
		
		//this automatically scrolls down the ChatBox for ease of use
		scrolldown.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JPanel startPanel = new JPanel();
		startPanel.add(Start);
		startPanel.setPreferredSize(new Dimension(560,40));
		cp.add(startPanel);
		
		cp.add(new JLabel("Word to guess:"));
		cp.add(SetText);
		cp.add(Set);
		SetText.setEditable(false);
		Set.setEnabled(false);
		
		Send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String sm = "[/" + ServerAddress.getHostAddress() + "] <Server> " + ServerAddress.getHostName() + ": " + UserText.getText() + "\n";
				ChatServer.SendMessage(sm);
				ChatBox.append(sm);
			}
		});
		
		Start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ChatServer = new server();
				ChatServer.start();
			}
		});
		
		Set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(ChatServer.playersAreReady() == 1) {
					guessguess = wordpool[wordindex];
					SetText.setText(guessguess);
					
					//create a single function for this block later after a successful testing blyat. if necessary, that is
					ChatServer.assignRandomClientA(); //this sends only the word to guess to a random client
					ChatServer.assignRandomClientB();
					try {
						Thread.sleep(1000); //this is for giving time for the buffer to clear up for the next SendMessage
					} catch (InterruptedException ee) {
						// TODO: handle exception
					}
					ChatServer.SendMessage("The Server has set the word to guess!\nThe Game has started!\n");
					
					Set.setEnabled(false);
				}
				else {
					ChatBox.append("Not enough Players are Ready blyat.\n");
				}
			}
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new ServerGUI();
	}
	
	public class server extends Thread{
		private static final int PORT = 1234;
		private LinkedList Clients;
		private LinkedList ClientsGroupA;
		private LinkedList ClientsGroupB;
		private ByteBuffer ReadBuffer;
		private ByteBuffer WriteBuffer;
		public ServerSocketChannel SSChan;
		private Selector ReaderSelector;
		private CharsetDecoder asciiDecoder;
		//private ServerSocket serverSocket;
	    //private Socket imageSocket;
		
		public server() {
			Clients = new LinkedList();
			ClientsGroupA = new LinkedList();
			ClientsGroupB = new LinkedList();
			ReadBuffer = ByteBuffer.allocateDirect(100000);
			WriteBuffer = ByteBuffer.allocateDirect(100000);
			asciiDecoder = Charset.forName("US-ASCII").newDecoder();
		}

		public void initServer() {
			try {
				SSChan = ServerSocketChannel.open();
				SSChan.configureBlocking(false);
				ServerAddress = InetAddress.getLocalHost();
				SSChan.socket().bind(new InetSocketAddress(ServerAddress, PORT));
				ReaderSelector = Selector.open();
				ChatBox.setText("Sketch-i server has now started.\nServer Name: " + ServerAddress.getHostName() + "\nServer Address: " + ServerAddress.getHostAddress() + "\n\n");

				//this enables the buttons as the server starts
				Send.setEnabled(true);
				Start.setEnabled(false);
				Set.setEnabled(true);
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		public void run() {
			initServer();
			while(true) {
				acceptNewConnection();
				ReadMessage();
				try {
					Thread.sleep(30); //for every 100ms, it checks for a new connection or message from a client
				} catch (InterruptedException e) {
					// TODO: handle exception
				}
			}
		}

		public void acceptNewConnection() {
			// TODO Auto-generated method stub
			SocketChannel newClient;
			try {
				while((newClient = SSChan.accept()) != null) {
					ChatServer.addClient(newClient);
					String ln = "Login from [" + newClient.socket().getInetAddress() + "]\n";
					//SendMessageToAllExceptOne(newClient, ln); //no need to reflect to clients all the logins. plus, its very unorganized and confusing when one client joins a server midway
					ChatBox.append(ln);
					SendWelcome(newClient, "Welcome to Sketch-i!\nServer Name: " + ServerAddress.getHostName() + "\nPlease select \"A\" or \"B\" to join a group.\n");
				}
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		public void addClient(SocketChannel newClient) {
			// TODO Auto-generated method stub
			Clients.add(newClient);
			try {
				newClient.configureBlocking(false);
				newClient.register(ReaderSelector, SelectionKey.OP_READ, new StringBuffer());
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		private void SendWelcome(SocketChannel newClient, String msg) {
			// TODO Auto-generated method stub
			prepareBuffer(msg);
			channelWrite(newClient);
		}

		public void SendMessage(String msg) {//this is a setup for sending a message one at a time for every client
			// TODO Auto-generated method stub
			if(Clients.size() > 0) {
				int i;
				for(i=0;i<Clients.size();i++) {
					SocketChannel client = (SocketChannel)Clients.get(i);
					SendMessage(client,msg);
				}
			}
			else {
				ChatBox.append(">> There are no Clients.\n");
			}
		}

		public void SendMessage(SocketChannel client, String msg) {//this sends a message to a specific "client"
			// TODO Auto-generated method stub
			prepareBuffer(msg);
			channelWrite(client);
		}

		public void SendMessageToAllExceptOne(SocketChannel client, String msg) {
			// TODO Auto-generated method stub
			prepareBuffer(msg);
			Iterator i = Clients.iterator();
			while (i.hasNext()) {
				SocketChannel channel = (SocketChannel)i.next();
				if (channel != client) {
					channelWrite(channel);
				}
			}
		}
		
		public void SendMessageToGroupAExceptOne(SocketChannel client, String msg) {
			// TODO Auto-generated method stub
			prepareBuffer(msg);
			Iterator i = ClientsGroupA.iterator();
			while (i.hasNext()) {
				SocketChannel channel = (SocketChannel)i.next();
				if (channel != client) {
					channelWrite(channel);
				}
			}
		}
		
		public void SendMessageToGroupBExceptOne(SocketChannel client, String msg) {
			// TODO Auto-generated method stub
			prepareBuffer(msg);
			Iterator i = ClientsGroupB.iterator();
			while (i.hasNext()) {
				SocketChannel channel = (SocketChannel)i.next();
				if (channel != client) {
					channelWrite(channel);
				}
			}
		}
		
		public void channelWrite(SocketChannel client) {
			// TODO Auto-generated method stub
			long num = 0;
			long len = WriteBuffer.remaining();
			while(num != len) {
				try {
					num += client.write(WriteBuffer);
					Thread.sleep(5);
				} catch (IOException e) {
					// TODO: handle exception
					ChatBox.append("A Client can't be reached.\n");
					break;
				} catch (InterruptedException e) {
					// TODO: handle exception
					ChatBox.append("A Client can't be reached.\n");
					break;
				}
			}
			WriteBuffer.rewind();
		}

		public void prepareBuffer(String msg) {
			// TODO Auto-generated method stub
			WriteBuffer.clear();
			WriteBuffer.put(msg.getBytes());
			WriteBuffer.putChar('\n');
			WriteBuffer.flip();
		}

		public void assignRandomClientA() {
			clientAindex = randomAclientnum.nextInt(ClientsGroupA.size());
			SocketChannel clientA = (SocketChannel)ClientsGroupA.get(clientAindex);
			SendMessage(clientA, guessguess + "4cmd1"); //this assigns a random client as the one who will draw
			SendMessageToGroupAExceptOne(clientA, "5cmd1"); //this assigns the rest of the clients to be able to guess			
		}
		
		public void assignRandomClientB() {
			clientBindex = randomBclientnum.nextInt(ClientsGroupB.size());
			SocketChannel clientB = (SocketChannel)ClientsGroupB.get(clientBindex);
			SendMessage(clientB, guessguess + "4cmd1"); //this assigns a random client as the one who will draw
			SendMessageToGroupBExceptOne(clientB, "5cmd1"); //this assigns the rest of the clients to be able to guess	
		}
		
		public int playersAreReady() {
			int ready = 0;
			if(ClientsGroupA.size() >= 2 && ClientsGroupB.size() >= 2) {
				ready = 1;
			}
			return ready;
		}
		
		public void ReadMessage() {
			// TODO Auto-generated method stub
			try {
				ReaderSelector.selectNow();
				Set readKeys = ReaderSelector.selectedKeys();
				Iterator i = readKeys.iterator();
				while (i.hasNext()) {
					SelectionKey key = (SelectionKey)i.next();
					i.remove();
					
					SocketChannel client = (SocketChannel)key.channel();
					ReadBuffer.clear();
					
					long num = client.read(ReadBuffer);
					
					if(num == -1) {
						client.close();
						Clients.remove(client);
						SendMessageToAllExceptOne(client, ">> " + client.socket().getInetAddress() + " logged out.");
					}
					else {
						StringBuffer str = (StringBuffer)key.attachment();
						ReadBuffer.flip();
						String data = asciiDecoder.decode(ReadBuffer).toString();
						ReadBuffer.clear();
						
						str.append(data);
						
						String line = str.toString();
						
						if ((line.indexOf("\n") != -1) || (line.indexOf("\r") != -1)) {
							line = line.trim();
							if(line.endsWith("cmd0")) {
								if(line.endsWith("Dasveedaneeya" + "1cmd0")) {//this checks for and notifies everyone of a logout event
									client.close();
									Clients.remove(client);
									ChatBox.append("Logout from [" + client.socket().getInetAddress() + "]\n");
									SendMessageToAllExceptOne(client, client.socket().getInetAddress() + " logged out.\n");
								}
								else if(line.endsWith("2cmd0")) {//this handles an event when a client tries to guess for the word
									//notify everyone that a guess was made. blyat
									String grp = line.substring(0,1);
									String atpmt = line.substring(1,line.length()-5);
									atpmt = atpmt.toLowerCase();
									if(atpmt.endsWith(guessguess)) {
										//the command "6cmd1" would trigger an end game to the clients
										String gg = "The word has been guessed!\nThe word to guess was: \"" + guessguess + "\"\nGroup " + grp + " Wins!\n" + "6cmd1";
										SendMessage(gg);
										gg = gg.substring(0,gg.length()-5); //this removes the "6cmd1" at the end of the string gg
										ChatBox.append(gg);
										Start.setEnabled(true);
									}
									else {
										//try again
										String gg = "Try Again Blyat.\n";
										SendMessage(client, gg);
										//ChatBox.append(gg);
									}
									str.delete(0, str.length());
								}
								else if(line.endsWith("3cmd0")) {//this handles an event when a client tries to join a group
									if(line.endsWith("A" + "3cmd0")) {
										ClientsGroupA.add(client);
										String gg = "[" + client.socket().getInetAddress() + "] has joined Group A\n";
										String ga = "You've successfully joined Group A\n" + "7cmd1"; //blyat. put the code here
										SendMessage(client,ga);
										ChatBox.append(gg);
									}
									else if(line.endsWith("B" + "3cmd0")) {
										ClientsGroupB.add(client);
										String gg = "[" + client.socket().getInetAddress() + "] has joined Group B\n";
										String ga = "You've successfully joined Group B\n" + "7cmd1"; //blyat. put the code here
										SendMessage(client,ga);
										ChatBox.append(gg);
									}
									else {
										//notify failure to join a group
										String gg = "[" + client.socket().getInetAddress() + "] has failed to join a group.\n";
										SendMessage(gg);
										ChatBox.append(gg);
									}
									str.delete(0, str.length());
								}
								else if(line.endsWith("Acmd0")) {
									if(ClientsGroupA.size() > 0) {
										line = line.substring(0,line.length()-5);
										line = "[" + client.socket().getInetAddress() + "] " + line + "\n"; //line = "UserName: message"
										ChatBox.append(line);
										int j;
										for(j=0;j<ClientsGroupA.size();j++) {
											SocketChannel clientA = (SocketChannel)ClientsGroupA.get(j);
											if (clientA != client) {
												SendMessage(clientA,line);
											}
										}
									}
									else {
										ChatBox.append(">> There are no Clients in Group A.\n");
									}
									str.delete(0, str.length());
								}
								else if(line.endsWith("Bcmd0")) {
									if(ClientsGroupB.size() > 0) {
										line = line.substring(0,line.length()-5);
										line = "[" + client.socket().getInetAddress() + "] " + line + "\n"; //line = "UserName: message"
										ChatBox.append(line);
										int j;
										for(j=0;j<ClientsGroupB.size();j++) {
											SocketChannel clientB = (SocketChannel)ClientsGroupB.get(j);
											SendMessage(clientB,line);
										}
									}
									else {
										ChatBox.append(">> There are no Clients in Group B.\n");
									}
									str.delete(0, str.length());
								}
							}
							else if(line.endsWith("cmd1")) { //this will pass the encodedImage string to its proper group
								//#savestate. pass encodedImage (aka: line) to its corresponding group
								if(line.endsWith("Acmd1")) {
									if(ClientsGroupA.size() > 0) {
										int j;
										for(j=0;j<ClientsGroupA.size();j++) {
											SocketChannel clientA = (SocketChannel)ClientsGroupA.get(j);
											//SendMessage(clientA,"I got a drawing for you Group A blyat!");
											SendMessage(clientA,line);
										}
										ChatBox.append("You got a drawing for Group A blyat!\n");
										System.out.println(line);
									}
									else {
										ChatBox.append(">> There are no Clients in Group A.\n");
									}
									str.delete(0, str.length());
								}
								else if(line.endsWith("Bcmd1")) {
									if(ClientsGroupB.size() > 0) {
										int j;
										for(j=0;j<ClientsGroupB.size();j++) {
											SocketChannel clientB = (SocketChannel)ClientsGroupB.get(j);
											//SendMessage(clientB,"I got a drawing for you Group B blyat!");
											SendMessage(clientB,line);
										}
										ChatBox.append("You got a drawing for Group B blyat!\n");
										System.out.println(line);
									}
									else {
										ChatBox.append(">> There are no Clients in Group B.\n");
									}
									str.delete(0, str.length());
								}
							}
							else {
								String gg = "[" + client.socket().getInetAddress() + "] " + line + "\n"; //line = "UserName: message"
								ChatBox.append(gg); 
								SendMessage(gg); //this would make the sender see its own message being successfully sent
								str.delete(0, str.length());
							}
						}
					}
				}
			} catch (IOException e) {
				// TODO: handle exception
				//e.printStackTrace();
			}
		}
	}
}
