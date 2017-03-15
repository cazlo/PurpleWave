package Development.Visualization

import Startup.With

object Visualization {
  def onFrame() {
    if (With.configuration.enableVisualization) {
      With.game.setTextSize(bwapi.Text.Size.Enum.Small)
      if (With.configuration.enableVisualizationBattles)        VisualizeBattles      .render
      if (With.configuration.enableVisualizationEconomy)        VisualizeEconomy      .render
      if (With.configuration.enableVisualizationGeography)      VisualizeGeography    .render
      if (With.configuration.enableVisualizationGrids)          VisualizeGrids        .render
      if (With.configuration.enableVisualizationPerformance)    VisualizePerformance  .render
      if (With.configuration.enableVisualizationPlans)          VisualizePlans        .render
      if (With.configuration.enableVisualizationResources)      VisualizeResources    .render
      if (With.configuration.enableVisualizationScheduler)      VisualizeScheduler    .render
      if (With.configuration.enableVisualizationUnitsForeign)   VisualizeUnitsForeign .render
      if (With.configuration.enableVisualizationUnitsOurs)      VisualizeUnitsOurs    .render
    }
  }
}