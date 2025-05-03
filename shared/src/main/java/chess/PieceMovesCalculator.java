package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    private final Collection<ChessMove> possibleMoves;
    private final ChessPosition myPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;
    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition){
        this.possibleMoves = new ArrayList<>();
        this.myPosition = myPosition;
        this.board = board;
        this.color = board.getPiece(myPosition).getTeamColor();
    }

    /**
     *
     * @return a boolean. True is the chess piece is in bounds, false if it is not.
     */
    public boolean isOutOfBounds (ChessPosition position){
        return position.getRow() > 8|| position.getRow() < 1 ||
                position.getColumn() > 8 || position.getColumn() < 1;
    }

    /**
     * This function adds possible moves to the possibleMoves object
     * @param rowIterator How much the piece moves in the vertical direction
     * @param colIterator How much the piece moves in the horizonal direction
     * @param maxValue How many times it should iterate (normally 7 for pieces that move the length of the board
     *                 and 2 for pieces that don't)
     *
     */
    public void ultimateCalculator(int rowIterator, int colIterator, int maxValue){
        for (int i = 1; i < maxValue; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() + i * rowIterator, myPosition.getColumn() + i * colIterator);
            if (isOutOfBounds(possiblePosition)) {
                break;
            }
            if (board.getPiece(possiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if (color == board.getPiece(possiblePosition).getTeamColor()) {
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
                break;
            }
        }
    }
    /**
     *
     * @return a collection of possible moves for a bishop
     */
    public Collection<ChessMove> bishopMovesCalculator(){
        ultimateCalculator(1,1,7);
        ultimateCalculator(-1,1,7);
        ultimateCalculator(1,-1,7);
        ultimateCalculator(-1,-1,7);
        return possibleMoves;
    }


    public Collection<ChessMove> rookMovesCalculator(){
        ultimateCalculator(1,0,7);
        ultimateCalculator(0,1,7);
        ultimateCalculator(-1,0,7);
        ultimateCalculator(0,-1,7);
        return possibleMoves;
    }

    public Collection<ChessMove> queenMovesCalculator(){
        ultimateCalculator(1,1,7);
        ultimateCalculator(-1,1,7);
        ultimateCalculator(1,-1,7);
        ultimateCalculator(-1,-1,7);
        ultimateCalculator(1,0,7);
        ultimateCalculator(0,1,7);
        ultimateCalculator(-1,0,7);
        ultimateCalculator(0,-1,7);
        return possibleMoves;
    }

    public Collection<ChessMove> knightMovesCalculator(){
        ultimateCalculator(2,1,2);
        ultimateCalculator(2,-1,2);
        ultimateCalculator(1,2,2);
        ultimateCalculator(1,-2,2);
        ultimateCalculator(-2,1,2);
        ultimateCalculator(-2,-1,2);
        ultimateCalculator(-1,2,2);
        ultimateCalculator(-1,-2,2);
        return possibleMoves;
    }

    public Collection<ChessMove> kingMovesCalculator(){
        ultimateCalculator(1,1,2);
        ultimateCalculator(1,-1,2);
        ultimateCalculator(-1,1,2);
        ultimateCalculator(-1,-1,2);
        ultimateCalculator(1,0,2);
        ultimateCalculator(-1,0,2);
        ultimateCalculator(0,1,2);
        ultimateCalculator(0,-1,2);
        return possibleMoves;
    }
}
