package Information.Grids.Vision

import Mathematics.Shapes.Circle
import Information.Grids.ArrayTypes.AbstractGridBoolean
import Lifecycle.With

class GridEnemyDetection extends AbstractGridBoolean {
  
  override def update() {
    reset()
    With.units.enemy
      .filter(_.likelyStillThere)
      .filter(_.unitClass.isDetector)
      .foreach(u => {
      Circle.points(u.unitClass.sightRange / 32)
        .map(u.tileIncludingCenter.add)
        .foreach(set(_, true))
    })
  }
}