package Zisa.src

import ij.ImagePlus

/**
  * Created by james on 4/05/16.
  */



class Cell(experimental_condition:String,compartments:List[Compartment]) {
  def getCondition = experimental_condition
  def getCompartments = compartments
  def append(compartment:Compartment) = new Cell(experimental_condition,compartments :+ compartment)
  def intersects(compartment:Compartment):Boolean = {
    val intersections = compartments.map{c=> c.overlapExistsWithCompartment(compartment)}
    intersections.contains(true)
  }




  def focus():Cell = {
    val focussed_compartments = compartments.map(_.focus)
    val focussed_compartment_indices = focussed_compartments.map(_.getSlices.map(_.getSlice))
    val common_slices = if (compartments.length > 1)
      focussed_compartment_indices.tail.foldLeft(focussed_compartment_indices.head)((a: List[Int], b: List[Int]) => a intersect b)
      else
        focussed_compartment_indices.head
    val compartments_retained = compartments.map(c=> c.selectSliceSubset(common_slices))
    new Cell(experimental_condition,compartments_retained)
  }


  def getBoundingBox = {
    val start_x:List[Int] = for (s<-compartments) yield s.getBoundingBox.getBounds.x
    val x=start_x.min
    val start_y:List[Int] = for (s<-compartments) yield s.getBoundingBox.getBounds.y
    val y= start_y.min
    val widths:List[Int] = for (s<-compartments) yield s.getBoundingBox.getBounds.width
    val w = widths.max
    val heights:List[Int] = for (s<-compartments) yield s.getBoundingBox.getBounds.height
    val h = heights.max
    new ij.gui.Roi(x,y,w,h)
  }

  def findThreshold(channel:ImagePlus,k:Int):Double = {
    val pixels:Array[Float] = compartments.toArray.flatMap(c => c.getPixels(channel)).flatten.flatten
    Blobs.kMeans(k,pixels)
  }

  def getCombinedMask(channel:ImagePlus):Array[Array[Array[Int]]] = {
    val compartment_masks:List[Array[Array[Array[Int]]]] = compartments.map(_.getMaskingPixels(channel))
    ArrayFunctions.addMultiple3DIntArrays(compartment_masks)
  }

//  def findThresholds(channels:Seq[ImagePlus],k:Int):Seq[Double] = {
//    //Generates images containing the thresholded objects for each channel for the cell
//    //First define the boundaries of the cell by adding the compartment masks together
//    val combined_mask = getCombinedMask(channels.head)
//
//
//
//  }




  def visualInspection(image:ImagePlus): Unit ={
    image.setRoi(getBoundingBox)
    val preview_image = image.duplicate()
    preview_image.show()
    Thread.sleep(10000)
    preview_image.close()
  }






  override def toString = compartments.length.toString + "Compartments," + compartments.head.getSlices.length.toString + " Slices"
}



