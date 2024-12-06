/*
 * 
 */
package berard_demers_5;

import ServerEnd.Server;
import javax.swing.*;

/**
 * The player of the white pieces
 * @author jxdem
 */
public class Player1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        JFrame game = new JFrame("Player 1");
//        Board b1 = new Board("cs.merrimack.edu",5011,true);
        Board b1 = new Board("127.0.0.1",5011,true);
        game.add(b1);
        //needed statements 
        game.setSize(700, 700);
        game.setVisible(true);
        game.setLocation(150, 75);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Thread t1 = new Thread(b1);
        t1.start();
        
    }
    
}
