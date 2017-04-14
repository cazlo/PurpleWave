package Information.Battles

import Information.Battles.Types.{Battle, BattleGroup}
import Information.Geography.Types.Zone
import Lifecycle.With
import Mathematics.Pixels.Tile
import Mathematics.Shapes.Circle
import ProxyBwapi.UnitInfo.{ForeignUnitInfo, FriendlyUnitInfo, UnitInfo}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class BattleClassifier {
  
  private var combatantsOurs  : Set[FriendlyUnitInfo] = Set.empty
  private var combatantsEnemy : Set[ForeignUnitInfo]  = Set.empty
  
  var global  : Battle                = null
  var byZone  : Map[Zone, Battle]     = Map.empty
  var byUnit  : Map[UnitInfo, Battle] = Map.empty
  var local   : Vector[Battle]        = Vector.empty
  
  def all:Traversable[Battle] = local ++ byZone.values :+ global
  
  def classify() {
    combatantsOurs  = With.units.ours .filter(unit => unit.unitClass.helpsInCombat)
    combatantsEnemy = With.units.enemy.filter(unit => unit.unitClass.helpsInCombat && unit.possiblyStillThere)
    replaceBattleGlobal()
    replaceBattleByZone()
    replaceBattlesLocal()
    all.foreach(BattleUpdater.updateBattle)
  }
  
  private def replaceBattleGlobal() {
    val oldGlobal = global
    global = new Battle(
      new BattleGroup(upcastOurs(combatantsOurs)),
      new BattleGroup(upcastEnemy(combatantsEnemy)))
    if (oldGlobal != null) {
      adoptMetrics(oldGlobal, global)
    }
  }
  
  private def replaceBattleByZone() {
    val combatantsOursByZone  = combatantsOurs .groupBy(_.tileIncludingCenter.zone)
    val combatantsEnemyByZone = combatantsEnemy.groupBy(_.tileIncludingCenter.zone)
    val oldByZone = byZone
    byZone = With.geography.zones
      .map(zone => (
        zone,
        new Battle(
          new BattleGroup(upcastOurs  (combatantsOursByZone .getOrElse(zone, Set.empty))),
          new BattleGroup(upcastEnemy (combatantsEnemyByZone.getOrElse(zone, Set.empty)))
        )))
      .toMap
    
    if (oldByZone.nonEmpty) {
      With.geography.zones.foreach(zone => adoptMetrics(oldByZone(zone), byZone(zone)))
    }
  }
  
  private def replaceBattlesLocal() {
    if (combatantsEnemy.isEmpty) {
      byUnit = Map.empty
      return
    }
    val localNew = buildBattlesLocal
    localNew.foreach(adoptExistingLocalBattleMetrics)
    local = localNew
    byUnit = local
      .flatten(battle =>
        Vector(
          battle.us.units,
          battle.enemy.units)
        .flatten
        .map(unit => (unit, battle)))
      .toMap
  }
  
  private def adoptExistingLocalBattleMetrics(battle:Battle) {
    val existingBattles = (battle.us.units ++ battle.enemy.units).groupBy(byUnit.get).filter(_._1.nonEmpty)
    if (existingBattles.nonEmpty) {
      adoptMetrics(existingBattles.maxBy(_._2.size)._1.get, battle)
    }
  }
  
  private def buildBattlesLocal:Vector[Battle] = {
    val clusters = clusterUnits()
    clusters
      .map(cluster =>
        new Battle(
          new BattleGroup(cluster.filter(_.isOurs).toSet),
          new BattleGroup(cluster.filter(_.isEnemy).toSet)))
      .filter(battle =>
        battle.us.units.nonEmpty &&
          battle.enemy.units.nonEmpty)
      .toVector
  }
  
  private def clusterUnits():ArrayBuffer[ArrayBuffer[UnitInfo]] = {
  
    val unassignedUnits = mutable.HashSet.empty ++ combatantsOurs ++ combatantsEnemy
    
    val clusters = new ArrayBuffer[ArrayBuffer[UnitInfo]]
  
    val exploredTiles = new mutable.HashSet[Tile]
    val horizonTiles  = new mutable.HashSet[Tile]
  
    while (unassignedUnits.nonEmpty) {
    
      val firstUnit = unassignedUnits.head
      val nextCluster = new ArrayBuffer[UnitInfo]
      unassignedUnits  -= firstUnit
      nextCluster += firstUnit
      horizonTiles.add(firstUnit.tileIncludingCenter)
    
      while (horizonTiles.nonEmpty) {
      
        val nextTile = horizonTiles.head
        horizonTiles  -= nextTile
        exploredTiles += nextTile
      
        val nextUnits = With.grids.units.get(nextTile).filter(_ != firstUnit)
      
        if (nextUnits.nonEmpty) {
          unassignedUnits  --= nextUnits
          nextCluster ++= nextUnits
          horizonTiles ++=
            Circle.points(With.configuration.combatEvaluationDistanceTiles)
              .map(nextTile.add)
              .filter(tile =>
                tile.valid &&
                  ! exploredTiles.contains(tile) &&
                  With.grids.units.get(tile).nonEmpty)
        }
      }
    
      clusters.append(nextCluster)
    }
    
    clusters
  }
  
  def adoptMetrics(oldBattle:Battle, newBattle:Battle) {
    newBattle.us.strength    = oldBattle.us.strength
    newBattle.enemy.strength = oldBattle.enemy.strength
    newBattle.simulations    = oldBattle.simulations
  }

  def upcastOurs  (units:Set[FriendlyUnitInfo]) : Set[UnitInfo] = units.map(_.asInstanceOf[UnitInfo])
  def upcastEnemy (units:Set[ForeignUnitInfo])  : Set[UnitInfo] = units.map(_.asInstanceOf[UnitInfo])
}