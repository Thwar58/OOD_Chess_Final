/*
 * 
 */
package berard_demers_5;

import javax.swing.JFrame;

/**
 * the player for black's pieces 
 * @author jxdem
 */
public class Player2 {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        JFrame game2 = new JFrame("Player 2");
//        Board b2 = new Board("cs.merrimack.edu",5011,false);
        Board b2 = new Board("127.0.0.1",5011,false);
        game2.add(b2);
        //needed statements 
        game2.setSize(700, 700);
        game2.setVisible(true);
        game2.setLocation(850, 75);
        game2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Thread t2 = new Thread(b2);
        t2.start();
        
    }
}
