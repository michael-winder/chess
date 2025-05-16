package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
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
        currentBoard.resetBoard();
        turnTracker = 1;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (turnTracker % 2 == 1){
            return TeamColor.WHITE;
        } else {
            return TeamColor.BLACK;
        }
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
        if (piece == null){
            return new ArrayList<>();
        }
        Collection<ChessMove> validMoves = piece.pieceMoves(currentBoard,startPosition);
        Collection<ChessMove> invalidMoves = new ArrayList<>();
        TeamColor color = piece.getTeamColor();
        for (ChessMove move : validMoves){
            ChessBoard testBoard = currentBoard.clone();
            testBoard.addPiece(move.getEndPosition(),testBoard.getPiece(move.getStartPosition()));
            testBoard.addPiece(move.getStartPosition(),null);
            Collection<ChessPosition> opponentMoveDestinations = teamMovePositions(color, testBoard, true,false);
            ChessPosition kingPosition = testBoard.getKing(color);
            if(opponentMoveDestinations.contains(kingPosition)){
                invalidMoves.add(move);
            }
        }
        for (ChessMove invalidMove : invalidMoves){
            validMoves.remove(invalidMove);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPiece piece = currentBoard.getPiece(move.getStartPosition());
        ChessPiece endPiece;
        if (piece == null || piece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("That move is invalid");
        }
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (move.getEndPosition().getRow() == 8 || move.getEndPosition().getRow() == 1)){
            ChessPiece.PieceType movedPiece = move.getPromotionPiece();
            endPiece = new ChessPiece(piece.getTeamColor(), movedPiece);
        } else{
            endPiece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
        }
        if (validMoves.contains(move)){
            currentBoard.addPiece(move.getEndPosition(),endPiece);
            currentBoard.addPiece(move.getStartPosition(),null);
            turnTracker++;
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
        Collection<ChessPosition> opponentMoveDestinations = teamMovePositions(teamColor,currentBoard,true,false);
        ChessPosition kingPosition = currentBoard.getKing(teamColor);
        return opponentMoveDestinations.contains(kingPosition);
    }

    /**
     * This acts as a helper function for isInCheck.
     * @return a collection of all possible ending positions of team moves
     * @param teamColor pass in a team color
     * @param board pass in the board you would like to test on
     * @param opponent true if you would like the opponent's moves, false if you would like teamColor's moves
     * @param start If true, the function will return the start positions instead. False will return end positions
     */
    public Collection<ChessPosition> teamMovePositions(TeamColor teamColor,ChessBoard board, Boolean opponent, Boolean start){
        Collection<ChessPosition> teamMovePositions = new ArrayList<>();
        // iterates through the board and gets each piece
        if (opponent == true){
            if (teamColor == TeamColor.WHITE){
                teamColor = TeamColor.BLACK;
            } else{
                teamColor = TeamColor.WHITE;
            }
        }
        for (int row  = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                ChessPosition piecePosition = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(piecePosition);
                // if the piece is not null and is the opposite color, the endPosition of each move is added to teamMoveDestinations
                if (piece != null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> teamMoves = piece.pieceMoves(board,piecePosition);
                    for (ChessMove move : teamMoves){
                        if (start == true){
                            teamMovePositions.add(move.getStartPosition());
                        } else {
                            teamMovePositions.add(move.getEndPosition());
                        }
                    }
                }
            }
        }
        return teamMovePositions;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor) && validMoves(currentBoard.getKing(teamColor)).isEmpty()){
            Collection<ChessPosition> teamMoves = teamMovePositions(teamColor,currentBoard,false,true);
            Collection<ChessMove> allValidMoves = new ArrayList<>();
            if (teamMoves.isEmpty()){
                return true;
            }
            for (ChessPosition position : teamMoves){
                allValidMoves.addAll(validMoves(position));
            }
            return allValidMoves.isEmpty();
        }
        else return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessPosition> teamMoves = teamMovePositions(teamColor,currentBoard,false,true);
        Collection<ChessMove> validTeamMoves = new ArrayList<>();
        for (ChessPosition startPosition : teamMoves){
            validTeamMoves.addAll(validMoves(startPosition));
        }
        ChessPosition king = currentBoard.getKing(teamColor);
        return (validMoves(king).isEmpty() && validTeamMoves.isEmpty() && !isInCheck(teamColor));
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turnTracker == chessGame.turnTracker && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentBoard, turnTracker);
    }
}
