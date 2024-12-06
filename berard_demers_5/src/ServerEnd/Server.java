/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerEnd;

import berard_demers_5.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author anthonyberard
 */
public class Server extends Thread {

    public static void main(String[] args) {

        Server server = new Server(5011);
        server.start();
        
    }
    
    private Socket connectionToBoards = null;
    private ServerSocket server = null;

    private ArrayList<BoardThread> threads;
    private int port;

    /**
     * takes in a port parameter and creates a server socket
     * @param p the port number
     */
    public Server(int p) {
        this.port = p;

        try {
            server = new ServerSocket(this.port);
        } catch (IOException ex) {

        }

        threads = new ArrayList<>();

    }

    /**
     * run method for server
     */
    @Override
    public void run() {
        while (true) {

            try {
                // Every time a connection is made, spawn a new client thread
                connectionToBoards = server.accept();
                // Spawn a new thread for the new chat window 
                BoardThread newThread = new BoardThread(this.port);
                newThread.start();
                threads.add(newThread);
            } catch (IOException ex) {
                System.err.println("Connection to client can't be established");
            } catch (NullPointerException ex) {
                ;
            }
        }
    }

    /**
     * calls update method on all of the threads
     * @param board the update board to send out
     * @throws IOException 
     */
    private void updateBoards(Square[][] board) throws IOException {
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).update(board);

        }
    }

    
    public class BoardThread extends Thread {

        private ObjectInputStream in = null;
        private ObjectOutputStream out = null;

        /**
         * initializes input and output streams
         * @param port port number
         */
        public BoardThread(int port) {
            try {
                // Try to establish a connection to server
                //   Should be in run to allow the other chats to open while this
                //   connection is established asynchronously
                in = new ObjectInputStream(connectionToBoards.getInputStream());
                out = new ObjectOutputStream(connectionToBoards.getOutputStream());
            } catch (IOException ex) {
                System.err.println("Error with I/O at server.");
            }
        }

        /**
         * run method for the board threads
         */
        @Override
        public void run() {
            // Reads message from client continuously, and when one is received
            //   it adds the new text to the "chat window" string that controls
            //   the entire chat window for each chat client.
            while (true) {
                Square[][] squares = null;
                try {
                    squares = ((Container) in.readObject()).getSquares();
                    System.out.println("read in the squares");
                    
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                try {
                    updateBoards(squares);
                } catch (IOException ex) {
                    //System.out.println("Could not update board.");
                }
            }
        }

        /**
         * updates the boards
         * @param board the updated board to send
         * @throws IOException 
         */
        public void update(Square[][] board) throws IOException {
            System.out.println("updating boards");
            Container c = new Container(board);

            out.reset();
            out.writeUnshared(c);

            out.flush();

        }

        /**
         * closes server and streams
         */
        @Override
        public void finalize() {
            System.out.println("Closing connection");

            try {
                // close connection
                connectionToBoards.close();
                in.close();
                out.close();
            } catch (IOException ex) {
                System.err.println("Error closing socket connection from server.");
            } finally {
                try {
                    super.finalize();
                } catch (Throwable ex) {

                }
            }
        }
    }

}
