package Micro.Heuristics.UnitHeuristics

import Micro.Heuristics.HeuristicMath
import Micro.Intent.Intention
import ProxyBwapi.UnitInfo.UnitInfo

object UnitHeuristicCombat extends UnitHeuristic{
  
  override def evaluate(intent: Intention, candidate: UnitInfo): Double = {
    
    HeuristicMath.unboolify(candidate.unitClass.helpsInCombat)
    
  }
  
}
