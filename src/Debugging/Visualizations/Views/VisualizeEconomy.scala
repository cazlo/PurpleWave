package Debugging.Visualizations.Views

import Debugging.Visualizations.Rendering.DrawScreen
import Lifecycle.With

object VisualizeEconomy {
  def render() {
    val labels = Vector(
      "Active miners:",
      "Active drillers:",
      "Minerals per minute:",
      "Gas per minute:",
      "Total minerals (est.):",
      "Total gas (est.):",
      "Total minerals (real):",
      "Total gas (real):"
    )
    val values = Vector(
      With.economy.ourActiveMiners.size,
      With.economy.ourActiveDrillers.size,
      (With.economy.ourIncomePerFrameMinerals * 24 * 60).toInt,
      (With.economy.ourIncomePerFrameGas * 24 * 60).toInt,
      With.economy.ourEstimatedTotalMinerals.toInt,
      With.economy.ourEstimatedTotalGas.toInt,
      With.economy.ourActualTotalMinerals,
      With.economy.ourActualTotalGas
    )
    DrawScreen.column(550, 31, labels.mkString("\n"))
    DrawScreen.column(600, 31, values.mkString("\n"))
  }
}
