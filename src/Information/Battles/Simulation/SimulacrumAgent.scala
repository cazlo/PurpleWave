package Information.Battles.Simulation

import Information.Battles.Simulation.Construction.{BattleSimulation, BattleSimulationGroup, Simulacrum}
import Information.Battles.Simulation.Strategies.{BattleStrategyWounded, BattleStrategyFocusAirOrGround, BattleStrategyMovement}
import Utilities.EnrichPosition._
import bwapi.Position

class SimulacrumAgent(
  thisUnit  : Simulacrum,
  thisGroup : BattleSimulationGroup,
  thatGroup : BattleSimulationGroup,
  battle    : BattleSimulation) {
  
  val chargingSpeedRatio = 0.75 //What fraction of top speed charging units are likely to get
  val movementFrames = 8
  
  def act() {
    updateFleeing()
    considerFleeing()
    considerAttacking()
    considerCharging()
    considerKiting()
  }
  
  private def updateFleeing() {
    if (thisUnit.readyToMove || thisUnit.readyToAttack && ! thisUnit.fleeing) {
      thisUnit.fleeing ||= thisGroup.strategy.movement == BattleStrategyMovement.Flee
      thisUnit.fleeing ||=
        thisUnit.totalLife <= Math.min(20, thisUnit.unit.unitClass.maxTotalHealth / 3) &&
        (
          thisGroup.strategy.fleeWounded == BattleStrategyWounded.Flee ||
          (
            thisGroup.strategy.fleeWounded == BattleStrategyWounded.FleeRanged &&
            ! thisUnit.unit.melee
          )
        )
      thisUnit.fleeing &&= thatGroup.units.exists(_.unit.canAttackThisSecond(thisUnit.unit))
      thisUnit.fighting &&= ! thisUnit.fleeing
    }
  }
  
  private def considerAttacking() {
    if (thisUnit.readyToAttack && thisUnit.fighting && ! thisUnit.fleeing) {
      doAttack()
    }
  }
  
  private def considerFleeing() {
    if (thisUnit.readyToMove && thisUnit.fleeing) {
      doFlee()
    }
  }
  
  private def considerCharging() {
    if (
      thisUnit.readyToMove &&
      thisUnit.fighting &&
      thisGroup.strategy.movement == BattleStrategyMovement.Charge) {
      doCharge()
    }
  }
  
  private def considerKiting() {
    if (
      thisUnit.readyToMove &&
      thisUnit.fighting &&
      thisGroup.strategy.movement == BattleStrategyMovement.Kite) {
      if (targetsInRange.isEmpty) {
        doCharge()
      }
      else {
        doFlee()
      }
    }
  }
  
  private def doAttack() {
    if (
      thisUnit.readyToAttack &&
      thisUnit.fighting &&
      targetsInRange.nonEmpty) {
      val target =
        if (thisGroup.strategy.focusAirOrGround == BattleStrategyFocusAirOrGround.Air) {
          val flyersInRange = targetsInRange.filter(_.unit.flying)
          if (flyersInRange.nonEmpty) lowestHealthTarget(flyersInRange) else lowestHealthTarget(targetsInRange)
        }
        else if (thisGroup.strategy.focusAirOrGround == BattleStrategyFocusAirOrGround.Ground) {
          val groundInRange = targetsInRange.filterNot(_.unit.flying)
          if (groundInRange.nonEmpty) lowestHealthTarget(groundInRange) else lowestHealthTarget(targetsInRange)
        }
        else {
          lowestHealthTarget(targetsInRange)
        }
    
      dealDamage(target)
    }
  }
  
  private def lowestHealthTarget(targets:Iterable[Simulacrum]):Simulacrum = {
    targets.minBy(_.totalLife)
  }
  
  private def doCharge() {
    if (targets.nonEmpty) {
      val target = targets.minBy(_.pixel.getDistance(thisUnit.pixel))
      moveTowards(target.pixel)
    }
  }
  
  private def doFlee() {
    val threats = thatGroup.units.filter(_.unit.canAttackThisSecond(thisUnit.unit))
    if (threats.nonEmpty) {
      val closestThreat = threats.minBy(threat =>
        Math.min(
          threat.pixel.getDistance(thisUnit.pixel),
          Math.max(0, threat.pixel.project(thisUnit.pixel, threat.unit.rangeAgainst(thisUnit.unit)).getDistance(thisUnit.pixel))))
      moveAwayFrom(closestThreat.pixel)
    }
  }
  
  private def dealDamage(target:Simulacrum) {
    val damage = thisUnit.unit.damageAgainst(target.unit, target.shields)
    thisUnit.attackCooldown = thisUnit.unit.cooldownAgainst(target.unit)
    thisUnit.moveCooldown = Math.min(thisUnit.attackCooldown, 8)
    target.shields -= damage
    if (target.shields < 0) {
      target.hitPoints += target.shields
      target.shields = 0
    }
  }
  
  private def moveAwayFrom(destination:Position) {
    move(destination, -1.0)
  }
  
  private def moveTowards(destination:Position) {
    move(destination, chargingSpeedRatio)
  }
  
  private def move(destination:Position, multiplier:Double) {
    thisUnit.pixel = thisUnit.pixel.project(destination, multiplier * thisUnit.unit.topSpeed * (1 + movementFrames))
    thisUnit.attackCooldown = movementFrames
    thisUnit.moveCooldown = movementFrames
  }
  
  private lazy val targets = thatGroup.units.filter(defender => thisUnit.unit.canAttackThisSecond(defender.unit))
  private lazy val targetsInRange =
    targets.filter(target =>
      thisUnit.unit.rangeAgainst(target.unit) >=
        thisUnit.pixel.getDistance(target.pixel)
  )
}