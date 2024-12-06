/*
 * 
 */
package berard_demers_5;

import java.io.Serializable;

/**
 * This class is soley used to transfer 2D arrays across the Object streams
 * @author jxdem
 */
public class Container implements Serializable{
    private Square[][] squares; //the stored value
    
    /**
     * the constructor that takes the stored value as the param
     * @param squares the soon to be stored value
     */
    public Container(Square[][] squares){
        this.squares = squares;
    }

    /**
     * standard getter
     * @return the current 2D array of squares
     */
    public Square[][] getSquares() {
        return squares;
    }

    /**
     * standard setter
     * @param squares the new 2D array of squares
     */
    public void setSquares(Square[][] squares) {
        this.squares = squares;
    }
    
    
    
}
