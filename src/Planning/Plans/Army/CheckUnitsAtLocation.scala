package Planning.Plans.Army

import Planning.Composition.PixelFinders.Generic.TileMiddle
import Planning.Plan
import Lifecycle.With
import Planning.Composition.PixelFinders.TileFinder
import Planning.Composition.Property
import Planning.Composition.UnitMatchers.{UnitMatchAnything, UnitMatcher}

import Utilities.EnrichPixel._

class CheckUnitsAtLocation extends Plan {
  
  val quantity        = new Property[Int](1)
  val positionFinder  = new Property[TileFinder](TileMiddle)
  val unitMatcher     = new Property[UnitMatcher](UnitMatchAnything)
  val range           = new Property[Int](32)
  
  override def isComplete: Boolean = {
    With.units.ours
      .filter(unitMatcher.get.accept)
      .filter(unit =>
        positionFinder.get.find.exists(tile =>
          tile.pixelCenter.pixelDistanceSquared(unit.pixelCenter) < Math.pow(range.get, 2)))
      .size >= quantity.get
  }
}
