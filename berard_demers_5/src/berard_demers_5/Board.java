/*
 * 
 */
package berard_demers_5;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.net.UnknownHostException;
import javax.swing.*;

/**
 *  The board that finds moves for pieces and stores the squares int a neat display
 * @author jxdem
 */
public class Board extends JPanel implements Runnable, Serializable {

    //the board
    private Square[][] squares;
    //for moving functionality only
    private Square pieceToMove;
    private Square destinationSquare;
    private boolean isWhiteTurn;
    private boolean isWhite;
    private boolean isPieceSelected;

    //for server stuff
    private String address;
    private int port;
    //sending objecting to and from server
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private Socket connectionToServer = null;

    /**
     * constructor for a board
     * @param address the ip address
     * @param port the port to connect to
     * @param isWhite if the board is white or black
     */
    public Board(String address, int port, boolean isWhite) {
//building the board        
        super(new GridLayout(8, 8));
        squares = new Square[8][8];
        isPieceSelected = false;
        isWhiteTurn = true;
        this.isWhite = isWhite;
        boolean shouldTheSquareBeWhite = true;
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[0].length; j++) {
                squares[i][j] = new Square(shouldTheSquareBeWhite, i, j);
                shouldTheSquareBeWhite = !shouldTheSquareBeWhite;
            }
            shouldTheSquareBeWhite = !shouldTheSquareBeWhite;
        }

        if (isWhite) {
            for (int i = 0; i < squares.length; i++) {
                for (int j = 0; j < squares[0].length; j++) {
                    this.add(squares[i][j]);
                }
            }
        } else {
            for (int i = squares.length; i > 0; i--) {
                for (int j = squares[0].length; j > 0; j--) {
                    this.add(squares[i - 1][j - 1]);
                }
            }
        }

        //populate the board with pieces       
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[0].length; j++) {
                if (squares[i][j].getRow() == 6) {
                    squares[i][j].setPiece(Piece.WHITE_PAWN);
                } else if (squares[i][j].getRow() == 1) {
                    squares[i][j].setPiece(Piece.BLACK_PAWN);
                }
            }
        }

        //populating the black side of the board
        squares[0][0].setPiece(Piece.BLACK_ROOK);
        squares[0][1].setPiece(Piece.BLACK_KNIGHT);
        squares[0][2].setPiece(Piece.BLACK_BISHOP);
        squares[0][3].setPiece(Piece.BLACK_QUEEN);
        squares[0][4].setPiece(Piece.BLACK_KING);
        squares[0][5].setPiece(Piece.BLACK_BISHOP);
        squares[0][6].setPiece(Piece.BLACK_KNIGHT);
        squares[0][7].setPiece(Piece.BLACK_ROOK);

        //populating the white side of the board
        squares[7][0].setPiece(Piece.WHITE_ROOK);
        squares[7][1].setPiece(Piece.WHITE_KNIGHT);
        squares[7][2].setPiece(Piece.WHITE_BISHOP);
        squares[7][3].setPiece(Piece.WHITE_QUEEN);
        squares[7][4].setPiece(Piece.WHITE_KING);
        squares[7][5].setPiece(Piece.WHITE_BISHOP);
        squares[7][6].setPiece(Piece.WHITE_KNIGHT);
        squares[7][7].setPiece(Piece.WHITE_ROOK);

