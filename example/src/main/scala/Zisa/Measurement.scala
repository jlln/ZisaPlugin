package Zisa

import ij.ImagePlus
import ij.measure.ResultsTable
import ij.plugin.filter.Analyzer

/**
  * Created by james on 6/05/16.
  */


class ExperimentStage(stage_name:String,analysis_function:((Seq[ImagePlus],Cell)=>List[Seq[Result]])){
  def run(channels:Seq[ImagePlus],cell:Cell):CellResultCollection = new CellResultCollection(cell.getCondition,analysis_function(channels,cell).flatten)
}

class ExperimentSpecification(channels:Seq[Int],experiment_stages:List[ExperimentStage]){
  //Used to define the channels for compartmentalization, and the options for analysis
  def getChannels = channels
  def getExperimentStages = experiment_stages
}


object Measurement {
  //make a function that accepts an image and a cell and an image label and an experiment specification, and returns results.
  def conductExperiment(channels:Seq[ImagePlus],cell:Cell,specification:ExperimentSpecification):CellResultCollection = {
    val stages = specification.getExperimentStages
    val experiment_results = stages.map(s=> s.run(channels,cell))
    val cell_result = experiment_results.tail.foldLeft(experiment_results.head)(_.addNewResultCollection(_))
    cell_result
  }





  val measureCellularIntensity:((Seq[ImagePlus],Cell) => List[Seq[Result]]) = (channels:Seq[ImagePlus],cell:Cell) => {
    val experimentalCondition = cell.getCondition
    val results = cell.getCompartments.map { c =>
      {
      val comparment_name = c.getCompartment
        channels.zipWithIndex.map {
          case (image, image_label) => measureIntensity(image, c, experimentalCondition, image_label.toString)
        }
      }
    }
    results
  }

  def measureIntensity(image:ImagePlus,compartment:Compartment,condition_label:String,image_label:String):Result = {
    //Measure the mean intensity of a given image across the slices of a compartment
    val intensity_results = new ResultsTable()
    val measurements = ij.measure.Measurements.MEAN
    val analyzer = new Analyzer(image, measurements, intensity_results)
    for (s <- compartment.getSlices) {
      image.setSlice(s.getSlice)
      image.setRoi(s.getRoi)
      analyzer.measure()
    }
    val intensity_values = intensity_results.getColumn(intensity_results.getColumnIndex("Mean"))
    val compartment_name = compartment.getCompartment
    val areas = compartment.getSlices.map(_.getArea)
    val measurement_label = "Channel" + image_label + "Compartment" + compartment.getCompartment + "MeanIntensity"
    val result_entries = intensity_values.zip(areas).map{
      case (v,a) => new ResultEntry(compartment_name,measurement_label,Some(v),a)
    }
    new Result(compartment_name,condition_label,result_entries)

  }

//  def measurePearsonCorrelation(image:ImagePlus,compartment:Compartment,condition_label:String,image_label:String):Result = {
//    val pixels = compartment.
//  }








}
