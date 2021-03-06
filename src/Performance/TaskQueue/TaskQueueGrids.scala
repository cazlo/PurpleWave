package Performance.TaskQueue
import Performance.Tasks.AbstractTask
import Performance.Tasks.Grids._

class TaskQueueGrids extends AbstractTaskQueue {
  
  override val tasks: Vector[AbstractTask] = Vector (
    new TaskGridChokepoints                 { urgency = 1   },
    new TaskGridPsi2x2and3x2                { urgency = 10  },
    new TaskGridPsi4x3                      { urgency = 10  },
    new TaskGridWalkable                    { urgency = 1   },
    new TaskGridWalkableTerrain             { urgency = 1   },
    new TaskGridWalkableUnits               { urgency = 1   },
    new TaskGridBuildable                   { urgency = 10  },
    new TaskGridBuildableTerrain            { urgency = 10  },
    new TaskGridUnits                       { urgency = 100 },
    new TaskGridAltitudeBonus               { urgency = 1   },
    new TaskGridEnemyDetection              { urgency = 100 },
    new TaskGridEnemyVision                 { urgency = 100 },
    new TaskGridMobility                    { urgency = 1   },
    new TaskGridDpsEnemyGroundConcussive    { urgency = 1000 },
    new TaskGridDpsEnemyGroundExplosive     { urgency = 1000 },
    new TaskGridDpsEnemyGroundNormal        { urgency = 1000 },
    new TaskGridDpsEnemyAirConcussive       { urgency = 1000 },
    new TaskGridDpsEnemyAirExplosive        { urgency = 1000 },
    new TaskGridDpsEnemyAirNormal           { urgency = 1000 }
  )
}
