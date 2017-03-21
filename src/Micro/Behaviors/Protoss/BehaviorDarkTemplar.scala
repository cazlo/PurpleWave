package Micro.Behaviors.Protoss

import Micro.Behaviors.{Behavior, BehaviorDefault, MovementProfiles}
import Micro.Intentions.Intention

object BehaviorDarkTemplar extends Behavior {
  
  def execute(intent: Intention) {
    intent.movementProfileCombat = MovementProfiles.darkTemplar
    intent.movementProfileNormal = MovementProfiles.darkTemplar
  
    BehaviorDefault.execute(intent)
  }
}