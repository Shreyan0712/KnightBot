package com.chess.engine.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public interface MoveStrategy {

    Move execute(Board board, int depth);

}