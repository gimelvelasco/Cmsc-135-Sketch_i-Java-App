package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

public class ClientGUI extends JFrame{
	//put frame initialization here
	private JTextArea ChatBox = new JTextArea(8,45);
    private JScrollPane ChatHistory = new JScrollPane(ChatBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JTextArea UserText = new JTextArea(2,40);
    private JScrollPane UserHistory = new JScrollPane(UserText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JButton Send = new JButton("All");
    private JButton Start = new JButton("Connect");
    private ClientC ChatClient;
    private ReadThread ThreadRead = new ReadThread();
    private JLabel ServerLabel = new JLabel("Server Name");
    private JTextField Server = new JTextField(15);
    private JLabel UserLabel = new JLabel("User Name");
    private JTextField User = new JTextField(5);
    private String ServerName;
    private String UserName;
    private JButton clearBtn = new JButton("Clear");
    private JButton blackBtn = new JButton("Black");
    private JButton redBtn = new JButton("Red");
    private JButton blueBtn = new JButton("Blue");
    private DrawArea draw = new DrawArea();
    private InetAddress ClientAddress;
    
    private JLabel GroupLabel = new JLabel("Group");
    private JTextField Group  = new JTextField(1);
    private JButton Logout = new JButton("Logout");
    private String GroupName;
    private JButton Ready = new JButton("Join");
    private JButton Submit = new JButton("Guess");
    
	private DefaultCaret caret = (DefaultCaret)ChatBox.getCaret();
	
	private JLabel SubmitLabel = new JLabel("Word");
	private JTextField SubmitText = new JTextField(10);
	
	private JButton SendGroup = new JButton("Group");
	private JButton SendImage = new JButton("Send");
	
	private BufferedImage image; //this is the image that will be drawn on
	private Graphics2D g2; //creating the object "g2" with Graphics2D
	private int X2,Y2,X1,Y1; //this will serve as the previous and current mouse coordinates that will be used for drawing lines
	
	public ClientGUI() {
		try {
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setResizable(false);
		setTitle("Sketch-i Client");
		setSize(660,760);
		
		Container content = getContentPane();
		content.setLayout(new BorderLayout()); //set layout on content pane
		draw = new DrawArea(); //this creates a drawArea
		
		//panel for the chat buttons and interface
		JPanel controlC = new JPanel();
		controlC.setPreferredSize(new Dimension(560,275)); //this sets the size of the panel for the chat interface. since the drawArea have difficulties in adjusting the size, the panels for the controls were adjusted instead
		controlC.add(ServerLabel);
		controlC.add(Server);
		controlC.add(UserLabel);
		controlC.add(User);
		//blyat. create a horizontal line separator here somehow
		controlC.add(Start);
		controlC.add(Logout);
		Logout.setEnabled(false);

		controlC.add(new JLabel("User Event History"));
		ChatBox.setEditable(false); //this sets the ChatHistory as read-only
		controlC.add(ChatHistory);
		controlC.add(UserHistory);
		controlC.add(Send);
		controlC.add(SendGroup);
		controlC.add(GroupLabel);
		controlC.add(Group);
		controlC.add(Ready);
		controlC.add(SubmitLabel);
		controlC.add(SubmitText);
		controlC.add(Submit);
		Send.setEnabled(false);
		SendGroup.setEnabled(false);
		Group.setEditable(false);
		Ready.setEnabled(false);
		SubmitText.setEditable(false);
		Submit.setEnabled(false);
		
		//this automatically scrolls down the ChatBox for ease of use
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//panel for the drawing control buttons
		JPanel controlD = new JPanel(); //create controls for different colors and clear buttons
		controlD.setPreferredSize(new Dimension(560,40));
		controlD.add(clearBtn);
		controlD.add(blackBtn);
		controlD.add(redBtn);
		controlD.add(blueBtn);
		controlD.add(SendImage);
		SendImage.setEnabled(false);
		
		content.add(controlC,BorderLayout.PAGE_START);
		content.add(draw,BorderLayout.CENTER);
		content.add(controlD,BorderLayout.PAGE_END);
		
		Send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(ChatClient != null) {
					ChatClient.SendMessage(UserText.getText());
				}
			}
		});
		SendGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(ChatClient != null) {
					ChatClient.SendGroupMessage(UserText.getText());
				}
			}
		});
		Start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//this block starts the connection
				ChatClient = new ClientC();
				ChatClient.start();
			}
		});
		Ready.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(ChatClient != null) {
					ChatClient.SendMessage(Group.getText() + "3cmd0");
				}
				//this stores the groupname
				GroupName = Group.getText();
				//this block enables/disables the texts/buttons accordingly
				Group.setEditable(false);
				Ready.setEnabled(false);
				//Submit.setEnabled(true);
			}
		});
		Logout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(ChatClient != null) {
					ChatClient.SendMessage("Dasveedaneeya" + "1cmd0");
					Start.setEnabled(true);
				}
			}
		});
		Submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(ChatClient != null) {
					//ChatClient.SendMessage(" <Group " + GroupName + "> " + UserName + " has made a guess.\n");
					ChatClient.SendGuess(GroupName + SubmitText.getText() + "2cmd0");
				}
			}
		});
		SendImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(ChatClient != null) {
					ChatClient.SendMessageImage();
				}
			}
		});
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				draw.clear();
			}
		});
		blackBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				draw.black();
			}
		});
		redBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				draw.red();
			}
		});
		blueBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				draw.blue();
			}
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new ClientGUI();
	}
	
	public class ClientC extends Thread{
		private static final int PORT = 1234;
		private LinkedList Clients;
		private ByteBuffer ReadBuffer;
		private ByteBuffer WriteBuffer;
		private SocketChannel SChan;
		private Selector ReadSelector;
		private CharsetDecoder asciiDecoder;
		//private Socket imageSocket;
		
		public ClientC() {
			Clients = new LinkedList();
			ReadBuffer = ByteBuffer.allocateDirect(100000);
			WriteBuffer = ByteBuffer.allocateDirect(100000);
			asciiDecoder = Charset.forName("US-ASCII").newDecoder();
		}
		
		public void run() {
			try {
				ClientAddress = InetAddress.getLocalHost();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			//this block stores the names of the server, user and group(null)
			ServerName = Server.getText();
			UserName = User.getText();
			GroupName = Group.getText();
			
			Connect(ServerName);
			ThreadRead.start();
			while(true) {
				ReadMessage();
				try {
					Thread.sleep(100); //refresh time
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		
		public void Connect(String hostname) {
			try {
				ReadSelector = Selector.open();
				//need to fix the special characters in connecting to hostname
				InetAddress adr = InetAddress.getByName(hostname);
				SChan = SocketChannel.open(new InetSocketAddress(adr, PORT));
				SChan.configureBlocking(false);
				SChan.register(ReadSelector, SelectionKey.OP_READ, new StringBuffer());
				
				//this block enables/disables the texts/buttons accordingly
				Start.setEnabled(false);
				Logout.setEnabled(true);
				Send.setEnabled(true);
				Group.setEditable(true);
				Ready.setEnabled(true);
				Server.setEditable(false);
				User.setEditable(false);
				SendImage.setEnabled(false);
			} catch (Exception e) {
				// TODO: handle exception
				ChatBox.append(">> Attempt to connect to server failed.\n");
				e.printStackTrace();
			}
		}
		
		public void SendMessage(String msg) {
			if(GroupName.endsWith("A") || GroupName.endsWith("B")) {
				prepareBuffer(" <Group " + GroupName + "> " + UserName + ": " + msg);
			}
			else {
				prepareBuffer(" <Ungrouped> " + UserName + ": " + msg);
			}
			channelWrite(SChan);
		}
		
		public void SendGuess(String msg) {
			prepareBuffer(msg);
			channelWrite(SChan);
		}
		
		public void SendGroupMessage(String msg) {
			if(GroupName.endsWith("A")) {
				prepareBuffer(" <Group " + GroupName + "> " + UserName + ": " + msg + "Acmd0");
				channelWrite(SChan);
			}
			else if(GroupName.endsWith("B")){
				prepareBuffer(" <Group " + GroupName + "> " + UserName + ": " + msg + "Bcmd0");
				channelWrite(SChan);
			}
			else {
				ChatBox.append("You don't have a group yet blyat.\n");
			}
		}
		
		public void prepareBuffer(String msg) {
			System.out.println("bytes sent: " + msg.getBytes().length);
			WriteBuffer.clear();
			WriteBuffer.put(msg.getBytes());
			WriteBuffer.putChar('\n');
			WriteBuffer.flip();
		}
		
		public void channelWrite(SocketChannel client) {
			long num = 0;
			long len = WriteBuffer.remaining();
			while(num != len) {
				try {
					num += SChan.write(WriteBuffer);
					Thread.sleep(5);
				} catch (IOException e) {
					// TODO: handle exception
					//e.printStackTrace();
					ChatBox.append("Server can't be reached.\n");
					break;
				} catch (InterruptedException e) {
					//e.printStackTrace();
					ChatBox.append("Server can't be reached.\n");
					break;
				}
			}
			WriteBuffer.rewind();
		}
		
		public void ReadMessage() {
			try {
				ReadSelector.selectNow();
				Set readyKeys = ReadSelector.selectedKeys();
				Iterator i = readyKeys.iterator();
				
				while(i.hasNext()) {
					SelectionKey key = (SelectionKey) i.next();
					i.remove();
					SocketChannel channel = (SocketChannel) key.channel();
					ReadBuffer.clear();
					
					long nbytes = channel.read(ReadBuffer);
					if(nbytes == -1) {
						ChatBox.append(">> Successful Log Out.\n");
						channel.close();
					}else {
						StringBuffer sb = (StringBuffer) key.attachment();
						ReadBuffer.flip();
						String str = asciiDecoder.decode(ReadBuffer).toString();
						sb.append(str);
						ReadBuffer.clear();
						
						String line = sb.toString();
						if((line.indexOf("\n") != -1) || (line.indexOf("\r") != -1)) {
							line = line.trim();
							if(line.endsWith("cmd1")) {
								if(line.endsWith("4cmd1")) {//this will be the client that would draw the word
									line = line.substring(0,line.length()-5); //this removes the "4cmd1" string at the end of the line
									SubmitText.setText(line);
									SubmitText.setEditable(false);
									Submit.setEnabled(false);
									SendImage.setEnabled(true);
								}
								else if(line.endsWith("5cmd1")) {//this will be for the clients that would guess the word
									SubmitText.setEditable(true);
									Submit.setEnabled(true);
									SendImage.setEnabled(false);
								}
								
								if(line.endsWith("6cmd1")) {//this will be for everyone when the game has finished
									//enable/disable the respective buttons and textfields
									//include the announcement of the group that won in here blyat
									line = line.substring(0,line.length()-5); //this removes the "6cmd1" at the end of the string gg
									ChatBox.append(line + "\n");
									SubmitText.setText("Game Finished");
									SubmitText.setEditable(false);
									Submit.setEnabled(false);
									SendImage.setEnabled(false);
								}
								
								if(line.endsWith("7cmd1")) {//this will be a notice and setup when a client has joined a group
									//enable/disable the respective buttons and textfields
									line = line.substring(0,line.length()-5);
									ChatBox.append(line + "\n");
									SendGroup.setEnabled(true);
									SendImage.setEnabled(false);
								}
								
								//#savestate. edit this for converting the encoded string to images
								if(line.endsWith("Acmd1") || line.endsWith("Bcmd1")) {
									line = line.substring(0,line.length()-5);
									System.out.println(line);
									//string to bytes
									byte[] imageDecoded = Base64.getDecoder().decode(line);
									//bytes to image
									ByteArrayInputStream inputStream = new ByteArrayInputStream(imageDecoded);
									image = ImageIO.read(inputStream);
									//g2.drawImage(image,0,0,null);
									
									g2 = (Graphics2D) image.getGraphics();
									g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //enable anti-aliasing
									g2.setPaint(Color.black);
									repaint();
								}
							}
							else {
								ChatBox.append(line + "\n"); //else this is a normal message
							}
							sb.delete(0, sb.length());
						}
					}
				}
			} catch (IOException e) {
				// TODO: handle exception
				//e.printStackTrace();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		
		public void SendMessageImage() {
			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ImageIO.write(image, "png", outputStream);
				String encodedImage = Base64.getEncoder().encodeToString(outputStream.toByteArray());
				
				if(GroupName.endsWith("A")) {
					prepareBuffer(encodedImage + "Acmd1");
					channelWrite(SChan);
				}
				else if(GroupName.endsWith("B")){
					prepareBuffer(encodedImage + "Bcmd1");
					channelWrite(SChan);
				}
				else {
					ChatBox.append("The Game hasnt started blyat.\n");
				}
			} catch (IOException ex) {
			    // TODO Auto-generated catch block
			    ex.printStackTrace();
			}
		}
	}
	
	public class ReadThread extends Thread{
		public void run() {
			ChatClient.ReadMessage();
		}
	}
	
	public class DrawArea extends JComponent { //blyat. if we were to find a way so that the drawArea would be centralized on the server, i guess it would become easier. i guess
		public DrawArea() {
			setDoubleBuffered(false);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {//this block is brought out using: right-click > Source > Override/Implement Methods > MousePressed
					X1 = e.getX(); //this saves the coordinates of the mouse pointer when pressed
					Y1 = e.getY();
				}
			});
			
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {//this block is brought out using: right-click > Source > Override/Implement Methods > MouseDragged
					X2 = e.getX(); //this saves the coordinates of the mouse pointer when dragged
					Y2 = e.getY();
					if(g2 != null) {
						g2.drawLine(X1,Y1,X2,Y2); //draw a line from (X1,Y1) to (X2,Y2)
						repaint(); //refresh DrawArea to repaint
						X1 = X2; //update the coordinates of the mouse while being dragged
						Y1 = Y2;
					}
				}
			});
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			if (image == null) { //if image is null,
				//image = createImage(getSize().width,getSize().height); //create image
				image = new BufferedImage(getSize().width,getSize().height, BufferedImage.TYPE_INT_RGB);
				g2 = (Graphics2D) image.getGraphics();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //enable anti-aliasing
				clear(); //clear DrawArea
			}
			g.drawImage(image,0,0,null);
		}
		
		public void clear() { //this function clears the DrawArea by coloring the entire image in white
			g2.setPaint(Color.white);
			g2.fillRect(0,0,getSize().width,getSize().height);
			g2.setPaint(Color.black);
			repaint();
		}
		
		public void red() {
			g2.setPaint(Color.red);
		}
		
		public void black() {
			g2.setPaint(Color.black);
		}

		public void blue() {
			g2.setPaint(Color.blue);
		}
	}
}