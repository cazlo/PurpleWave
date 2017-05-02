package Micro.Heuristics.Targeting

import Mathematics.Heuristics.HeuristicMath
import Micro.Intent.Intention
import ProxyBwapi.UnitInfo.UnitInfo

object TargetHeuristicDistraction extends TargetHeuristic{
  
  override def evaluate(intent: Intention, candidate: UnitInfo): Double = {
  
    if (intent.destination.isEmpty) return 1.0
  
    HeuristicMath.fromBoolean(
      candidate.pixelDistanceTravelling(intent.destination.get) >
      intent.unit.pixelDistanceTravelling(intent.destination.get))
  }
}