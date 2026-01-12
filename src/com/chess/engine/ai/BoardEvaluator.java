package com.chess.engine.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

public final class BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;


    /**
     * Evaluates the current board state from the perspective of the *current* player.
     * A positive score is good for the current player, a negative score is bad.
     */
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.currentPlayer(), depth) -
                scorePlayer(board, board.currentPlayer().getOpponent(), depth);
    }

    private int scorePlayer(final Board board,
                            final Player player,
                            final int depth) {
        return pieceValue(player) +
                mobility(player) +
                check(player) +
                checkmate(player, depth) +
                castled(player);
    }

    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static int checkmate(Player player, int depth) {
        // If we find a checkmate, we reward it highly, and more so
        // if it's found at a shallower depth (it's "quicker").
        return player.getOpponent().isInCheckMate() ? CHECKMATE_BONUS * (DEPTH_BONUS - depth) : 0;
    }

    private static int check(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int mobility(final Player player) {
        // A simple mobility score: number of legal moves
        return player.getLegalMoves().size();
    }

    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;
        // Loop over the coordinates (int)
        for (final int pieceCoordinate : player.getActivePieces()) {
            // Get the Piece object from the board using the coordinate
            final Piece piece = player.getBoard().getPiece(pieceCoordinate);
            if (piece != null) {
                pieceValueScore += piece.getPieceValue() + piece.locationBonus();
            }
        }
        return pieceValueScore;
    }
}