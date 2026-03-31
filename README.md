# AI Search Algorithm Lab

"Play. Learn. Visualize AI."

Welcome to the AI Search Algorithm Lab! This Android application is designed for educational purposes, teaching users how various classical Artificial Intelligence search algorithms work by solving well-known puzzles.

## Features

- **Interactive Games:** Play classic AI problems manually:
  - 8-Puzzle
  - Water Jug Problem
  - N-Queens Problem
  - Demon and Monk River Crossing
- **Algorithm Visualizer (Teacher Mode):** Watch AI solve these problems step-by-step.
  - See the Search Tree drawn in real-time.
  - Pause, step, and resume execution.
- **Side-by-Side Comparison:** Race two algorithms simultaneously to see which one explores fewer nodes and runs faster.
- **Leaderboard:** Track the best performances (least cost, fewest nodes explored) using a local Room Database.
- **Modern UI:** Designed with Material Design 3 and Glassmorphism themes for a beautiful user experience.

## Architecture

This project is built using Android's modern architecture components:

- **MVVM Pattern:** The application logic is separated into Models, Views, and ViewModels.
- **Navigation Component:** Single Activity architecture with Android Jetpack Navigation handling fragment transactions (`nav_graph.xml`).
- **Room Database:** Used for the local Leaderboard, storing algorithm performance results via Entities, DAOs, and Repositories.
- **Custom Views:** The visual search tree is rendered using a custom Android View (`AlgorithmGraphView`) with custom Canvas drawing.

## Algorithms Implemented

The application abstracts search algorithms using a generic `SearchAlgorithm` base class and `SearchState` / `SearchNode` structure.

The following algorithms are implemented:
1. **Breadth-First Search (BFS):** Explores all neighbors at the present depth before moving to nodes at the next depth level. Guarantees the shortest path for unweighted graphs.
2. **Depth-First Search (DFS):** Explores as far as possible along each branch before backtracking. Does not guarantee the optimal path.
3. **Uniform Cost Search (UCS):** Explores nodes with the lowest path cost from the root. Guarantees the optimal path in weighted graphs.
4. **Greedy Best-First Search:** Explores nodes that appear closest to the goal according to a heuristic function. Does not guarantee optimal path but is very fast.
5. **A* Search:** Combines cost-so-far (UCS) and estimated-cost-to-goal (Greedy Search) to find the shortest path efficiently. Guarantees optimal path if the heuristic is admissible.
6. **Hill Climbing:** Local search algorithm that continuously moves in the direction of increasing value/decreasing cost. Can get stuck in local optima.

## Getting Started

1. Open the project in Android Studio (built for Java 17).
2. Build and run the app on an Android Emulator or physical device (Min SDK 24, Target SDK 34).
3. Select a game from the Home screen to play it.
4. Navigate to the Visualizer tab, select a game and algorithm, and click "Play" to watch the algorithm run.
5. Use the Compare tab to race algorithms against each other.
6. View historical best runs in the Leaderboard tab.

## License
Educational Use Only.
