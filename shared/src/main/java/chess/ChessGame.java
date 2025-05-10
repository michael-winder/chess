package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.CheckedInputStream;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private final ChessBoard currentBoard;
    private int turnTracker;
    public ChessGame() {
        currentBoard = new ChessBoard();
        turnTracker = 1;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (turnTracker % 2 == 1){
            return TeamColor.WHITE;
        } else return TeamColor.BLACK;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (team == TeamColor.BLACK){
            turnTracker = 2;
        } else {
            turnTracker = 1;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = currentBoard.getPiece(startPosition);
        Collection<ChessMove> validMoves = piece.pieceMoves(currentBoard,startPosition);
        for (ChessMove : validMoves){
            ChessBoard testBoard = currentBoard.clone();
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves.contains(move)){
            currentBoard.addPiece(move.getEndPosition(),currentBoard.getPiece(move.getStartPosition()));
            currentBoard.addPiece(move.getStartPosition(),null);
        } else {
            throw new InvalidMoveException("That move is invalid");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> opponentMoveDestinations = getOpponentMoveDestinations(teamColor);
        
    }

    /**
     * This acts as a helper function for isInCheck.
     * returns a collection of all possible ending positions of team moves
     */
    public Collection<ChessPosition> getOpponentMoveDestinations(TeamColor teamColor){
        Collection<ChessPosition> opponentMoveDestinations = new ArrayList<>();
        // iterates through the board and gets each piece
        for (int row  = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                ChessPosition piecePosition = new ChessPosition(row,col);
                ChessPiece piece = currentBoard.getPiece(piecePosition);
                // if the piece is not null and is the opposite color, the endPosition of each move is added to teamMoveDestinations
                if (piece != null && piece.getTeamColor() != teamColor){
                    Collection<ChessMove> teamMoves = piece.pieceMoves(currentBoard,piecePosition);
                    for (ChessMove move : teamMoves){
                        opponentMoveDestinations.add(move.getEndPosition());
                    }
                }
            }
        }
        return opponentMoveDestinations;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                ChessPosition boardPosition = new ChessPosition(row,col);
                currentBoard.addPiece(boardPosition,board.getPiece(boardPosition));
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }


}
