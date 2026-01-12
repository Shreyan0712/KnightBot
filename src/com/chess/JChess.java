package com.chess;

import com.chess.engine.ai.Negamax;
import com.chess.engine.ai.MoveStrategy;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

import java.util.Scanner;

public class JChess {

    public static void main(String[] args) {

        Board board = Board.createStandardBoard();
        System.out.println(board);

        // We will make the AI play as Black
        // The search depth (e.g., 6) can be increased for a stronger AI,
        MoveStrategy ai = new Negamax();
        int aiSearchDepth = 6;

        Scanner scanner = new Scanner(System.in);

        // Main Game Loop
        while (!board.currentPlayer().isInCheckMate() && !board.currentPlayer().isInStaleMate()) {

            Player currentPlayer = board.currentPlayer();

            if (currentPlayer.getAlliance().isWhite()) {
                // --- Human's Turn ---
                System.out.println("\n" + currentPlayer.getAlliance() + "'s turn. Enter your move (e.g., e2e4):");
                String moveInput = scanner.nextLine();

                if(moveInput.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting game.");
                    break;
                }

                Move humanMove = parseUserMove(board, moveInput);

                if (humanMove == null) {
                    System.out.println("!!! Invalid move. Try again. (Format: a2a4)");
                    continue;
                }

                // Make the move
                board = board.currentPlayer().makeMove(humanMove).getToBoard();

            } else {
                // --- AI's Turn ---
                System.out.println("\n" + currentPlayer.getAlliance() + "'s turn (AI).");

                // AI calculates the best move
                Move aiMove = ai.execute(board, aiSearchDepth);

                System.out.println("AI plays: " + aiMove);

                // Make the move
                board = board.currentPlayer().makeMove(aiMove).getToBoard();
            }

            // Print the new board state
            System.out.println(board);
        }

        // --- Game Over ---
        scanner.close();
        System.out.println("--- Game Over ---");
        if(board.currentPlayer().isInCheckMate()) {
            System.out.println("Winner: " + board.currentPlayer().getOpponent().getAlliance());
        } else if (board.currentPlayer().isInStaleMate()) {
            System.out.println("Result: Stalemate (Draw)");
        }
    }

    private static Move parseUserMove(Board board, String moveInput) {
        if (moveInput == null || moveInput.length() != 4) {
            return null;
        }

        try {
            String from = moveInput.substring(0, 2);
            String to = moveInput.substring(2, 4);

            int fromCoord = BoardUtils.INSTANCE.getCoordinateAtPosition(from);
            int toCoord = BoardUtils.INSTANCE.getCoordinateAtPosition(to);

            // Check all legal moves for a match
            for (Move move : board.currentPlayer().getLegalMoves()) {
                if (move.getCurrentCoordinate() == fromCoord && move.getDestinationCoordinate() == toCoord) {

                    if (move instanceof Move.PawnPromotion) {
                        if (((Move.PawnPromotion) move).promotionPiece.getPieceType() == Piece.PieceType.QUEEN) {
                            return move;
                        }
                        continue;
                    }

                    return move;
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}