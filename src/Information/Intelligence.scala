package Information

import Information.Geography.Types.Base
import Performance.Caching.CacheFrame
import Lifecycle.With
import Mathematics.Pixels.Tile

class Intelligence {
  
  def mostBaselikeEnemyTile:Tile = mostBaselikeEnemyTileCache.get
  val mostBaselikeEnemyTileCache = new CacheFrame(() =>
    With.units.enemy
      .toVector
      .filter(unit => unit.possiblyStillThere && ! unit.flying)
      .sortBy(unit => ! unit.unitClass.isBuilding)
      .sortBy(unit => ! unit.unitClass.isTownHall)
      .map(_.tileIncludingCenter)
      .headOption
      .getOrElse(leastScoutedBases.head.townHallArea.midpoint))
  
  def leastScoutedBases:Iterable[Base] = leastScoutedBasesCache.get
  private val leastScoutedBasesCache = new CacheFrame(() =>
    With.geography.bases
      .toVector
      .sortBy( ! _.isStartLocation)
      .sortBy(_.lastScoutedFrame))
}
