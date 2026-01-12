package com.chess.engine.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.board.MoveUtils;

public class Negamax implements com.chess.engine.ai.MoveStrategy {

    private final com.chess.engine.ai.BoardEvaluator evaluator;
    private int boardsEvaluated;

    public Negamax() {
        this.evaluator = new com.chess.engine.ai.BoardEvaluator();
        this.boardsEvaluated = 0;
    }

    @Override
    public Move execute(Board board, int depth) {
        System.out.println("AI is thinking with depth " + depth);
        this.boardsEvaluated = 0;
        final long startTime = System.currentTimeMillis();

        Move bestMove = Move.MoveFactory.getNullMove();
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        // Sort moves to improve alpha-beta pruning efficiency (try best moves first)
        for (final Move move : MoveUtils.MoveSorter.STANDARD.sort(board.currentPlayer().getLegalMoves())) {

            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if (moveTransition.getMoveStatus().isDone()) {
                // Switch to minimizing (hence the negative)
                currentValue = -negamax(moveTransition.getToBoard(), depth - 1, -lowestSeenValue, -highestSeenValue);

                if (board.currentPlayer().getAlliance().isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.printf("%s selected %s, Boards Evaluated: %d, Time: %d ms%n",
                board.currentPlayer(), bestMove, this.boardsEvaluated, executionTime);
        return bestMove;
    }

    private int negamax(final Board board, final int depth, int alpha, int beta) {
        this.boardsEvaluated++;

        if (depth == 0) {
            return quiescence(board, alpha, beta);
        }

        if (board.currentPlayer().isInCheckMate()
                || board.currentPlayer().isInStaleMate()) {
            return this.evaluator.evaluate(board, depth);
        }


        int max = Integer.MIN_VALUE;

        for (final Move move : MoveUtils.MoveSorter.STANDARD.sort(board.currentPlayer().getLegalMoves())) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {

                final int score = -negamax(moveTransition.getToBoard(), depth - 1, -beta, -alpha);

                max = Math.max(score, max);
                alpha = Math.max(alpha, score);

                if (alpha >= beta) {
                    break; // Beta cut-off
                }
            }
        }
        return max;
    }
    private int quiescence(final Board board, int alpha, int beta) {

        // Count this as a searched position
        this.boardsEvaluated++;

        // Stand-pat evaluation (current position as-is)
        int standPat = this.evaluator.evaluate(board, 0);

        // Alpha–beta logic
        if (standPat >= beta) {
            return beta;
        }
        if (alpha < standPat) {
            alpha = standPat;
        }

        // Generate ONLY tactical moves (captures)
        for (final Move move : MoveUtils.getQuiescenceMoves(board.currentPlayer())) {

            final MoveTransition moveTransition =
                    board.currentPlayer().makeMove(move);

            if (moveTransition.getMoveStatus().isDone()) {

                int score = -quiescence(
                        moveTransition.getToBoard(),
                        -beta,
                        -alpha
                );

                if (score >= beta) {
                    return beta;
                }
                if (score > alpha) {
                    alpha = score;
                }
            }
        }

        return alpha;
    }

}