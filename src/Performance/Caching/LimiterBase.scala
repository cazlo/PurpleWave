package Performance.Caching

import Lifecycle.With

abstract class LimiterBase(action:() => Unit) {
  
  private var nextAction = 0
  
  def act() {
    if (With.frame >= nextAction) {
      action()
      nextAction = With.frame + frameDelay
    }
  }
  
  protected def frameDelay:Int
}
