package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import chess.*;

import static ui.EscapeSequences.*;


public class BoardDrawer {
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String[] WHITE_HEADER = {"  ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
    private static final String[] BLACK_HEADER = {"  ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
    private static final String[] BLACK_ROWS = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
    private static final String[] WHITE_ROWS = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};

    public static void drawBoard(ChessBoard board, ChessGame.TeamColor color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out, color);
        drawChessBoard(out, board, color, new ArrayList<>());
        drawHeaders(out, color);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void drawPossibleMoves(ChessBoard board, ChessGame.TeamColor color, ArrayList<ChessPosition> endPositions){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out, color);
        drawChessBoard(out, board, color, endPositions);
        drawHeaders(out, color);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, ChessGame.TeamColor color) {
        String[] header;
        if (color == ChessGame.TeamColor.BLACK){
            header = BLACK_HEADER;
        } else {
            header = WHITE_HEADER;
        }
        setGray(out);
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            printHeaderText(out, header[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print("  ");
            }
        }

        out.println();
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setGray(out);
    }

    private static void drawChessBoard(PrintStream out, ChessBoard board, ChessGame.TeamColor color, ArrayList<ChessPosition> endPositions) {
        String[] rows;
        if (color == ChessGame.TeamColor.BLACK){
            rows = BLACK_ROWS;
        } else {
            rows = WHITE_ROWS;
        }
        boolean startColor;
        String[][] boardStrings = pieceConverter(board, color);
        for (int boardRow = 0; boardRow < 8; ++boardRow) {
            startColor = boardRow % 2 == 1;
            drawChessRow(out, startColor, rows[boardRow], boardStrings[boardRow], endPositions, color);
        }
    }

    private static void drawChessRow(PrintStream out, Boolean startWhite, String row, String[] board,
                                     ArrayList<ChessPosition> endPositions, ChessGame.TeamColor color) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(row);
        int boardRow = Integer.parseInt(row.trim());
        for (int boardCol = 0; boardCol < 8; ++boardCol) {
            if (startWhite == true) {
                setWhite(out);
                if (isPossibleMove(boardRow, boardCol, color, endPositions)){
                    setGreen(out);
                }
                if (boardCol % 2 == 0) {
                    setBlack(out);
                    if (isPossibleMove(boardRow, boardCol, color, endPositions)){
                        setDarkGreen(out);
                    }
                }
            } else {
                setBlack(out);
                if (isPossibleMove(boardRow, boardCol, color, endPositions)){
                    setDarkGreen(out);
                }
                if (boardCol % 2 == 0) {
                    setWhite(out);
                    if (isPossibleMove(boardRow, boardCol, color, endPositions)){
                        setGreen(out);
                    }
                }
            }
            out.print(board[boardCol]);
        }
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(row);
        setGray(out);
        out.println();
    }

    public static boolean isPossibleMove(int row, int col, ChessGame.TeamColor color, ArrayList<ChessPosition> endPositions){
        if (color == ChessGame.TeamColor.BLACK){
            ChessPosition position = new ChessPosition(row, 8 - col);
            return endPositions.contains(position);
        } else {
            ChessPosition position = new ChessPosition(row, col + 1);
            return endPositions.contains(position);
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static String[][] pieceConverter(ChessBoard board, ChessGame.TeamColor color) {
        String[][] boardStrings = new String[8][8];

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (color == ChessGame.TeamColor.BLACK) {
                    boardStrings[row - 1][8 - col] = pieceIntoString(piece);
                } else {
                    boardStrings[8 - row][col - 1] = pieceIntoString(piece);
                }

            }
        }

        return boardStrings;
    }

    private static String pieceIntoString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_ROOK;
            } else {
                return BLACK_ROOK;
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_QUEEN;
            } else {
                return BLACK_QUEEN;
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_KNIGHT;
            } else {
                return BLACK_KNIGHT;
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_BISHOP;
            } else {
                return BLACK_BISHOP;
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_KING;
            } else {
                return BLACK_KING;
            }
        } else {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_PAWN;
            } else {
                return BLACK_PAWN;
            }
        }
    }
}
