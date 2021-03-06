package Micro.Heuristics.Movement

import Debugging.Visualizations.Colors
import Mathematics.Heuristics.HeuristicWeight
import bwapi.Color

class MovementHeuristicWeight (
  heuristic : MovementHeuristic,
  weight    : Double,
  val color : Color = Colors.DefaultGray)

  extends HeuristicWeight(
    heuristic,
    weight)