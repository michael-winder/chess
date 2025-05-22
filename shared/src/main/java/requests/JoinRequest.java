package requests;

import chess.ChessGame;

public record JoinRequest (ChessGame.TeamColor color, Integer gameID){ }
