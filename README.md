# Rubik's Cube Solver (Java)

I built this project to solve a 3x3 Rubik's Cube state from a text file using an IDA* search strategy with pruning and heuristic bounds.

## What this project does

- Reads a cube state from an input text file.
- Runs iterative deepening A* (IDA*) up to a max search depth.
- Uses multiple lower-bound heuristics to prune impossible branches early.
- Writes the move sequence to an output text file.

## How I represent moves

I use face letters:

- `F`, `L`, `R`, `U`, `D`, `B`

I also include repeated turns in the move list:

- `FF` and `FFF`
- `LL` and `LLL`
- `RR` and `RRR`
- `UU` and `UUU`
- `DD` and `DDD`
- `BB` and `BBB`

In code, each repeated sequence is still applied as repeated quarter turns.

## Input format

The cube file must contain 9 lines:

- Lines 1 to 3: Up face, 3 chars each
- Lines 4 to 6: Left + Front + Right + Back in one line, 12 chars total each line
- Lines 7 to 9: Down face, 3 chars each

Color letters expected by this implementation:

- `O` (Up)
- `G` (Left)
- `W` (Front)
- `B` (Right)
- `Y` (Back)
- `R` (Down)

If formatting is invalid, I throw `IncorrectFormatException`.

## How the solver works

### 1) IDA* depth loop

I start from `depth = heuristic(startState)` and increase depth until I find a solution or hit the max depth:

- `MAX_DEPTH = 21`

This is implemented in `solveIDAStar`.

### 2) Depth-limited DFS with pruning

Inside `depthLimitedSearch`, I prune aggressively:

- Stop if solved.
- Stop if depth is 0.
- Stop if `heuristic(state) > remainingDepth`.
- Skip same-face consecutive moves.
- Skip inverse/canceling patterns.

### 3) Heuristic

I combine three lower bounds and take the max:

- sticker misplacement lower bound (`getHeuristicValue` in `RubiksCube`)
- corner lower bound (`cornerManhattan`)
- edge lower bound (`edgeManhattan`)

Using the max gave me stronger pruning than any single bound by itself.

## Hard parts I had to get right

- Move pruning logic in `depthLimitedSearch`: this dramatically affects runtime and correctness.
- Face rotation mappings in `moveF/moveL/moveR/moveU/moveD/moveB`: one indexing mistake breaks solver validity.
- Heuristic balance: I needed a fast enough heuristic that was still informative for IDA*.

## Build and run

From the repo root:

```powershell
javac -d . *.java
java rubikscube.Solver input.txt output.txt
```

Arguments:

1. input cube file path
2. output file path for solution sequence

## Output

- If solved, output file contains the move sequence.
- If no solution is found within max depth, output file is empty.

## Project files

- `Solver.java`: IDA* search, pruning, heuristic composition
- `RubiksCube.java`: cube model, parser, moves, helpers
- `IncorrectFormatException.java`: input validation exception
- `CMPT 225 FINAL PROJECT DOCUMENTATION_.pdf`: project documentation
