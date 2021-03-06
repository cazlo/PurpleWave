package Information.Geography.Calculations

import Information.Geography.Types.{Base, Zone}
import Lifecycle.With
import ProxyBwapi.Races.Protoss
import Utilities.EnrichPixel._

object ZoneUpdater {
  
  def update() = {
    With.geography.zones.foreach(updateZone)
  }
  
  private def updateZone(zone:Zone) {
    zone.bases.foreach(updateBase)
  }
  
  private def updateBase(base:Base) {
    updateTownHall(base)
    updateOwner(base)
    updateResources(base)
  }
  
  private def updateTownHall(base: Base) {
    base.townHall = None
    val townHalls = With.units.buildings
      .filter(unit =>
        unit.unitClass.isTownHall &&
        unit.tileIncludingCenter.zone == base.zone &&
        base.zone.contains(unit.pixelCenter))
    
    if (townHalls.nonEmpty) {
      base.townHall = Some(townHalls.minBy(_.pixelDistanceSquared(base.townHallArea.midPixel)))
    }
  }
  
  private def updateOwner(base: Base) {
    base.zone.owner = base.townHall.map(_.player).getOrElse(With.neutral)
    if (base.zone.owner == With.neutral && base.lastScoutedFrame < With.frame - Protoss.Nexus.buildFrames) {
      With.units.enemy
        .find(unit => ! unit.flying && unit.unitClass.isBuilding && unit.pixelCenter.zone == base.zone)
        .foreach(enemyUnit => base.zone.owner = enemyUnit.player)
    }
  }
  
  private def updateResources(base: Base) {
    base.minerals = With.units.neutral
      .filter(unit => unit.unitClass.isMinerals && base.zone.contains(unit.pixelCenter) && unit.mineralsLeft > 0)
      .toSet
    base.gas = With.units.all
      .filter(unit => unit.unitClass.isGas && base.zone.contains(unit.pixelCenter))
  
    base.mineralsLeft   = base.minerals.filter(_.alive).toVector.map(_.mineralsLeft).sum
    base.gasLeft        = base.gas.filter(_.alive).toVector.map(_.gasLeft).sum
    base.harvestingArea = (Vector(base.townHallArea) ++ (base.minerals ++ base.gas).map(_.tileArea)).boundary
    base.heart          = base.harvestingArea.midpoint
  }
}
