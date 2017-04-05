package Micro.Heuristics.TargetHeuristics

import Lifecycle.With
import Micro.Intent.Intention
import ProxyBwapi.UnitInfo.UnitInfo
import Utilities.EnrichPosition._

object TargetHeuristicFiringPosition extends TargetHeuristic{
  
  override def evaluate(intent: Intention, candidate: UnitInfo): Double = {
    
    val firingPosition =
      intent.unit.pixelCenter
        .project(
          candidate.pixelCenter,
          Math.max(0, intent.unit.pixelsFromEdge(candidate) - intent.unit.range(candidate)))
        .tileIncluding
    
    val dpsExposure = With.grids.dpsEnemy.get(firingPosition, intent.unit)
    
    dpsExposure
  }
  
}