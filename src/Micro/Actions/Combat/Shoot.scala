package Micro.Actions.Combat

import Micro.Actions.Action
import Micro.Actions.Commands.Attack
import Micro.Heuristics.Targeting.EvaluateTargets
import Micro.Task.ExecutionState

object Shoot extends Action {
  
  override def allowed(state:ExecutionState): Boolean = {
    state.canAttack &&
    state.toAttack.isEmpty &&
    state.unit.canAttackThisFrame &&
    state.targetsInRange.nonEmpty
  }
  
  override def perform(state:ExecutionState) {
    state.toAttack = EvaluateTargets.best(state, state.targetsInRange.filter(_.canAttackThisSecond(state.unit)))
    Attack.delegate(state)
  }
}