//set the disables icons for all the pieces 
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[0].length; j++) {
                if (squares[i][j].hasWhitePiece() && isWhite && isWhiteTurn) {
                    squares[i][j].setEnabled(true);
                } else if (squares[i][j].hasBlackPiece() && !isWhite && !isWhiteTurn) {
                    squares[i][j].setEnabled(true);
                } else {
                    squares[i][j].setEnabled(false);
                    squares[i][j].setDisabledIcon(squares[i][j].getPiece()
                            .getIcon(squares[i][j].getHeight(), squares[i][j]
                                    .getWidth()));
                }
            }
        }
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[0].length; j++) {
                //pieceToMove = squares[i][j];
                squares[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //selcting the piece
                        if ((isWhite && ((Square) e.getSource()).hasWhitePiece())
                                || (!isWhite && ((Square) e.getSource()).hasBlackPiece())) {
                            isPieceSelected = true;
                            pieceToMove = (Square) e.getSource();
                            if (pieceToMove.getPiece().equals(Piece.BLANK)) {
                                isPieceSelected = false;
                            } else {
                                ArrayList<Square> legalMoves = getLegalMoves(pieceToMove);
                                //change the all pieces to the getmoves array
                                for (int i = 0; i < squares.length; i++) {
                                    for (int j = 0; j < squares[0].length; j++) {
                                        if (legalMoves.contains(squares[i][j]) || pieceToMove.equals(squares[i][j])) {
                                            squares[i][j].setEnabled(true);
                                        } else {
                                            squares[i][j].setEnabled(false);
                                            if (squares[i][j].hasBlackPiece() && !isWhite) {
                                                squares[i][j].setEnabled(true);
                                            }
                                            if (squares[i][j].hasWhitePiece() && isWhite) {
                                                squares[i][j].setEnabled(true);
                                            }
                                        }
                                    }
                                }
                            }
                            //moves the piece
                        } else if ((isPieceSelected && isWhite && !((Square) e.getSource()).hasWhitePiece())
                                || (isPieceSelected && !isWhite && !((Square) e.getSource()).hasBlackPiece())) {
                            for (int i = 0; i < squares.length; i++) {
                                for (int j = 0; j < squares[0].length; j++) {
                                    if (!squares[i][j].hasWhitePiece() && !squares[i][j].hasBlackPiece()) {
                                        squares[i][j].setEnabled(false);
                                    }
                                }
                            }
                            destinationSquare = (Square) e.getSource();
                            if (destinationSquare.getPiece().equals(Piece.BLACK_KING)) {
                                for (int i = 0; i < squares.length; i++) {
                                    for (int j = 0; j < squares[0].length; j++) {
                                        if (squares[i][j].hasBlackPiece()) {
                                            squares[i][j].setPiece(Piece.BLANK);
                                        }
                                    }
                                }
                            }
                            if (destinationSquare.getPiece().equals(Piece.WHITE_KING)) {
                                for (int i = 0; i < squares.length; i++) {
                                    for (int j = 0; j < squares[0].length; j++) {
                                        if (squares[i][j].hasWhitePiece()) {
                                            squares[i][j].setPiece(Piece.BLANK);
                                        }
                                    }
                                }
                            }
                            if (destinationSquare.getRow() == 0 && pieceToMove.getPiece().equals(Piece.WHITE_PAWN)) {
                                destinationSquare.setPiece(Piece.WHITE_QUEEN);
                            } else if (destinationSquare.getRow() == 7 && pieceToMove.getPiece().equals(Piece.BLACK_PAWN)) {
                                destinationSquare.setPiece(Piece.BLACK_QUEEN);
                            } else {
                                destinationSquare.setPiece(pieceToMove.getPiece());
                            }
                            pieceToMove.setPiece(Piece.BLANK);
                            isPieceSelected = false;
                            destinationSquare = null;

                            try {
                                Container c = new Container(squares);
                                System.out.println(c);
                                out.reset();
                                out.writeUnshared(c);
                                out.flush();
                            } catch (IOException ex) {
                                System.out.println("could not send move.");
                            }

                        }

                    }
                });

            }
        }

