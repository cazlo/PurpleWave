package Information.Battles.Clustering

import Lifecycle.With
import ProxyBwapi.UnitInfo.UnitInfo

class BattleClustering {
  
  var lastUpdateFrame = 0
  
  private var nextUnits:          Traversable[UnitInfo] = Vector.empty
  private var clusterInProgress:  BattleClusteringState = new BattleClusteringState(Vector.empty)
  private var clusterComplete:    BattleClusteringState = new BattleClusteringState(Vector.empty)
  
  //////////////////////
  // Batch processing //
  //////////////////////
  
  def clusters = clusterComplete.clusters
  
  def enqueue(units: Traversable[UnitInfo]) {
    nextUnits = units
  }
  
  def run() {
    while ( ! clusterInProgress.isComplete && With.performance.continueRunning) {
      clusterInProgress.step()
    }
  
    if (clusterInProgress.isComplete) {
      clusterComplete = clusterInProgress
      clusterInProgress = new BattleClusteringState(nextUnits)
    }
  }
}
