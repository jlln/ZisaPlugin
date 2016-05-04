package Zisa

import ij.measure.ResultsTable
import ij.plugin.filter.Analyzer
import ij.{IJ, ImagePlus}

/**
  * Created by james on 3/05/16.
  */
class Compartment(image:ImagePlus,slices:List[CompartmentSlice]){
  def getSlices = slices
  def getImageSlices = slices.map{ s=> s.getSlice}
  def last = slices.last
  def append(s:CompartmentSlice):Compartment = new Compartment(image,slices:+s)
  def merge(c:Compartment):Compartment = new Compartment(image,slices ++ c.getSlices)
  def getMaximumCrossSectionRoi = slices.zip(slices.map(s=>s.getArea)).maxBy(_._2)._1.getRoi
  def getTotalArea = slices.map(s=>s.getArea).sum
  def getMeanArea = Stats.mean(slices.map(s=>s.getArea))
  def getBoundingBox = {
    val start_x:List[Int] = for (s<-slices) yield s.getRoi.getBounds.x
    val x=start_x.min
    val start_y:List[Int] = for(s<-slices) yield s.getRoi.getBounds.y
    val y= start_y.min
    val widths:List[Int] = for (s<-slices) yield s.getRoi.getBounds.width
    val w = widths.max
    val heights:List[Int] = for (s<-slices) yield s.getRoi.getBounds.height
    val h = heights.max
    new ij.gui.Roi(x,y,w,h)

  }
  def getXCentre = slices.head.getXCentre
  def getYCentre = slices.head.getYCentre
  def getCentroids:List[(Double,Double)] = slices.map{ s=>
    s.getCentroid}.toList

  def getOverlayRoi:List[ij.gui.Roi] = {
    val roi = getMaximumCrossSectionRoi
    for (s<-getSlices) yield {
      val sroi = roi
      sroi.setPosition(s.getSlice)
      sroi
    }
  }

  def getPixels(mask:List[List[List[Int]]]):Array[Array[Array[Float]]] = {
    getSlices.toArray.zip(mask).map{
      case (s,m)=>s.getPixels(image,m)
    }
  }
  def getMaskingImages(mask_image:ij.ImagePlus):List[List[List[Int]]] = {
    getSlices.map(s=>s.getMaskPixels(mask_image))
  }

  def overlapExistsWithCompartment(that:Compartment):Boolean = {
    val intersections = for (a<- this.getSlices; b<-that.getSlices) yield a.intersect(b)
    intersections.contains(true)
  }
  def overlapExistsWithCompartmentSlice(that:CompartmentSlice):Boolean = {
    val intersections = this.getSlices.map(s => s.intersect(that))
    intersections.contains(true)
  }

  def focus():Compartment = {
    val edge_mask = image.duplicate()
    IJ.run(edge_mask, "Find Edges","stack")
    var variance_results = new ResultsTable()
    val measurements = ij.measure.Measurements.MEAN+ij.measure.Measurements.AREA
    val analyzer= new Analyzer(edge_mask,measurements,variance_results)
    for (s<-slices){
      edge_mask.setSlice(s.getSlice)
      edge_mask.setRoi(s.getRoi)
      analyzer.measure()
    }
    val variance_values = variance_results.getColumn(variance_results.getColumnIndex("Mean")).map(x=>x*x)
    val area_values = variance_results.getColumn(variance_results.getColumnIndex("Area"))
    val mean_area = Stats.mean(area_values)
    val variance_threshold = 0.6*variance_values.max
    val retained_slice_indices = for ((s,i)<-slices.zipWithIndex
      if (variance_values(i)>variance_threshold)) yield{
      i
    }
    val retained_slices = retained_slice_indices.map(i=> slices(i))
    new Compartment(image,retained_slices)

  }

  def selectSliceSubset(indices:List[Int]):Compartment = {
    val retained_slices = slices.filter(s=> indices.contains(s.getSlice))
    new Compartment(image,retained_slices)
  }

}
