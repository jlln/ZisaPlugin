package Zisa

import ij.ImagePlus
import ij.measure.ResultsTable
import ij.plugin.filter.Analyzer

/**
  * Created by james on 6/05/16.
  */
object Measurement {


  def measureCellularIntensity(image:ImagePlus,cell:Cell,image_label:String):Seq[Result] = {
    val experimentalCondition = cell.getCondition
    val results = cell.getCompartments.map{ c=>
      val comparment_name = c.getCompartment
      measureIntensity(image,c,experimentalCondition,image_label)
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


}
