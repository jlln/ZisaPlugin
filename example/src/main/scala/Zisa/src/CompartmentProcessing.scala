package Zisa.src

/**
  * @author james
  *
  */
import ij.{IJ, ImagePlus}
import ij.measure.ResultsTable
import ij.plugin.filter.ParticleAnalyzer
import ij.plugin.frame.RoiManager

import scala.annotation.tailrec


object CompartmentProcessing {
  @tailrec
  def mergeCompartmentsToCells_(condition:String,cells:List[Cell],compartments:List[Compartment]):List[Cell] ={
    if (compartments.isEmpty) return cells //If there are no more compartments to incorporate, return the cell list
    val working_compartment = compartments.head
    if (cells.isEmpty){ //If there are no cells, begin by creating a cell from the first compartment in the list
    val new_cells_list = List(new Cell(condition,List(working_compartment)))
      mergeCompartmentsToCells_(condition,new_cells_list,compartments.tail)
    }
    else{

      val overlapping_cells = cells.filter(_.intersects(working_compartment))
      if (overlapping_cells.isEmpty){
        val new_cells_list = cells :+ new Cell(condition,List(working_compartment))
        mergeCompartmentsToCells_(condition,new_cells_list,compartments.tail)
      }
      else if (overlapping_cells.length == 1){
        val match_cell_index = cells.indexOf(overlapping_cells.head)
        val updated_match_cell = overlapping_cells.head.append(working_compartment)
        val new_cells_list = cells.updated(match_cell_index,updated_match_cell)
        mergeCompartmentsToCells_(condition,new_cells_list,compartments.tail)
      }
      else{
        val merged_cells = overlapping_cells.tail.foldLeft(overlapping_cells.head)(_.merge(_)).append(working_compartment)
        val new_cells = cells.filter(!overlapping_cells.contains(_)) :+ merged_cells
        mergeCompartmentsToCells_(condition,new_cells,compartments.tail)
      }
    }
  }


  @tailrec
  def mergeCompartmentsToCells(condition:String,cells:List[Cell],compartments:List[Compartment]):List[Cell] = {
    //Tail recursive function for merging overlapping compartments into cells.
    if (compartments.isEmpty) return cells //If there are no more compartments to incorporate, return the cell list
    if (cells.isEmpty){ //If there are no cells, begin by creating a cell from the first compartment in the list
      val new_cells_list = List(new Cell(condition,List(compartments.head)))
      val new_compartment_list = compartments.tail
      mergeCompartmentsToCells(condition,new_cells_list,new_compartment_list)
    }
    else{  //If there are cells in the list, check the most recently created one to see if it can be expanded.
      val working_cell = cells.last
      val intersecting_compartments = compartments.filter(c=> working_cell.intersects((c)))
      //If it cannot be expanded, create a new cell with an unused compartment from the compartment list
      if (intersecting_compartments.isEmpty){
        val new_cell = new Cell(condition,List(compartments.head))
        val new_cells = cells :+ new_cell
        mergeCompartmentsToCells(condition,new_cells,compartments.tail)
      }
      else{ //If it can be expanded, then expand it.
        val working_index = cells.length -1
        val updated_cell = working_cell.append(intersecting_compartments.head)
        val updated_compartment_list = compartments.filter(c=> c!=intersecting_compartments.head)
        val updated_cell_list = cells.updated(working_index,updated_cell)
        mergeCompartmentsToCells(condition,updated_cell_list,updated_compartment_list)
      }
    }
  }


  def processChannelToCompartments(compartment_name:String,image:ij.ImagePlus,blurring_radius:Int):List[Compartment]={
    /*
     * A function to facilitate the processing of a single channel into a list of Compartments.
     */
    val compartment_slices = Profiling.timed(Profiling.printTime("Compartments Slices Found In")){identifyCompartmentSlices(compartment_name,image,blurring_radius)}
    val compartments:List[Compartment] = Profiling.timed(Profiling.printTime("Compartments Merged In")){(mergeCompartments(image,List(),compartment_slices.toList))}
    compartments
  }


