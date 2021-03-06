package Planning.Plans.GamePlans.Protoss

import Planning.Plans.Army.{Defend, WorkersDefend}
import Planning.Plans.Compound.Parallel
import Planning.Plans.Information.SwitchEnemyRace
import Planning.Plans.Macro.Automatic.Gather
import Planning.Plans.Macro.BuildOrders.FollowBuildOrder
import Planning.Plans.Macro.Expansion.RemoveMineralBlockAt

class ProtossGamePlan extends Parallel {
  children.set(Vector(
    new SwitchEnemyRace {
      terran  .set(new ProtossVsTerran)
      protoss .set(new ProtossVsProtoss)
      zerg    .set(new ProtossVsZerg)
      random  .set(new ProtossVsRandom)
    },
    new FollowBuildOrder,
    new RemoveMineralBlockAt(50),
    new WorkersDefend,
    new Gather,
    new Defend
  ))
}
