package Lifecycle

import Debugging.Visualization.Viewport
import Debugging.{AutoCamera, Configuration, Logger, Performance}
import Information.Battles.Battles
import Information.Geography.Geography
import Information.Geography.Pathfinding.Paths
import Information.Grids.Grids
import Information._
import Macro.Allocation._
import Macro.Architect
import Macro.Scheduling.Scheduler
import Micro.Intent.Commander
import Micro.State.Executor
import Planning.Plans.GamePlans.WinTheGame
import ProxyBwapi.Players.{PlayerInfo, Players}
import ProxyBwapi.ProxyBWMirror
import ProxyBwapi.UnitTracking.UnitTracker
import _root_.Performance.Latency
import bwta.BWTA

import scala.collection.JavaConverters._

object With {
  var game          : bwapi.Game    = null
  var architect     : Architect     = null
  var bank          : Bank          = null
  var camera        : AutoCamera    = null
  var configuration : Configuration = null
  var battles       : Battles       = null
  var executor      : Executor      = null
  var commander     : Commander     = null
  var economy       : Economy       = null
  var grids         : Grids         = null
  var intelligence  : Intelligence  = null
  var geography     : Geography     = null
  var gameplan      : WinTheGame    = null
  var latency       : Latency       = null
  var logger        : Logger        = null
  var paths         : Paths         = null
  var performance   : Performance   = null
  var proxy         : ProxyBWMirror = null
  var prioritizer   : Prioritizer   = null
  var realEstate    : RealEstate    = null
  var recruiter     : Recruiter     = null
  var scheduler     : Scheduler     = null
  var units         : UnitTracker   = null
  var viewport      : Viewport      = null
  
  var self    : PlayerInfo         = null
  var neutral : PlayerInfo         = null
  var enemies : Vector[PlayerInfo]   = null
  
  var frame       : Int = 0
  var mapWidth    : Int = 0
  var mapHeight   : Int = 0
  
  def onFrame() {
    frame         = With.game.getFrameCount
  }
  
  def onStart() {
    With.proxy                  = new ProxyBWMirror
    With.self                   = Players.get(With.game.self)
    With.neutral                = Players.get(With.game.neutral)
    With.enemies                = With.game.enemies.asScala.map(Players.get).toVector
    With.mapWidth               = With.game.mapWidth
    With.mapHeight              = With.game.mapHeight
    With.configuration          = new Configuration
    With.logger                 = new Logger
    initializeBWTA()
    With.architect              = new Architect
    With.bank                   = new Bank
    With.battles                = new Battles
    With.camera                 = new AutoCamera
    With.economy                = new Economy
    With.executor               = new Executor
    With.commander              = new Commander
    With.gameplan               = new WinTheGame
    With.geography              = new Geography
    With.grids                  = new Grids
    With.intelligence           = new Intelligence
    With.latency                = new Latency
    With.paths                  = new Paths
    With.performance            = new Performance
    With.prioritizer            = new Prioritizer
    With.realEstate             = new RealEstate
    With.recruiter              = new Recruiter
    With.scheduler              = new Scheduler
    With.units                  = new UnitTracker
    With.viewport               = new Viewport
  
    With.game.enableFlag(1) //Enable unit control
    With.game.setLocalSpeed(With.configuration.gameSpeed)
    With.game.setLatCom(With.configuration.enableLatencyCompensation)
  }
  
  def onEnd() {
    With.logger.flush
    BWTA.cleanMemory()
  }
  
  private def initializeBWTA() {
    BWTA.readMap()
    BWTA.analyze()
    //These may not be necessary or helpful since BWTA2 doesn't seem to work in BWMirror
    BWTA.computeDistanceTransform()
    BWTA.buildChokeNodes()
  }
}
