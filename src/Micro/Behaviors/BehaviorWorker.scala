package Micro.Behaviors
import Micro.Intent.Intention
import Lifecycle.With

object BehaviorWorker extends Behavior {
  
  def execute(intent: Intention) {
    
    intent.movementProfileNormal = MovementProfiles.worker
  
    if (intent.toBuild.isDefined) {
      return With.commander.build(intent, intent.toBuild.get, intent.destination.get)
    }
  
    if (intent.toGather.isDefined) {
      //TODO: Test this against heuristics when threats exist, so that we don't blindly transfer workers into a fight
      return With.commander.gather(intent, intent.toGather.get)
    }
    
    BehaviorDefault.execute(intent)
  }
}
