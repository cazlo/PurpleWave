package Planning.Composition.PositionFinders
import Performance.Caching.Cache
import Startup.With
import bwapi.TilePosition
import Utilities.EnrichPosition._

class PositionChoke extends PositionFinder {
  
  override def find: Option[TilePosition] = findCache.get
  val findCache = new Cache[Option[TilePosition]](3, () => findRecalculate)
  private def findRecalculate:Option[TilePosition] = {
    
    val home = With.geography.home
    val ourExposedChokes = With.geography.zones
      .filter(zone =>
        zone.owner == With.self ||
        With.executor.lastIntentions.values.exists(i => i.toBuild.nonEmpty && i.destination.exists(_.zone == zone)))
      .flatten(_.edges)
      .filter(edge => edge.zones.exists(_.owner != With.self))
    
    if (ourExposedChokes.isEmpty) return Some(home)
    
    val mostExposedChoke = ourExposedChokes
      .maxBy(choke => With.paths.groundPixels(
        choke.centerPixel.toTilePosition,
        home))
    
    return Some(mostExposedChoke.centerPixel.toTilePosition)
  }
}
