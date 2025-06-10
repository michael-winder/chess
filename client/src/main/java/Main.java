import chess.*;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String port;
        if (args.length > 0){
            port = args[0];
        } else {
            port = "8081";
        }
        String url = "http://localhost:" + port;
        new Repl(url).run();
    }
}