package Utilities

import Mathematics.Pixels
import Mathematics.Pixels.{Pixel, Tile, TileRectangle}

case object EnrichPixel {
  implicit class EnrichedPixelCollection(positions:Iterable[Pixel]) {
    def minBound:Pixel = {
      if (positions.isEmpty) return Pixels.Points.middle
      Pixel(
        positions.view.map(_.x).min,
        positions.view.map(_.y).min)}
    def maxBound:Pixel = {
      if (positions.isEmpty) return Pixels.Points.middle
      Pixel(
        positions.view.map(_.x).max,
        positions.view.map(_.y).max)}
    def bottomLeftBound:Pixel = {
      if (positions.isEmpty) return Pixels.Points.middle
      Pixel(
        positions.view.map(_.x).min,
        positions.view.map(_.y).max)}
    def topRightBound:Pixel = {
      if (positions.isEmpty) return Pixels.Points.middle
      Pixel(
        positions.view.map(_.x).max,
        positions.view.map(_.y).min)}
    def centroid:Pixel = {
      if (positions.isEmpty) return Pixels.Points.middle
      Pixel(
        positions.view.map(_.x).sum / positions.size,
        positions.view.map(_.y).sum / positions.size)
    }
  }
  
  implicit class EnrichedRectangleCollection(rectangles:Iterable[TileRectangle]) {
    def boundary: TileRectangle =
      new TileRectangle(
        new Tile(
          rectangles.map(_.startInclusive.x).min,
          rectangles.map(_.startInclusive.y).min),
        new Tile(
          rectangles.map(_.endExclusive.x).max,
          rectangles.map(_.endExclusive.y).max))
  }
  
  implicit class EnrichedTileCollection(positions:Iterable[Tile]) {
    def minBound:Tile = {
      if (positions.isEmpty) return Pixels.Points.tileMiddle
      new Tile(
        positions.view.map(_.x).min,
        positions.view.map(_.y).min)}
    def maxBound:Tile = {
      if (positions.isEmpty) return Pixels.Points.tileMiddle
      new Tile(
        positions.view.map(_.x).max,
        positions.view.map(_.y).max)}
    def centroid:Tile = {
      if (positions.isEmpty) return Pixels.Points.tileMiddle
      new Tile(
        positions.view.map(_.x).sum / positions.size,
        positions.view.map(_.y).sum / positions.size)
    }
  }
}
