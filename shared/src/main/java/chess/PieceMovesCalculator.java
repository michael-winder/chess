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
    public boolean isInBounds (ChessPosition position){
        return position.getRow() <= 7 && position.getRow() >= 0 &&
                position.getColumn() <= 7 && position.getColumn() >= 0;
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
        for(int i = 1; i < 8; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
            if (!isInBounds(possiblePosition)){
                break;
            }
            if (board.getPiece(possiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(possiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            }
        }
        for(int i = 1; i < 8; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
            if (!isInBounds(possiblePosition)){
                break;
            }
            if (board.getPiece(possiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(possiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            }
        }
        for(int i = 1; i < 8; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
            if (!isInBounds(possiblePosition)){
                break;
            }
            if (board.getPiece(possiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(possiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            }
        }
        for(int i = 1; i < 8; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
            if (!isInBounds(possiblePosition)){
                break;
            }
            if (board.getPiece(possiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(possiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            }
        }

        return possibleMoves;
    }
}
