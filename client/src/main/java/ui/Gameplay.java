package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences.*;

import static ui.EscapeSequences.*;


public class Gameplay {
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String[] header = {"  ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
    private static final String[] rows = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
    private static final String[] whitePieces = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ChessBoard board = new ChessBoard();
        out.print(ERASE_SCREEN);

        drawHeaders(out);
        drawChessBoard(out, board);
        drawHeaders(out);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void drawBoard(ChessBoard board) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out);
        drawChessBoard(out, board);
        drawHeaders(out);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {

        setGray(out);
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, header[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print("  ");
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setGray(out);
    }

    private static void drawChessBoard(PrintStream out, ChessBoard board) {
        String[][] boardStrings = pieceConverter(board);
        for (int boardRow = 0; boardRow < 8; ++boardRow) {
            boolean startColor = boardRow % 2 == 1;
            drawChessRow(out, startColor, rows[boardRow], boardStrings[boardRow]);
        }
    }

    private static void drawChessRow(PrintStream out, Boolean startWhite, String row, String[] board) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(row);
        for (int boardCol = 0; boardCol < 8; ++boardCol) {
            if (startWhite == true) {
                setWhite(out);
                if (boardCol % 2 == 0) {
                    setBlack(out);
                }
            } else {
                setBlack(out);
                if (boardCol % 2 == 0) {
                    setWhite(out);
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


    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static String[][] pieceConverter(ChessBoard board) {
        String[][] boardStrings = new String[8][8];
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                boardStrings[row - 1][col - 1] = pieceIntoString(piece);
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
