package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    private final Collection<ChessMove> possibleMoves;
    private final ChessPosition myPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;
    private final ChessPiece piece;
    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition){
        this.possibleMoves = new ArrayList<>();
        this.myPosition = myPosition;
        this.board = board;
        this.color = board.getPiece(myPosition).getTeamColor();
        this.piece = board.getPiece(myPosition);
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
    public void ultimateCalculator(int rowIterator, int colIterator, int maxValue, ChessPiece.PieceType promotionPiece){
        for (int i = 1; i < maxValue; i++) {
            ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() + i * rowIterator, myPosition.getColumn() + i * colIterator);
            if (isOutOfBounds(possiblePosition)) {
                break;
            }
            if (board.getPiece(possiblePosition) == null) {
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
                    break;
                }
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, promotionPiece);
                possibleMoves.add(possibleMove);
            } else if (piece.getPieceType() != ChessPiece.PieceType.PAWN && color == board.getPiece(possiblePosition).getTeamColor()) {
                break;
            } else if (color != board.getPiece(possiblePosition).getTeamColor()){
                ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, promotionPiece);
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
        ultimateCalculator(1,1,8,null);
        ultimateCalculator(-1,1,8,null);
        ultimateCalculator(1,-1,8,null);
        ultimateCalculator(-1,-1,8,null);
        return possibleMoves;
    }


    public Collection<ChessMove> rookMovesCalculator(){
        ultimateCalculator(1,0,8,null);
        ultimateCalculator(0,1,8,null);
        ultimateCalculator(-1,0,8,null);
        ultimateCalculator(0,-1,8,null);
        return possibleMoves;
    }

    public Collection<ChessMove> queenMovesCalculator(){
        ultimateCalculator(1,1,8,null);
        ultimateCalculator(-1,1,8,null);
        ultimateCalculator(1,-1,8,null);
        ultimateCalculator(-1,-1,8,null);
        ultimateCalculator(1,0,8,null);
        ultimateCalculator(0,1,8,null);
        ultimateCalculator(-1,0,8,null);
        ultimateCalculator(0,-1,8,null);
        return possibleMoves;
    }

    public Collection<ChessMove> knightMovesCalculator(){
        ultimateCalculator(2,1,2,null);
        ultimateCalculator(2,-1,2,null);
        ultimateCalculator(1,2,2,null);
        ultimateCalculator(1,-2,2,null);
        ultimateCalculator(-2,1,2,null);
        ultimateCalculator(-2,-1,2,null);
        ultimateCalculator(-1,2,2,null);
        ultimateCalculator(-1,-2,2,null);
        return possibleMoves;
    }

    public Collection<ChessMove> kingMovesCalculator(){
        ultimateCalculator(1,1,2,null);
        ultimateCalculator(1,-1,2,null);
        ultimateCalculator(-1,1,2,null);
        ultimateCalculator(-1,-1,2,null);
        ultimateCalculator(1,0,2,null);
        ultimateCalculator(-1,0,2,null);
        ultimateCalculator(0,1,2,null);
        ultimateCalculator(0,-1,2,null);
        return possibleMoves;
    }

    public Collection<ChessMove> pawnMovesCalculator(){
        if (color == ChessGame.TeamColor.WHITE) {
            pawnHelper(2,7,1);
        } else {
            pawnHelper(7,2,-1);
        }
        return possibleMoves;

    }

public void pawnHelper(int startRow, int backRow, int forward){
    ChessPosition possiblePosition = new ChessPosition(myPosition.getRow() + forward, myPosition.getColumn());
    if(myPosition.getRow() == startRow) {
        ChessPosition possiblePosition2 = new ChessPosition(myPosition.getRow() + 2 * forward, myPosition.getColumn());
        if (board.getPiece(possiblePosition) == null && board.getPiece(possiblePosition2) == null) {
            ChessMove possibleMove = new ChessMove(myPosition, possiblePosition2, null);
            possibleMoves.add(possibleMove);
        }
    }
    if (myPosition.getRow() == backRow) {
        if (board.getPiece(possiblePosition) == null && !isOutOfBounds(possiblePosition)){
            ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, ChessPiece.PieceType.QUEEN);
            possibleMoves.add(possibleMove);
            ChessMove possibleMove2 = new ChessMove(myPosition, possiblePosition, ChessPiece.PieceType.ROOK);
            possibleMoves.add(possibleMove2);
            ChessMove possibleMove3 = new ChessMove(myPosition, possiblePosition, ChessPiece.PieceType.KNIGHT);
            possibleMoves.add(possibleMove3);
            ChessMove possibleMove4 = new ChessMove(myPosition, possiblePosition, ChessPiece.PieceType.BISHOP);
            possibleMoves.add(possibleMove4);
        }
        ultimateCalculator(forward,1,2,ChessPiece.PieceType.QUEEN);
        ultimateCalculator(forward,-1,2,ChessPiece.PieceType.QUEEN);
        ultimateCalculator(forward,1,2,ChessPiece.PieceType.ROOK);
        ultimateCalculator(forward,-1,2,ChessPiece.PieceType.ROOK);
        ultimateCalculator(forward,1,2,ChessPiece.PieceType.KNIGHT);
        ultimateCalculator(forward,-1,2,ChessPiece.PieceType.KNIGHT);
        ultimateCalculator(forward,1,2,ChessPiece.PieceType.BISHOP);
        ultimateCalculator(forward,-1,2,ChessPiece.PieceType.BISHOP);
    } else {
        if (board.getPiece(possiblePosition) == null && !isOutOfBounds(possiblePosition)){
            ChessMove possibleMove = new ChessMove(myPosition, possiblePosition, null);
            possibleMoves.add(possibleMove);
        }
        ultimateCalculator(forward,1,2,null);
        ultimateCalculator(forward,-1,2,null);
    }
}

}
