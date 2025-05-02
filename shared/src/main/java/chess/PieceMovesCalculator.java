package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    private final Collection<ChessMove> possibleMoves;
    private final ChessPosition myPosition;
    private final ChessBoard board;
    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition){
        this.possibleMoves = new ArrayList<>();
        this.myPosition = myPosition;
        this.board = board;
    }

    /**
     * @return a collection of possible moves for a pawn
     */
    public Collection<ChessMove> pawnsMovesCalculator(){
        ChessPosition possiblePosition = new ChessPosition(myPosition.getRow(),myPosition.getColumn()+1);
        if(board.getPiece(possiblePosition) == null){
            ChessMove possibleMove = new ChessMove(myPosition,possiblePosition,null);
            possibleMoves.add(possibleMove);
        }
        return possibleMoves;
    }

    /**
     *
     * @return a collection of possible moves for a bishop
     */
    public Collection<ChessMove> bishopMovesCalculator(){
        for(int i = myPosition.getRow(); i <= 8; i++) {
            ChessPosition possiblePosition = new ChessPosition(i, myPosition.getColumn() + 1);
            if (board.getPiece(possiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            }
        }
    }
}
