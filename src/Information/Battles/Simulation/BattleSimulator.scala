package Information.Battles.Simulation

import Information.Battles.Battle
import Information.Battles.Simulation.Construction.{BattleSimulation, BattleSimulationBuilder, BattleSimulationGroup, Simulacrum}

object BattleSimulator {
  
  var frame = 0
  val maxFrames = 24 * 8
  
  // Loosely, based on the values in the BOSS paper:
  // http://www.aaai.org/ocs/index.php/AIIDE/AIIDE11/paper/viewFile/4078/4407
  def costPerSecondOfNotMining(workers:Int):Int = workers
  
  def simulate(battle:Battle):Iterable[BattleSimulation] = {
    val simulations = BattleSimulationBuilder.build(battle)
    simulations.foreach(runSimulation)
    simulations
  }
  
  private def runSimulation(simulation: BattleSimulation) {
    for(frame <- 0 until maxFrames)
      if (simulation.us.units.nonEmpty && simulation.enemy.units.nonEmpty)
        step(simulation)
  }
  
  private def step(battle: BattleSimulation) {
    updateAgents    (battle, battle.us,     battle.enemy)
    updateAgents    (battle, battle.enemy,  battle.us)
    removeDeadUnits (battle.us)
    removeDeadUnits (battle.enemy)
    reduceCooldown  (battle.us)
    reduceCooldown  (battle.enemy)
    costOfWar       (battle.us)
    costOfWar       (battle.enemy)
    frame += 1
  }
  
  private def updateAgents (
    battle    : BattleSimulation,
    thisGroup : BattleSimulationGroup,
    thatGroup : BattleSimulationGroup) {
    thisGroup.units
      .filter(thisUnit => thisUnit.readyToAttack || thisUnit.readyToMove)
      .foreach(thisUnit => new SimulacrumAgent(thisUnit, thisGroup, thatGroup, battle).act)
  }
  
  private def removeDeadUnits(group:BattleSimulationGroup) {
    group.units.filterNot(_.alive).foreach(deadUnit => {
      group.lostValue += value(deadUnit)
      group.units -= deadUnit
    })
  }
  
  private def value(unit:Simulacrum):Int = {
    2 * unit.unit.unitClass.mineralPrice +
    3 * unit.unit.unitClass.gasPrice +
    (if (unit.unit.unitClass.worker) 50 else 0)
  }
  
  private def reduceCooldown(group:BattleSimulationGroup) {
    group.units.foreach(simulacrum => {
      simulacrum.attackCooldown = Math.max(0, simulacrum.attackCooldown - 1)
      simulacrum.moveCooldown   = Math.max(0, simulacrum.moveCooldown   - 1)
    })
  }
  
  private def costOfWar(group:BattleSimulationGroup) {
    if (frame % 24 == 0) group.lostValue += group.lostValuePerSecond
  }
}