/*
 * 
 */
package berard_demers_5;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.*;

/**
 *  each square on the chess board acts as a button to allow for clicking to move
 * @author jxdem
 */
public class Square extends JButton implements Serializable{
    
    //coordinates to allow for moving and board managment
    private final int row;
    private final int column;
    
    //tells if it has a black, white, or no piece
    private boolean hasWhitePiece;
    private boolean hasBlackPiece;
    private Piece piece = Piece.BLANK;//default

    /**
     * constructor for squares
     * @param isWhite if square is white
     * @param row row of square
     * @param column column of square
     */
    public Square(boolean isWhite, int row, int column) {
        
        this.row = row;
        this.column = column;
        if (isWhite) {
            this.setBackground(Color.white);
            this.setOpaque(true);
            this.setBorderPainted(false);
        } else {
            this.setBackground(Color.LIGHT_GRAY);
            this.setOpaque(true);
            this.setBorderPainted(false);
        }
        this.setEnabled(false);

    }

    /**
     * returns the piece on this square
     * @return the piece
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * changes the piece on the square
     * @param piece the new piece
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
        if (!piece.equals(Piece.BLANK)) {
            this.setEnabled(true);
            

            if (piece.isWhite()) {
                hasWhitePiece = true;
                hasBlackPiece = false;
            } else if (!piece.isWhite()) {
                hasWhitePiece = false;
                hasBlackPiece = true;
            }
        }else{
                hasWhitePiece = false;
                hasBlackPiece = false;
        }
        this.setIcon(piece.getIcon(this.getHeight(),this.getWidth()));
    }

    /**
     * returns the row of the square
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * returns the column of the square
     * @return the column
     */
    public int getColumn() {
        return column;
    }

    /**
     * returns if the square has a white piece
     * @return a boolean representing if it is white
     */
    public boolean hasWhitePiece() {
        return hasWhitePiece;
    }

    /**
     * returns if the square has a black piece
     * @return a boolean representing if it is black
     */
    public boolean hasBlackPiece() {
        return hasBlackPiece;
    }
    
    /**
     * tostring for the square object
     * @return the column and row as a string
     */
    public String toString(){
        return("" + this.column + " " + this.row);
    }
    
}
