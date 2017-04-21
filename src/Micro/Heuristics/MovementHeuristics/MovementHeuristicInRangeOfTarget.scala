package Micro.Heuristics.MovementHeuristics

import Mathematics.Heuristics.HeuristicMath
import Mathematics.Pixels.Pixel
import Micro.Intent.Intention

object MovementHeuristicInRangeOfTarget extends MovementHeuristic {
  
  override def evaluate(intent: Intention, candidate: Pixel): Double = {
  
    if (intent.toAttack.isEmpty) return 1.0
    
    HeuristicMath.fromBoolean(
      intent.toAttack.get.pixelDistanceSquared(candidate) <
      Math.pow(
        intent.unit.unitClass.radialHypotenuse +
        intent.toAttack.get.unitClass.radialHypotenuse +
        intent.unit.unitClass.maxAirGroundRange,
        2))
  }
}
