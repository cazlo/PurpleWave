package Planning.Plans.GamePlans.Protoss

import Macro.BuildRequests.{BuildRequest, RequestUnitAtLeast}
import Planning.Composition.UnitMatchers.UnitMatchType
import Planning.Plans.Army.Defend
import Planning.Plans.Compound.{IfThenElse, Parallel}
import Planning.Plans.Information.ScoutAt
import Planning.Plans.Macro.Automatic.Continuous.{BuildPylonsContinuously, TrainGatewayUnitsContinuously, TrainProbesContinuously}
import Planning.Plans.Macro.BuildOrders.Build
import Planning.Plans.Macro.Milestones.UnitsExactly
import ProxyBwapi.Races.Protoss

class ProtossVsRandom extends Parallel {
  
  description.set("Protoss vs Random")
  
  private val safeBuild = Vector[BuildRequest] (
    RequestUnitAtLeast(1,   Protoss.CyberneticsCore),
    RequestUnitAtLeast(1,   Protoss.Assimilator),
    RequestUnitAtLeast(4,   Protoss.Gateway)
  )
  
  children.set(Vector(
    new Build(ProtossBuilds.OpeningTwoGate1012),
    new IfThenElse(
      new UnitsExactly(0, UnitMatchType(Protoss.CyberneticsCore)),
      new Build(ProtossBuilds.OpeningTwoGate1012Zealots)
    ),
    new BuildPylonsContinuously,
    new TrainProbesContinuously,
    new TrainGatewayUnitsContinuously,
    new Build(safeBuild),
    new ScoutAt(9),
    new Defend
  ))
}