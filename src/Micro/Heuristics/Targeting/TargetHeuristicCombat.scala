package Micro.Heuristics.Targeting

import Mathematics.Heuristics.HeuristicMathMultiplicative
import Micro.Task.ExecutionState
import ProxyBwapi.UnitInfo.UnitInfo

object TargetHeuristicCombat extends TargetHeuristic {
  
  override def evaluate(state: ExecutionState, candidate: UnitInfo): Double = {
    
    HeuristicMathMultiplicative.fromBoolean(candidate.unitClass.helpsInCombat)
    
  }
}
