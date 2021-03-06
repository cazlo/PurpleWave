package Mathematics.Heuristics

object HeuristicMathMultiplicative extends HeuristicMath {
  
  val heuristicMaximum  = 100000.0
  val heuristicMinimum  = 1.0
  val default           = heuristicMinimum
  
  def fromBoolean(value:Boolean):Double = if (value) 2.0 else 1.0
  
  def order[TContext, TCandidate, THeuristic, THeuristicWeight <: HeuristicWeight[TContext, TCandidate]](
    context       : TContext,
    heuristics    : Iterable[THeuristicWeight],
    candidate     : TCandidate)
      : Double = {
    - heuristics.map(_.weighMultiplicatively(context, candidate)).product
  }
}
