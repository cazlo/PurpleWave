package Micro.Intent

import Lifecycle.With
import ProxyBwapi.Races.Zerg
import ProxyBwapi.UnitInfo.UnitInfo

object Targets {
  
  val ineligibleClasses = Vector(Zerg.Larva, Zerg.Egg)
  
  def get(intent:Intention):Vector[UnitInfo] = {
    if ( ! intent.unit.canAttackThisSecond) return Vector.empty
    if (intent.unit.battle.isEmpty && intent.unit.unitClass.isWorker) return Vector.empty //Performance shortcut
    With.units.inTileRadius(
      intent.unit.tileIncludingCenter,
      With.configuration.battleMarginTiles)
      .filter(target => valid(intent, target))
      .toVector
  }
  
  def valid(intent:Intention, target:UnitInfo):Boolean = {
    target.likelyStillThere &&
      With.frame - target.lastSeen < 24 * 60 &&
      target.isEnemyOf(intent.unit) &&
      intent.unit.canAttackThisSecond(target) &&
      ! ineligibleClasses.contains(target.unitClass)
  }
  
  def inRange(intent:Intention, target:UnitInfo):Boolean = {
    intent.unit.inRangeToAttackFast(target, With.configuration.microFrameLookahead)
  }
}
