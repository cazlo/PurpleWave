package Information.Battles

import Information.Battles.Types.Battle
import Lifecycle.With
import Mathematics.Pixels.Points

object BattleUpdater {
  
  def run() {
    With.battles.all.foreach(updateBattle)
  }
  
  def updateBattle(battle:Battle) {
    if (battle.happening) {
      battle.groups.foreach(group => {
        val airCentroid = group.units.map(_.pixelCenter).reduce(_.add(_)).divide(group.units.size)
        val hasGround   = group.units.exists( ! _.flying)
        group.centroid  = group.units.filterNot(_.flying && hasGround).minBy(_.pixelDistanceSquared(airCentroid)).pixelCenter
        group.spread    = group.units.map(_.pixelDistanceFast(group.centroid)).sum / group.units.size
      })
    }
    battle.groups.foreach(group =>
      group.vanguard =
        if (battle.happening)
          group.units.minBy(_.pixelDistanceFast(group.opponent.centroid)).pixelCenter
        else
          group.units.headOption.map(_.pixelCenter).getOrElse(Points.middle))
  }
}
