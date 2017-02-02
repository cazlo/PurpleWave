package Types.BuildOrders.Protoss

import Types.BuildOrders.{BuildOrder, Buildable}
import Types.PositionFinders.PositionProxy
import bwapi.UnitType

class BuildProxyGateway extends BuildOrder {
  _buildOrder = Array(
    new Buildable(UnitType.Protoss_Probe),
    new Buildable(UnitType.Protoss_Probe),
    new Buildable(UnitType.Protoss_Probe),
    new Buildable(UnitType.Protoss_Probe),
    new Buildable(UnitType.Protoss_Pylon, positionFinder = new PositionProxy),
    new Buildable(UnitType.Protoss_Probe),
    new Buildable(UnitType.Protoss_Gateway, positionFinder = new PositionProxy),
    new Buildable(UnitType.Protoss_Gateway, positionFinder = new PositionProxy),
    new Buildable(UnitType.Protoss_Zealot),
    new Buildable(UnitType.Protoss_Pylon),
    new Buildable(UnitType.Protoss_Zealot),
    new Buildable(UnitType.Protoss_Zealot),
    new Buildable(UnitType.Protoss_Zealot),
    new Buildable(UnitType.Protoss_Zealot)
  )
}
