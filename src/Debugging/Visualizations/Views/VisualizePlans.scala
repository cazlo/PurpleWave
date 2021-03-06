package Debugging.Visualizations.Views

import Debugging.Visualizations.Rendering.DrawScreen
import Planning.Plan
import Lifecycle.With

object VisualizePlans {
  def render() {
    DrawScreen.column(2, 2, describePlanTree(With.gameplan, 0, 0))
    drawPlansRecursively(With.gameplan)
  }
  
  private def drawPlansRecursively(plan:Plan) {
    plan.drawOverlay()
    plan.getChildren.foreach(drawPlansRecursively)
  }
  
  private def describePlanTree(plan:Plan, childOrder:Integer, depth:Integer):String = {
    if (isRelevant(plan)) {
      (describePlan(plan, childOrder, depth)
        ++ plan.getChildren.zipWithIndex.map(x => describePlanTree(x._1, x._2, depth + 1)))
        .mkString("")
    } else ""
  }
  
  private def describePlan(plan:Plan, childOrder:Integer, depth:Integer):String = {
    val checkbox = if (plan.isComplete) "X " else "  "
    val spacer = "  " * depth
    val leftColumn =
      (checkbox
        ++ spacer
        ++ "#"
        ++ (childOrder + 1).toString
        ++ " "
        ++ plan.toString)
    
    leftColumn + " " * Math.max(0, 45 - leftColumn.length) + "\n"
  }
  
  private def isRelevant(plan:Plan):Boolean = With.prioritizer.active(plan)
}
