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
        for(int i = 1; i < 7; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
            if (isOutOfBounds(possiblePosition)){
                break;
            }
            //The getPossiblePosition object is needed, because the Chessboard goes from 0-7, rather than 1-8
            ChessPosition getPossiblePosition = new ChessPosition(possiblePosition.getRow() - 1, possiblePosition.getColumn()-1);
            if (board.getPiece(getPossiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(getPossiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
                break;
            }
        }
        for(int i = 1; i < 7; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
            if (isOutOfBounds(possiblePosition)){
                break;
            }
            ChessPosition getPossiblePosition = new ChessPosition(possiblePosition.getRow() - 1, possiblePosition.getColumn()-1);
            if (board.getPiece(getPossiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(getPossiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
                break;
            }
        }
        for(int i = 1; i < 7; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
            if (isOutOfBounds(possiblePosition)){
                break;
            }
            ChessPosition getPossiblePosition = new ChessPosition(possiblePosition.getRow() - 1, possiblePosition.getColumn()-1);
            if (board.getPiece(getPossiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(getPossiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
                break;
            }
        }
        for(int i = 1; i < 7; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
            if (isOutOfBounds(possiblePosition)){
                break;
            }
            ChessPosition getPossiblePosition = new ChessPosition(possiblePosition.getRow() - 1, possiblePosition.getColumn()-1);
            if (board.getPiece(getPossiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(getPossiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
                break;
            }
        }

        return possibleMoves;
    }


    public Collection<ChessMove> rookMovesCalculator(){
        for(int i = 1; i < 7; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() , myPosition.getColumn() + i);
            if (isOutOfBounds(possiblePosition)){
                break;
            }
            //The getPossiblePosition object is needed, because the Chessboard goes from 0-7, rather than 1-8
            ChessPosition getPossiblePosition = new ChessPosition(possiblePosition.getRow() - 1, possiblePosition.getColumn()-1);
            if (board.getPiece(getPossiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(getPossiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
                break;
            }
        }
        for(int i = 1; i < 7; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
            if (isOutOfBounds(possiblePosition)){
                break;
            }
            ChessPosition getPossiblePosition = new ChessPosition(possiblePosition.getRow() - 1, possiblePosition.getColumn()-1);
            if (board.getPiece(getPossiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(getPossiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
                break;
            }
        }
        for(int i = 1; i < 7; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
            if (isOutOfBounds(possiblePosition)){
                break;
            }
            ChessPosition getPossiblePosition = new ChessPosition(possiblePosition.getRow() - 1, possiblePosition.getColumn()-1);
            if (board.getPiece(getPossiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(getPossiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
                break;
            }
        }
        for(int i = 1; i < 7; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn());
            if (isOutOfBounds(possiblePosition)){
                break;
            }
            ChessPosition getPossiblePosition = new ChessPosition(possiblePosition.getRow() - 1, possiblePosition.getColumn()-1);
            if (board.getPiece(getPossiblePosition) == null) {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
            } else if(color == board.getPiece(getPossiblePosition).getTeamColor()){
                break;
            } else {
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
                possibleMoves.add(possibleMove);
                break;
            }
        }

        return possibleMoves;
    }
}
