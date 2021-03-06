package Planning.Plans.GamePlans.Protoss

import Macro.BuildRequests.{BuildRequest, RequestUnitAtLeast, RequestUpgrade}
import Planning.Composition.UnitMatchers.{UnitMatchType, UnitMatchWarriors}
import Planning.Plans.Army.{Attack, ControlEnemyAirspace, Defend}
import Planning.Plans.Compound.{IfThenElse, _}
import Planning.Plans.Information.{ScoutAt, ScoutExpansionsAt}
import Planning.Plans.Macro.Automatic.Continuous.{BuildPylonsContinuously, TrainContinuously, TrainGatewayUnitsContinuously, TrainProbesContinuously}
import Planning.Plans.Macro.Automatic.Gas.BuildAssimilators
import Planning.Plans.Macro.BuildOrders.Build
import Planning.Plans.Macro.Expansion.WhenMinedOutExpand
import Planning.Plans.Macro.Milestones.{UnitsAtLeast, UnitsAtMost, UnitsExactly}
import Planning.Plans.Macro.Reaction.{EnemyHydralisks, EnemyMassMutalisks, EnemyMassZerglings, EnemyMutalisks}
import ProxyBwapi.Races.Protoss

class ProtossVsZerg extends Parallel {
  
  description.set("Protoss vs Zerg")
  
  private val lateGameBuild = Vector[BuildRequest] (
  
    RequestUnitAtLeast(2,   Protoss.Nexus),
    RequestUnitAtLeast(4,   Protoss.Gateway),
    RequestUnitAtLeast(1,   Protoss.Forge),
    RequestUnitAtLeast(1,   Protoss.CitadelOfAdun),
    RequestUpgrade(         Protoss.DragoonRange),
    RequestUpgrade(         Protoss.GroundDamage, 1),
    RequestUnitAtLeast(6,   Protoss.Gateway),
    RequestUnitAtLeast(1,   Protoss.TemplarArchives),
    
    RequestUnitAtLeast(3,   Protoss.Nexus),
    RequestUpgrade(         Protoss.GroundDamage, 2),
    RequestUnitAtLeast(10,  Protoss.Gateway),
    RequestUpgrade(         Protoss.GroundDamage, 3),
    
    RequestUnitAtLeast(4,   Protoss.Nexus),
    RequestUnitAtLeast(2,   Protoss.Forge),
    RequestUnitAtLeast(7,   Protoss.PhotonCannon),
    RequestUpgrade(         Protoss.GroundDamage, 2),
    RequestUpgrade(         Protoss.GroundArmor,  2),
    RequestUnitAtLeast(8,   Protoss.Gateway),
    
    RequestUnitAtLeast(5,   Protoss.Nexus),
    RequestUnitAtLeast(10,  Protoss.Gateway),
    RequestUpgrade(         Protoss.GroundDamage, 3),
    RequestUpgrade(         Protoss.GroundArmor, 3),
    
    RequestUnitAtLeast(6,   Protoss.Nexus),
    RequestUnitAtLeast(12,  Protoss.Gateway),
    
    RequestUnitAtLeast(7,   Protoss.Nexus),
    RequestUnitAtLeast(8,   Protoss.Nexus)
  )
  
  private class WhenSafeTakeNatural extends IfThenElse(
    new UnitsAtLeast(12, UnitMatchWarriors),
    new Build(ProtossBuilds.TakeNatural)
  )
  
  private class WhenSafeTakeThirdBase extends IfThenElse(
    new And(
      new UnitsAtLeast(16, UnitMatchWarriors),
      new UnitsAtLeast(1, UnitMatchType(Protoss.Corsair)),
      new Or(
        new UnitsAtLeast(1, UnitMatchType(Protoss.Reaver)),
        new UnitsAtLeast(1, UnitMatchType(Protoss.DarkTemplar)))
    ),
    new Build(ProtossBuilds.TakeThirdBase)
  )
  
  private class BuildZealotsInitially extends IfThenElse(
    new UnitsExactly(0, UnitMatchType(Protoss.CyberneticsCore)),
    new Build(ProtossBuilds.OpeningTwoGate99Zealots)
  )
  
  private class RespondToMutalisksWithMassCorsairs extends IfThenElse(
    new EnemyMutalisks,
    new Parallel(
      new Build(ProtossBuilds.TechCorsairs),
      new TrainContinuously(Protoss.Corsair, 12)
    )
  )
  
  private class RespondToHydrasWithReavers_OrGetCorsairTech extends IfThenElse(
    new EnemyHydralisks,
    new Build(ProtossBuilds.TechReavers),
    new Build(ProtossBuilds.TechCorsairs)
  )
  
  private class BuildZealotsOrDragoons_BasedOnMutalisksAndZerglings extends IfThenElse (
    new EnemyMassMutalisks,
    new TrainContinuously(Protoss.Dragoon),
    new IfThenElse(
      new Or(
        new EnemyMassZerglings,
        new UnitsAtMost(8, UnitMatchType(Protoss.Zealot))
      ),
      new TrainContinuously(Protoss.Zealot),
      new TrainGatewayUnitsContinuously
    )
  )
  
  private class AttackWhenWeHaveArmy extends IfThenElse(
    new UnitsAtLeast(10, UnitMatchWarriors),
    new Attack,
    new Defend
  )
  
  private val earlyZealotCount = 8
  
  children.set(Vector(
    new Build(ProtossBuilds.OpeningTwoGate99),
    new WhenMinedOutExpand,
    new WhenSafeTakeNatural,
    new WhenSafeTakeThirdBase,
    new BuildZealotsInitially,
    new BuildPylonsContinuously,
    new TrainProbesContinuously,
    new BuildAssimilators,
    new RespondToMutalisksWithMassCorsairs,
    new TrainContinuously(Protoss.DarkTemplar, 3),
    new TrainContinuously(Protoss.Reaver, 2),
    new TrainContinuously(Protoss.Corsair, 3),
    new RespondToHydrasWithReavers_OrGetCorsairTech,
    new BuildZealotsOrDragoons_BasedOnMutalisksAndZerglings,
    new Build(ProtossBuilds.TakeNatural),
    new Build(lateGameBuild),
    new ScoutAt(10),
    new ScoutExpansionsAt(40),
    new ControlEnemyAirspace,
    new Attack { attackers.get.unitMatcher.set(UnitMatchType(Protoss.Corsair)) },
    new AttackWhenWeHaveArmy
  ))
}
