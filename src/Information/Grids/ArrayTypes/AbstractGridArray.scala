package Information.Grids.ArrayTypes

import Information.Grids.AbstractGrid
import Mathematics.Pixels.Tile

abstract class AbstractGridArray[T] extends AbstractGrid[T] {
  
  protected var values:Array[T]
  private var initialized = false
  
  def defaultValue:T
  def repr(value:T):String
  
  def reset() = {
    val default = defaultValue
    
    //Use a while-loop because in Scala they are much faster than for-loops
    var i = 0
    while (i < length) {
      values(i) = default
      i += 1
    }
  }
  
  final def initialize()                          = if ( ! initialized) { onInitialization(); initialized = true }
  override def update()                           = initialize()
  def onInitialization() {}
  def indices:Iterable[Int]                       = values.indices
  def tiles:Iterable[Tile]                        = indices.map(i => new Tile(i))
  def get(i:Int):T                                = if (valid(i)) values(i) else defaultValue
  def set(i:Int, value:T):Unit                    = if (valid(i)) values(i) = value
  def set(tile:Tile, value:T):Unit                = set(tile.i, value)
}

