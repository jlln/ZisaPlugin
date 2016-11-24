package Zisa.src

import ij.gui.ShapeRoi
import ij.process.ImageProcessor

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
  def getPixels(image:ij.ImagePlus):Array[Array[Float]] = {
    //image: the full image, possibly containing multiple cells.
    val mask = getMaskPixels(image)
    val cropped_image = makeCroppedProcessor(image,getBoundingBox)
    cropped_image.resetMinAndMax()
    val pixel_array:Array[Float] = cropped_image.getFloatArray.flatten
    val mask_array:Array[Int] = mask.flatten
    if (pixel_array.length != mask_array.length){
      throw new Exception("Mask and Image not equal in size")
    }
    pixel_array.zip(mask_array).map{
      case (p,m) => if (m > 0) p else 0
    }.grouped(mask.head.length).toArray
  }

  def getMaskPixels(image:ij.ImagePlus):Array[Array[Int]] = {
    image.setSlice(slice)
    val processor = image.getProcessor
    processor.setRoi(roi)
    processor.getMask().getIntArray()
  }

//  def applyThreshold(image:ImageProcessor,threshold:Double):ImageProcessor = {
//    ImageProcessing.applyThreshold(image,threshold)
//  }




}
