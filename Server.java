import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.*;
import java.io.*;

public class Server extends JFrame
{
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    //    Declare Components
    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    //    contructor
    public Server()
    {
        try
        {
            server = new ServerSocket(7779);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting...");
            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
//            startWriting();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void handleEvents()
    {
        messageInput.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {

            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                System.out.println("Key released "+ e.getKeyCode());
                if(e.getKeyCode() == 10)
                {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    private void createGUI()
    {
        this.setTitle("Server Messenger");
        this.setSize(600, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        messageArea.setEditable(false);
//        heading.setIcon(new ImageIcon("src/logo.png"));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

//        frame layout
        this.setLayout(new BorderLayout());

//        adding component to frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        JScrollBar sb = jScrollPane.getVerticalScrollBar();
        sb.setValue( sb.getMaximum() );
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
    }
    public void startReading()
    {
//        Start reading
        Runnable r1 = ()->{
            System.out.println("Reader started...");
            try
            {
                while (true)
                {
                    String msg = br.readLine();
                    if (msg.equals("exit"))
                    {
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
//                    System.out.println("Client : " + msg);
                    messageArea.append("Client: "+msg+"\n");
                }
            }
            catch (Exception e)
            {
//                e.printStackTrace();
                System.out.println("Connection closed!!!");
            }
        };
        new Thread(r1).start();
    }
    public void startWriting()
    {
//        Start writing
        Runnable r2 = ()->{
            System.out.println("Writer started...");
            try
            {
                while (!socket.isClosed())
                {
                    BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
                    String content = kb.readLine();
                    out.println(content);
                    out.flush();
                    if(content.equals("exit"))
                    {
                        socket.close();
                        break;
                    }

                }
            }
            catch (Exception e)
            {
//                e.printStackTrace();
                System.out.println("Connection closed!!!");
            }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args)
    {
        System.out.println("Server going to start...");
        new Server();
    }
}