  @tailrec
  def mergeCompartments(image:ImagePlus,compartments:List[Compartment],compartment_slices:List[CompartmentSlice]):List[Compartment] = {
    if (compartment_slices.isEmpty) return compartments //If there are no more compartment slices to incorporate, return the compartment list
    else {
      if (compartments.isEmpty) {  //If there are no compartments in the list, begin a new compartment using the first slice in the compartment list
        val new_compartments = List(new Compartment(image, List(compartment_slices.head)))
        val new_slice_list = compartment_slices.tail
        mergeCompartments(image, new_compartments, new_slice_list)
      }
      else { //If there are compartments in the list, check the most recently created one to see if it can be expanded.
        val working_compartment: Compartment = compartments.last
        val intersecting_slices = compartment_slices.filter(c => working_compartment.overlapExistsWithCompartmentSlice(c))
        if (intersecting_slices.isEmpty) { //If it cannot be expanded, create a new compartment with an unused slice from the slice list
          val new_compartment = new Compartment(image, List(compartment_slices.head))
          val new_slice_list = compartment_slices.tail
          mergeCompartments(image, compartments :+ new_compartment, new_slice_list)
        }
        else { //If it can be expanded, then expand it.
          val working_index = compartments.length - 1
          val updated_compartment = working_compartment.append(intersecting_slices.head)
          val updated_slice_list = compartment_slices.filter(x => x != intersecting_slices.head)
          val updated_compartment_list = compartments.updated(working_index, updated_compartment)
          mergeCompartments(image, updated_compartment_list, updated_slice_list)
        }
      }
    }
  }





  def identifyCompartmentSlices(compartment_name:String,image:ij.ImagePlus,blurring_radius:Int):Array[CompartmentSlice] = {
    //First apply the image transformation using the user-determined blurring radius
    image.show()
    val compartment_mask=image.duplicate()
    compartment_mask.getChannelProcessor().resetMinAndMax()
    compartment_mask.show
    val calibration = image.getCalibration()
    calibration.setUnit("micron")
    val outlier_radius = calibration.getRawX(4.8)
    val cell_lower = math.pow(calibration.getRawX(5),2)*3.14156
    val cell_upper = math.pow(calibration.getRawX(50),2)*3.14156
    val outlier_search_string = "radius="+outlier_radius+" threshold=50 which=Dark stack"
    IJ.run(compartment_mask,"Gaussian Blur...", "sigma="+blurring_radius+" stack")
    IJ.run(compartment_mask,"Make Binary", "method=RenyiEntropy background=Default calculate ")
    IJ.run(compartment_mask,"Fill Holes", "stack")
    IJ.run(compartment_mask,"Remove Outliers...", outlier_search_string);
    IJ.run(compartment_mask,"Watershed", "stack")
    //Then analyse the resulting blobs to produce ROIs.
    val roim= new RoiManager()
    var results_table= new ResultsTable()
    val pa = new ParticleAnalyzer(ParticleAnalyzer.ADD_TO_MANAGER,
      ij.measure.Measurements.MEAN+ij.measure.Measurements.CENTROID+ij.measure.Measurements.AREA,
      results_table,
      cell_lower,cell_upper,
      0.3,1.0)

    val stack_size = compartment_mask.getStackSize()
    for (i<-(0 until stack_size)){
      compartment_mask.setSliceWithoutUpdate(i + 1)
      pa.analyze(compartment_mask)
    }
    val rm=roim.getRoisAsArray()
    //Then converting these ROIs into CompartmentSliceObjects
    val slice_list= for (roi<-rm) yield roi.getName().split("-")(0).toInt
    val roi_and_slice = (rm.zip(slice_list)).sortBy(_._2)


    val x_centres = results_table.getColumn(results_table.getColumnIndex("X"))
    val y_centres = results_table.getColumn(results_table.getColumnIndex("Y"))
    val areas = results_table.getColumn(results_table.getColumnIndex("Area"))
    val processed_roi = ((((slice_list zip x_centres) zip y_centres) zip rm) zip areas) map {
      case ((((s,x),y),r),sr) => new CompartmentSlice(compartment_name,s,x,y,r,sr.toInt)
    }
    roim.reset()
    roim.close()
    compartment_mask.changes=false
    compartment_mask.close()
    processed_roi
  }











}
