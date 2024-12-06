/*
 * 
 */
package berard_demers_5;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *  The possibles pieces that a square could contain
 *  images were cropped from screenshots from
 *  https://commons.wikimedia.org/wiki/Category:SVG_chess_pieces
 * @author jxdem
 */
public enum Piece {
    BLANK("nothing here",true),
    //white pieces
    WHITE_PAWN("piece_images/white_Pawn_Chess_piece.png",true),
    WHITE_KNIGHT("piece_images/white_Knight_Chess_piece.png",true),
    WHITE_BISHOP("piece_images/white_Bishop_Chess_piece.png",true),
    WHITE_ROOK("piece_images/white_Rook_Chess_piece.png",true),
    WHITE_QUEEN("piece_images/white_Queen_Chess_piece.png",true),
    WHITE_KING("piece_images/white_King_Chess_piece.png",true),
    //black pieces    
    BLACK_PAWN("piece_images/black_Pawn_Chess_piece.png",false),
    BLACK_KNIGHT("piece_images/black_Knight_Chess_piece.png",false),
    BLACK_BISHOP("piece_images/black_Bishop_Chess_piece.png",false),
    BLACK_ROOK("piece_images/black_Rook_Chess_piece.png",false),
    BLACK_QUEEN("piece_images/black_Queen_Chess_piece.png",false),
    BLACK_KING("piece_images/black_King_Chess_piece.png",false);

    //the icon loaded from the saved photos
    private BufferedImage icon = new BufferedImage(1, 1, 2);
    
    //tells if the associated piece is black or white
    private boolean isWhite;

    /**
     * constructor for piece
     * @param iconName name of icon
     * @param isWhite is white or black
     */
    Piece(String iconName, boolean isWhite) {
        this.isWhite = isWhite;
        if (!iconName.equals("nothing here")) {
            File f = new File(iconName);
            try {
                icon = ImageIO.read(f);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * returns if piece is white or black
     * @return 
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * returns the icon of the piece
     * @param height height of icon
     * @param width width of icon
     * @return image icon
     */
    public ImageIcon getIcon(int height, int width) {
        if(height == 0){
            height =(int)700/8;
        }
        if(width == 0){
            width = (int)700/8;
        }
        ImageIcon MyImage = new ImageIcon(icon);
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
}
