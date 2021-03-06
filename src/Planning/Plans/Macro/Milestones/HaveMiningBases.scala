package Planning.Plans.Macro.Milestones

import Lifecycle.With
import Planning.Plan

class HaveMiningBases(requiredBases: Int) extends Plan {
  
  description.set("Require a certain number of bases.")
  
  override def isComplete: Boolean = With.geography.ourBases
    .count(base =>
      base.minerals.size >= 6 &&
      base.mineralsLeft / base.minerals.size > 300) > requiredBases
  
}
