/*
* @Author: Puffrora
* @Date:   2019-09-20 15:35:02
* @Last Modified by:   Puffrora
* @Last Modified time: 2019-10-24 11:53:15
*/
import whiteboard.*;
import whiteboard.WhiteBoard.drawings;

import java.awt.*;
import java.awt.event.*;

import javax.net.ServerSocketFactory;
import javax.swing.*;
import java.io.*;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;



import message.MsgOperation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateWhiteBoard {
    static int port = 9090;
    static int counter = 0;
    static String userName = "Server";
    volatile static ArrayList<drawings> sumDraw = new ArrayList<drawings>();
    static ArrayList<Socket> clientList = new ArrayList<Socket>();
    static WhiteBoard newPad;
    static String dicpath = "chatmsg.txt";

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Somethings wrong in 'UIManager.setLookAndFeel' process");
        }

        Thread WBT = new Thread(() -> startWBService());
        WBT.start();

        Thread chatboxT = new Thread(() -> startChatboxService());
        chatboxT.start();
    }

    public static void startWBService() {
        newPad = new WhiteBoard(userName);
        newPad.setTitle("Server Side");
        newPad.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        ServerSocketFactory factory = ServerSocketFactory.getDefault();
        try (ServerSocket server = factory.createServerSocket(port)) {
            System.out.println("Waiting for client connection to port number: " + port);

            // Wait for connections.
            while (true) {
                Socket client = null;
                try {
                    client = server.accept();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                counter++;
                System.out.println("Client " + counter + ": Applying for connection! in port num: " + client.getPort());

                // Start a new thread for a connection
                ServerThread t = new ServerThread(client);
                t.start();
            }

        } catch (IOException e) {
            System.out.println("Unable to setup server, try another port.");
            // System.exit(1);
        }

    }

    static class ServerThread extends Thread {
        // Client sends the query here and this thread will produce the responses to the client. In this case, client sends the drawings here
        // And the drawings will be combined with other drawings then send back to the client.
        Socket client;
        DataInputStream is;
        DataOutputStream os;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        BufferedReader in;

        ServerThread(Socket client) {
            this.client = client;
            clientList.add(client);
        }

        public void run() {
            String clientDrawing;
            try {
                //连接成功后得到数据输出流
                os = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
                oos = new ObjectOutputStream(client.getOutputStream());
                
                is = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                ois = new ObjectInputStream(client.getInputStream());


                //x1,y1为起始点坐标，x2,y2为终点坐标。四个点的初始值设为0

                
                int count = 0;
                Graphics g = newPad.getGraphics();
                while (true) {
                    // drawings nb = (drawings) ois.readObject();
                    int x1 = is.readInt();
                    if(x1 < -10000) {
                		x1 = is.readInt();
                	}
                    int y1 = is.readInt();
                    int x2 = is.readInt();
                    int y2 = is.readInt();
                    //                    System.out.println(nb.x1);
                    //                    System.out.println(nb.y1);
                    //                    System.out.println(nb.x2);
                    //                    System.out.println(nb.y2);
                    //                    newPad.createNewItemInClient(nb);
                    g.drawLine(x1, y1, x2, y2);
                    for(Socket client:clientList) {
                    	os = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
                        // oos = new ObjectOutputStream(client.getOutputStream());
                        os.writeInt(x1);
                    	os.writeInt(y1);
                    	os.writeInt(x2);
                    	os.writeInt(y2);
                    	os.flush();
                        // if(nb != null && nb.x1 != 0 && nb.y1 != 0) {
                        // oos.writeObject(nb);
                        // }
                    }
                    //                    WhiteBoard.drawings draw = (WhiteBoard.drawings) ois.readObject();
                    //                    sumDraw.add(draw);
                    //                    oos.writeObject(sumDraw);
                                        
                                        
                                        
                                        
                                        
                                        
                    //                    if (newOb != null) {
                    //
                    //                        System.out.println(newOb.x1 + " " + newOb.y2 + " " + newOb.x2 + " " + newOb.y2);
                    //                        System.out.println(clientSocket.getPort() + "cacacaa" + clientSocket.getLocalPort());
                    //                        System.out.println(number);
                    //
                    //                        ArrayList<Integer> coordinate = new ArrayList<Integer>();
                    //
                    //    //                            oss.writeObject(newOb);
                    //                        coordinate.add(newOb.x1);
                    //                        coordinate.add(newOb.y1);
                    //                        coordinate.add(newOb.x2);
                    //                        coordinate.add(newOb.y2);
                    //                        for (int i = 0; i < 4; i++) {
                    //                            os.writeInt(coordinate.get(i));
                    //                            System.out.println("wrote " + i);
                    //                        }
                    //                        count += 1;
                    //                        os.flush();
                    //    //                        if(count == 20) {
                    //    //                            os.flush();
                    //    //                            count = 0;
                    //    //                        }
                    //
                    //                        newOb = null;
                    //    //                        int x1, x2, y1, y2;
                    //    //                        x1=is.readInt();
                    //    //                        y1=is.readInt();
                    //    //                        x2=is.readInt();
                    //    //                        y2=is.readInt();
                    //    //                        Graphics g = this.getGraphics();
                    //    //                        g.drawLine(x1, y1, x2, y2);
                    //                    }
                }
            } catch (IOException e) {
                System.out.println("IOException");
                 e.printStackTrace();
            } /*
            catch (ClassNotFoundException cnfe) {
                System.out.println("ClassNotFoundException");
                cnfe.printStackTrace();
            }*/
        }
    }

    public static void startChatboxService(){

        ServerSocketFactory factoryc = ServerSocketFactory.getDefault();

        try (ServerSocket serverc = factoryc.createServerSocket(2029)) {
            System.out.println("Waiting for client connection-");

            // Wait for connections.
            while (true) {
                Socket clientc = serverc.accept();
                // counter ++;
                // System.out.println("Client " + counter + ": Applying for connection!");

                // Start a new thread for a connection
                Thread t = new Thread(() -> serveClient(clientc, dicpath));
                t.start();
            }

        }
        catch (IOException e) {
            // e.printStackTrace();
            System.out.println("IOException occurs");
        }
    }

    private static void serveClient(Socket client, String dicpath) {
        MsgOperation op = new MsgOperation();

        try (Socket clientSocket = client) {
            // Input stream
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            // Output Stream
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            String msg = input.readUTF();
            System.out.println("CLIENT: " + clientSocket.getInetAddress().getHostName() + " " + clientSocket.getLocalPort() + " " + msg);

            output.writeUTF(op.getMsg(dicpath, msg));

        }
        catch (SocketException e) {
            System.out.println("closed ...");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}