//socket stuff dealt with here
        this.address = address;
        this.port = port;
    }

    /**
     * run method for the board
     */
    @Override
    public void run() {
        // Try to establish a connection to server
        //   Should be in run to allow the other chats to open while this
        //   connection is established asynchronously
        try {
            connectionToServer = new Socket(address, port);
            System.out.println("connected");

            // takes in from terminal 
            out = new ObjectOutputStream(connectionToServer.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connectionToServer.getInputStream());

        } catch (UnknownHostException u) {
            System.err.println("Can't find the host:\n  "
                    + address + "\n  " + port);
        } catch (IOException i) {
            System.err.println("No input");
        }

        while (true) {
            try {
                Square[][] board = ((Container) in.readObject()).getSquares();
                isWhiteTurn = !isWhiteTurn;
                for (int i = 0; i < squares.length; i++) {
                    for (int j = 0; j < squares[0].length; j++) {
                        squares[i][j].setPiece(board[i][j].getPiece());
                    }
                }
                for (int i = 0; i < squares.length; i++) {
                    for (int j = 0; j < squares[0].length; j++) {
                        if (squares[i][j].hasWhitePiece() && isWhite && isWhiteTurn) {
                            squares[i][j].setEnabled(true);
                        } else if (squares[i][j].hasBlackPiece() && !isWhite && !isWhiteTurn) {
                            squares[i][j].setEnabled(true);
                        } else {
                            squares[i][j].setEnabled(false);
                            squares[i][j].setDisabledIcon(squares[i][j].getPiece()
                                    .getIcon(squares[i][j].getHeight(), squares[i][j]
                                            .getWidth()));
                        }
                    }
                }
            } catch (IOException ex) {
            } catch (ClassNotFoundException ex) {

            }
        }
    }

    /**
     * closes board and streams
     */
    @Override
    public void finalize() {
        // close the connection 
        try {
            in.close();
            out.close();
            connectionToServer.close();
        } catch (IOException i) {
            System.err.println("Error closing connection on board.");
        } finally {
            try {
                super.finalize();
            } catch (Throwable ex) {
                System.err.println("Runnable can't finalize");
            }
        }
    }

    /**
     * returns board
     * @return 2d array of squares
     */
    public Square[][] getBoard() {
        return this.squares;
    }

    /**
     * gets the legal moves for a piece
     * @param spot the location of the piece
     * @return an arraylist of possible moves
     */
    private ArrayList<Square> getLegalMoves(Square spot) {
        ArrayList<Square> moves = new ArrayList<>();

//knight moves
        if (spot.getPiece().equals(Piece.BLACK_KNIGHT)) {
            ArrayList<Square> totalMoves = getKnightMoves(spot);
            for (int i = 0; i < totalMoves.size(); i++) {
                if (!totalMoves.get(i).hasBlackPiece()) {
                    moves.add(totalMoves.get(i));
                }
            }
        }
        if (spot.getPiece().equals(Piece.WHITE_KNIGHT)) {
            ArrayList<Square> totalMoves = getKnightMoves(spot);
            for (int i = 0; i < totalMoves.size(); i++) {
                if (!totalMoves.get(i).hasWhitePiece()) {
                    moves.add(totalMoves.get(i));
                }
            }
        }
//bishop moves
        if (spot.getPiece().equals(Piece.BLACK_BISHOP)) {
            moves = getBishopMoves(spot, false);
        }
        if (spot.getPiece().equals(Piece.WHITE_BISHOP)) {
            moves = getBishopMoves(spot, true);

        }
//pawn moves
        if (spot.getPiece().equals(Piece.WHITE_PAWN)) {
            if (squares[spot.getRow() - 1][spot.getColumn()].getPiece().equals(Piece.BLANK)) {
                moves.add(squares[spot.getRow() - 1][spot.getColumn()]);
                try {
                    if (squares[spot.getRow() - 2][spot.getColumn()].getPiece().equals(Piece.BLANK) && spot.getRow() == 6) {
                        moves.add(squares[spot.getRow() - 2][spot.getColumn()]);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }

            try {
                if (squares[spot.getRow() - 1][spot.getColumn() + 1].hasBlackPiece()) {
                    moves.add(squares[spot.getRow() - 1][spot.getColumn() + 1]);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                if (squares[spot.getRow() - 1][spot.getColumn() - 1].hasBlackPiece()) {
                    moves.add(squares[spot.getRow() - 1][spot.getColumn() - 1]);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }

        }
        if (spot.getPiece().equals(Piece.BLACK_PAWN)) {
            if (squares[spot.getRow() + 1][spot.getColumn()].getPiece().equals(Piece.BLANK)) {
                moves.add(squares[spot.getRow() + 1][spot.getColumn()]);
                try {
                    if (squares[spot.getRow() + 2][spot.getColumn()].getPiece().equals(Piece.BLANK) && spot.getRow() == 1) {
                        moves.add(squares[spot.getRow() + 2][spot.getColumn()]);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }

            try {
                if (squares[spot.getRow() + 1][spot.getColumn() + 1].hasWhitePiece()) {
                    moves.add(squares[spot.getRow() + 1][spot.getColumn() + 1]);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                if (squares[spot.getRow() + 1][spot.getColumn() - 1].hasWhitePiece()) {
                    moves.add(squares[spot.getRow() + 1][spot.getColumn() - 1]);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }

        }
//King moves        
        if (spot.getPiece().equals(Piece.BLACK_KING)) {
            moves = getKingMoves(spot);
        }
        if (spot.getPiece().equals(Piece.WHITE_KING)) {
            moves = getKingMoves(spot);
        }
//rook moves
        if (spot.getPiece().equals(Piece.WHITE_ROOK)) {
            moves = getRookMoves(spot, true);
        }
        if (spot.getPiece().equals(Piece.BLACK_ROOK)) {
            moves = getRookMoves(spot, false);
        }
//queen moves
        if (spot.getPiece().equals(Piece.WHITE_QUEEN)) {
            ArrayList<Square> additionalMoves = getBishopMoves(spot, true);
            moves = getRookMoves(spot, true);
            for (int i = 0; i < additionalMoves.size(); i++) {
                moves.add(additionalMoves.get(i));
            }
        }
        if (spot.getPiece().equals(Piece.BLACK_QUEEN)) {
            ArrayList<Square> additionalMoves = getBishopMoves(spot, false);
            moves = getRookMoves(spot, false);
            for (int i = 0; i < additionalMoves.size(); i++) {
                moves.add(additionalMoves.get(i));
            }
        }

        return moves;
    }

    /**
     * gets the moves for a knight
     * @param spot the location of the piece
     * @return arraylist of possible moves
     */
    private ArrayList<Square> getKnightMoves(Square spot) {
        ArrayList<Square> moves = new ArrayList<>();

        try {
            moves.add(squares[spot.getRow() + 2][spot.getColumn() + 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() + 2][spot.getColumn() - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() - 2][spot.getColumn() + 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() - 2][spot.getColumn() - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() + 1][spot.getColumn() + 2]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() + 1][spot.getColumn() - 2]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() - 1][spot.getColumn() + 2]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() - 1][spot.getColumn() - 2]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return moves;
    }

    /**
     * gets the moves for a bishop
     * @param spot the location 
     * @return arraylist of possible moves
     */
    private ArrayList<Square> getBishopMoves(Square spot, boolean isWhite) {
        ArrayList<Square> moves = new ArrayList<>();

        int difX = 0;
        for (int i = 0; i < 8; i++) {
            difX++;
            try {
                if (squares[spot.getRow() + difX][spot.getColumn() + difX].getPiece().equals(Piece.BLANK)) {
                    moves.add(squares[spot.getRow() + difX][spot.getColumn() + difX]);
                } else if (squares[spot.getRow() + difX][spot.getColumn() + difX].hasBlackPiece() && !isWhite) {
                    break;
                } else if (squares[spot.getRow() + difX][spot.getColumn() + difX].hasBlackPiece() && isWhite) {
                    moves.add(squares[spot.getRow() + difX][spot.getColumn() + difX]);
                    break;
                } else if (squares[spot.getRow() + difX][spot.getColumn() + difX].hasWhitePiece() && isWhite) {
                    break;
                } else if (squares[spot.getRow() + difX][spot.getColumn() + difX].hasWhitePiece() && !isWhite) {
                    moves.add(squares[spot.getRow() + difX][spot.getColumn() + difX]);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }
        difX = 0;
        for (int i = 0; i < 8; i++) {
            difX++;
            try {
                if (squares[spot.getRow() - difX][spot.getColumn() + difX].getPiece().equals(Piece.BLANK)) {
                    moves.add(squares[spot.getRow() - difX][spot.getColumn() + difX]);
                } else if (squares[spot.getRow() - difX][spot.getColumn() + difX].hasBlackPiece() && !isWhite) {
                    break;
                } else if (squares[spot.getRow() - difX][spot.getColumn() + difX].hasBlackPiece() && isWhite) {
                    moves.add(squares[spot.getRow() - difX][spot.getColumn() + difX]);
                    break;
                } else if (squares[spot.getRow() - difX][spot.getColumn() + difX].hasWhitePiece() && isWhite) {
                    break;
                } else if (squares[spot.getRow() - difX][spot.getColumn() + difX].hasWhitePiece() && !isWhite) {
                    moves.add(squares[spot.getRow() - difX][spot.getColumn() + difX]);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }
        difX = 0;
        for (int i = 0; i < 8; i++) {
            difX++;
            try {
                if (squares[spot.getRow() - difX][spot.getColumn() - difX].getPiece().equals(Piece.BLANK)) {
                    moves.add(squares[spot.getRow() - difX][spot.getColumn() - difX]);
                } else if (squares[spot.getRow() - difX][spot.getColumn() - difX].hasBlackPiece() && !isWhite) {
                    break;
                } else if (squares[spot.getRow() - difX][spot.getColumn() - difX].hasBlackPiece() && isWhite) {
                    moves.add(squares[spot.getRow() - difX][spot.getColumn() - difX]);
                    break;
                } else if (squares[spot.getRow() - difX][spot.getColumn() - difX].hasWhitePiece() && isWhite) {
                    break;
                } else if (squares[spot.getRow() - difX][spot.getColumn() - difX].hasWhitePiece() && !isWhite) {
                    moves.add(squares[spot.getRow() - difX][spot.getColumn() - difX]);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }
        difX = 0;
        for (int i = 0; i < 8; i++) {
            difX++;
            try {
                if (squares[spot.getRow() + difX][spot.getColumn() - difX].getPiece().equals(Piece.BLANK)) {
                    moves.add(squares[spot.getRow() + difX][spot.getColumn() - difX]);
                } else if (squares[spot.getRow() + difX][spot.getColumn() - difX].hasBlackPiece() && !isWhite) {
                    break;
                } else if (squares[spot.getRow() + difX][spot.getColumn() - difX].hasBlackPiece() && isWhite) {
                    moves.add(squares[spot.getRow() + difX][spot.getColumn() - difX]);
                    break;
                } else if (squares[spot.getRow() + difX][spot.getColumn() - difX].hasWhitePiece() && isWhite) {
                    break;
                } else if (squares[spot.getRow() + difX][spot.getColumn() - difX].hasWhitePiece() && !isWhite) {
                    moves.add(squares[spot.getRow() + difX][spot.getColumn() - difX]);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }

        return moves;
    }

    /**
     * gets the moves for a king
     * @param spot the location 
     * @return arraylist of possible moves
     */
    private ArrayList<Square> getKingMoves(Square spot) {
        ArrayList<Square> moves = new ArrayList<>();
        try {
            moves.add(squares[spot.getRow() + 1][spot.getColumn() + 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() + 1][spot.getColumn()]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() + 1][spot.getColumn() - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow()][spot.getColumn() + 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow()][spot.getColumn() - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() - 1][spot.getColumn() + 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() - 1][spot.getColumn()]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            moves.add(squares[spot.getRow() - 1][spot.getColumn() - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return moves;
    }

    /**
     * gets the moves for a rook
     * @param spot the location 
     * @return arraylist of possible moves
     */
    private ArrayList<Square> getRookMoves(Square spot, boolean isWhite) {
        ArrayList<Square> moves = new ArrayList<>();

        int dif = 0;
        for (int i = 0; i < 8; i++) {
            dif++;
            try {
                if (squares[spot.getRow() + dif][spot.getColumn()].getPiece().equals(Piece.BLANK)) {
                    moves.add(squares[spot.getRow() + dif][spot.getColumn()]);
                } else if (squares[spot.getRow() + dif][spot.getColumn()].hasBlackPiece() && !isWhite) {
                    break;
                } else if (squares[spot.getRow() + dif][spot.getColumn()].hasBlackPiece() && isWhite) {
                    moves.add(squares[spot.getRow() + dif][spot.getColumn()]);
                    break;
                } else if (squares[spot.getRow() + dif][spot.getColumn()].hasWhitePiece() && isWhite) {
                    break;
                } else if (squares[spot.getRow() + dif][spot.getColumn()].hasWhitePiece() && !isWhite) {
                    moves.add(squares[spot.getRow() + dif][spot.getColumn()]);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        dif = 0;
        for (int i = 0; i < 8; i++) {
            dif++;
            try {
                if (squares[spot.getRow() - dif][spot.getColumn()].getPiece().equals(Piece.BLANK)) {
                    moves.add(squares[spot.getRow() - dif][spot.getColumn()]);
                } else if (squares[spot.getRow() - dif][spot.getColumn()].hasBlackPiece() && !isWhite) {
                    break;
                } else if (squares[spot.getRow() - dif][spot.getColumn()].hasBlackPiece() && isWhite) {
                    moves.add(squares[spot.getRow() - dif][spot.getColumn()]);
                    break;
                } else if (squares[spot.getRow() - dif][spot.getColumn()].hasWhitePiece() && isWhite) {
                    break;
                } else if (squares[spot.getRow() - dif][spot.getColumn()].hasWhitePiece() && !isWhite) {
                    moves.add(squares[spot.getRow() - dif][spot.getColumn()]);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        dif = 0;
        for (int i = 0; i < 8; i++) {
            dif++;
            try {
                if (squares[spot.getRow()][spot.getColumn() + dif].getPiece().equals(Piece.BLANK)) {
                    moves.add(squares[spot.getRow()][spot.getColumn() + dif]);
                } else if (squares[spot.getRow()][spot.getColumn() + dif].hasBlackPiece() && !isWhite) {
                    break;
                } else if (squares[spot.getRow()][spot.getColumn() + dif].hasBlackPiece() && isWhite) {
                    moves.add(squares[spot.getRow()][spot.getColumn() + dif]);
                    break;
                } else if (squares[spot.getRow()][spot.getColumn() + dif].hasWhitePiece() && isWhite) {
                    break;
                } else if (squares[spot.getRow()][spot.getColumn() + dif].hasWhitePiece() && !isWhite) {
                    moves.add(squares[spot.getRow()][spot.getColumn() + dif]);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        dif = 0;
        for (int i = 0; i < 8; i++) {
            dif++;
            try {
                if (squares[spot.getRow()][spot.getColumn() - dif].getPiece().equals(Piece.BLANK)) {
                    moves.add(squares[spot.getRow()][spot.getColumn() - dif]);
                } else if (squares[spot.getRow()][spot.getColumn() - dif].hasBlackPiece() && !isWhite) {
                    break;
                } else if (squares[spot.getRow()][spot.getColumn() - dif].hasBlackPiece() && isWhite) {
                    moves.add(squares[spot.getRow()][spot.getColumn() - dif]);
                    break;
                } else if (squares[spot.getRow()][spot.getColumn() - dif].hasWhitePiece() && isWhite) {
                    break;
                } else if (squares[spot.getRow()][spot.getColumn() - dif].hasWhitePiece() && !isWhite) {
                    moves.add(squares[spot.getRow()][spot.getColumn() - dif]);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }

        return moves;
    }
}
