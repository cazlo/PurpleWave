package Plans.Macro.Build

import Development.TypeDescriber
import Plans.Allocation.{LockCurrency, LockCurrencyForUnit, LockUnits, LockUnitsExactly}
import Plans.Plan
import Startup.With
import Strategies.UnitMatchers.UnitMatchType
import Utilities.Property
import bwapi.UnitType

class TrainUnit(val traineeType:UnitType) extends Plan {
  
  val currencyPlan  = new Property[LockCurrency] (new LockCurrencyForUnit(traineeType))
  val trainerPlan   = new Property[LockUnits] (new LockUnitsExactly {
    this.unitMatcher.set(new UnitMatchType(traineeType.whatBuilds.first))
  })
  
  var _trainer:Option[bwapi.Unit] = None
  var _trainee:Option[bwapi.Unit] = None
  var lastOrderFrame = 0
  
  description.set(Some(TypeDescriber.describeUnitType(traineeType)))
  
  override def isComplete: Boolean = { _trainee.exists(p => p.exists && p.isCompleted) }
  override def getChildren: Iterable[Plan] = { List(currencyPlan.get, trainerPlan.get) }
  override def onFrame() {
    if (isComplete) {
      //It's important to quit so we release our resources
      return
    }
    
    getChildren.foreach(_.onFrame())
  
    //Require all the resources
    if (!getChildren.forall(_.isComplete)) {
      return
    }
  
    trainerPlan.get.units.headOption.foreach(_requireTraining)
  }
  
  def _reset() {
    currencyPlan.get.isSpent = false
    _trainer = None
    _trainee = None
  }
  
  def _requireTraining(trainer:bwapi.Unit) {
    
    // Training?	Ordered?	Unit?	Then
    // ---------- --------- ----- ----
    // Yes		    No		    -	    Wait
    // Yes		    Yes		    Yes	  Wait
    // Yes		    Yes		    No	  Wait (Unit should appear eventually)
    // No		      No		    -	    Order
    // No		      Yes		    -	    Order (again; unexpected, but stuff happens)
    
    if ( ! _trainer.contains(trainer)) {
      _reset()
    }
    
    val isTraining = ! trainer.getTrainingQueue.isEmpty
    val ordered = _trainer.nonEmpty
    
    if (isTraining) {
      if (ordered) {
        //TODO: Make sure we verify that the worker is *not complete* so, say, a wraith floating over a Startport doesn't get linked
        
        //Note that it's possible for a building to briefly have a worker type in the queue with no worker created.
        _trainee = With.ourUnits
          .filter(u =>
            u.getType == traineeType &&
            u.getX == trainer.getX &&
            u.getY == trainer.getY &&
            ! u.isCompleted)
          .headOption
        
        //There seems to be a 1+ frame delay between the queue getting started
        //and the incomplete worker being created.
        //So don't freak out if the worker doesn't show up right away
      }
    }
    else {
      _orderUnit(trainer)
    }
  }
  
  def _orderUnit(trainer:bwapi.Unit) {
    _trainer = Some(trainer)
    currencyPlan.get.isSpent = true
    trainer.train(traineeType)
    lastOrderFrame = With.game.getFrameCount
  }
}