package Zisa

import ij.ImagePlus
import ij.gui.ShapeRoi
import ij.plugin.frame.RoiManager

/**
  * Created by james on 3/05/16.
  */
class CompartmentSlice(compartment:String,slice:Int,x_centre:Double,y_centre:Double,roi:ij.gui.Roi,area:Int){
  def getCompartment = compartment
  def getSlice = slice
  def getXCentre = x_centre
  def getYCentre = y_centre
  def getCentroid = (x_centre,y_centre)
  def getRoi = roi
  def getArea = area
  def getBoundingBox = {
    val x = getRoi.getBounds.x
    val y = getRoi.getBounds.y
    val w = getRoi.getBounds.width
    val h = getRoi.getBounds.height
    new ij.gui.Roi(x,y,w,h)
  }
  def intersect(that:CompartmentSlice):Boolean = {

    val slice_delta = scala.math.abs(slice - that.getSlice)
    if (slice_delta > 2) false
    else {
      val this_roi = new ShapeRoi(this.getRoi)
      val that_roi = new ShapeRoi(that.getRoi)
      val intersection_roi = this_roi.and(that_roi)
      val overlap_box = intersection_roi.getBounds
      val overlap_area = overlap_box.width * overlap_box.height
      overlap_area > 0
    }
  }

  def makeCroppedProcessor(image:ij.ImagePlus,boundaries:ij.gui.Roi):ij.process.ImageProcessor = {
    image.setRoi(boundaries)
    image.setSlice(getSlice)
    image.getProcessor.crop()
  }
  def getPixels(image:ij.ImagePlus,mask:List[List[Int]]):Array[Array[Float]] = {
    val cropped_image = makeCroppedProcessor(image,getBoundingBox)
    cropped_image.resetMinAndMax()
    val pixel_array:Array[Array[Float]] = cropped_image.getFloatArray
    val corrected_array = pixel_array.flatten.zip(mask.flatten).map{
      case (p,m) => if (m > 0) p else 0
    }.grouped(mask.head.length)
    corrected_array.toArray
  }

  def getMaskPixels(mask_image:ij.ImagePlus):List[List[Int]] = {
    mask_image.setSlice(slice)
    mask_image.setRoi(roi)
    val processor = mask_image.getProcessor.crop()
    processor.getIntArray.map(x=>x.toList).toList
  }




}
