package Debugging.Visualizations.Views

import Debugging.Visualizations.Colors
import Debugging.Visualizations.Rendering.DrawMap
import Planning.Composition.ResourceLocks.LockArea
import Lifecycle.With

object VisualizeRealEstate {
  
  def render() = With.realEstate.requests.foreach(renderArea)
  
  def renderArea(lock:LockArea) {
    if ( ! lock.satisfied) return
    lock.area.foreach(rectangle => {
      DrawMap.tileRectangle(rectangle, Colors.DarkRed)
      DrawMap.labelBox(
        Vector("Reserved by", lock.owner.toString),
        rectangle.midPixel,
        drawBackground = true,
        backgroundColor = Colors.DarkRed)
    })
  }
}
