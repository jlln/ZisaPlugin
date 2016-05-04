package Zisa

import ij.ImagePlus

/**
  * Created by james on 4/05/16.
  */



class Cell(compartments:List[Compartment]) {
  def append(compartment:Compartment) = new Cell(compartments :+ compartment)
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
    new Cell(compartments_retained)
  }

  override def toString = compartments.length.toString + "Compartments," + compartments.head.getSlices.length.toString + " Slices"
}



