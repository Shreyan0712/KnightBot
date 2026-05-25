# ♞ KnightBot

A Java-based chess engine implementing the **Classical Negamax** search architecture with Shannon-style evaluation — the baseline engine described in the research paper *"Beyond Monolithic Evaluation: A Phase-Aware Multi-Agent Framework for Computer Chess"* (Dhar & Kalla, Manipal University Jaipur).

> **Note:** This repository contains the classical engine. The full **NegamaxMultiAgent** system (phase-aware multi-agent evaluation) described in the paper is not yet included in this public release.

---

## 🧠 How It Works

KnightBot plays chess by combining a **tree search algorithm** with a **static position evaluation function**.

### Search
- **Negamax with Alpha-Beta Pruning** — finds the optimal move by recursively searching the game tree, pruning branches that cannot affect the result
- **Iterative Deepening** — repeatedly searches at increasing depths until the time limit is hit, always returning the best move found so far
- **Transposition Table** (2²⁰ entries, always-replace) — caches previously evaluated positions to avoid redundant computation
- **Killer Move Heuristic** (2 killers per ply) — prioritizes moves that caused beta cutoffs in sibling nodes
- **Quiescence Search** (depth 6) — extends the search at leaf nodes to resolve captures and checks, avoiding the horizon effect

### Evaluation (Shannon-style)
The evaluation function scores positions using fixed-weight features:

| Feature | Description |
|---|---|
| **Material balance** | Pawn=100, Knight/Bishop=300, Rook=500, Queen=900 centipawns |
| **Mobility** | Number of legal moves available to each side |
| **Pawn structure** | Penalizes doubled, isolated, and backward pawns |
| **King safety** | Estimates threat density around the king |

Mate scores are represented as ±100,000 centipawns with overflow guards.

---

## 🏗️ Project Structure

```
KnightBot/
└── src/
    └── com/
        └── chess/          # All engine source code (Java)
```

Built and developed using **IntelliJ IDEA** (Java 17 / Microsoft OpenJDK 17.0.15).

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or later
- IntelliJ IDEA (recommended) or any Java IDE

### Running

1. Clone the repository:
   ```bash
   git clone https://github.com/Shreyan0712/KnightBot.git
   cd KnightBot
   ```

2. Open the project in IntelliJ IDEA (it uses the included `.iml` module file).

3. Build and run the main class from `src/com/chess/`.

---

## 📊 Performance

Results from the research paper comparing KnightBot (Classical) against the NegamaxMultiAgent engine across tournament conditions (60 games each):

| Condition | Classical Result | Notes |
|---|---|---|
| Fixed Depth 3 | **+52.5 ELO** vs MultiAgent | Faster evaluation wins at equal depth |
| 500 ms (blitz) | **+40.7 ELO** vs MultiAgent | Speed advantage dominates |
| 1000 ms (standard) | −46.6 ELO vs MultiAgent | MultiAgent's quality advantage emerges |
| 2000 ms (rapid) | −5.8 ELO vs MultiAgent | Near parity |

At fixed depth, the classical engine explores ~70% more nodes per move (7,557 vs 4,436) thanks to its lightweight evaluator, which outweighs evaluation quality at low time controls.

---

## 📄 Research Paper

This engine is the baseline described in:

> **Shreyan Dhar and Gauransh Kalla**, *"Beyond Monolithic Evaluation: A Phase-Aware Multi-Agent Framework for Computer Chess"*, Department of Data Science, Manipal University Jaipur.

The paper proposes **NegamaxMultiAgent**, an extension that replaces the monolithic evaluation with four specialized agents (Tactical, Positional, Defensive, Opening) whose contributions are dynamically reweighted via a continuous phase coefficient φ ∈ [0, 1] derived from remaining material. Key findings:

- Multi-agent combination alone provides +649 ELO over the best single agent
- Phase-aware weighting adds a further +11.8 ELO over equal weighting
- The full system outperforms this classical engine by **+46.6 ELO** at 1000 ms time control

---

## 🔮 Future Work (from the paper)

- Automatic weight tuning via **Texel tuning** over labelled game corpora
- **Piece-square tables** for positional preferences per board square
- Dynamic disagreement extension thresholds based on position type
- Replacing handcrafted agents with small **NNUE-style** networks while preserving phase-weighted interpretability

---

## 👤 Author

**Shreyan Dhar** — Department of Data Science, Manipal University Jaipur

---

## 📜 License

This project is currently unlicensed. Contact the author for usage permissions.
