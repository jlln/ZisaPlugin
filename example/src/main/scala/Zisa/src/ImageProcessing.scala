package Zisa.src

/**
  * Created by james on 27/04/16.
  */


import ij.measure.ResultsTable
import ij.plugin.ChannelSplitter
import ij.process.{FloatProcessor, ImageProcessor}
import ij.{ImagePlus, WindowManager}
object ImageProcessing {


  def splitChannels(image:ImagePlus):Array[ImagePlus] = {
    ChannelSplitter.split(image)
  }

  def applyThreshold(channel:ImageProcessor, threshold:Double): FloatProcessor ={
    //Applies threshold to a single processor slice of an image
    val channel_pixels:Array[Array[Float]] = channel.getFloatArray()
    val thresholded_pixels = channel_pixels.map(row => row.map(pixel =>{
      if (pixel > threshold) 255
      else 0
      }))
    new FloatProcessor(thresholded_pixels)
  }


  def processImage(blurring_radii:Seq[Int],image_path:String,table:ResultsTable,experiment_specification: ExperimentSpecification) = {
    val compartment_channels = experiment_specification.getChannels
    val image = ImageIO.openImageFile(image_path)
    val channels: Array[ImagePlus] = splitChannels(image)
    val experimental_condition = {
      val segments = image_path.split("/")
      segments(segments.length - 2)
    }
    val compartments: Seq[List[Compartment]] = compartment_channels.zip(blurring_radii).map {
      case (c, r) => CompartmentProcessing.processChannelToCompartments(c.toString, channels(c), r)
    }
    val cells:List[Cell] = CompartmentProcessing.mergeCompartmentsToCells(experimental_condition, List(), compartments.flatten.toList)
    val focussed_cells = cells.map(_.focus())


    val results:List[CellResultCollection] = focussed_cells.map{ c=> Measurement.conductExperiment(channels,c,experiment_specification)}
    results.map(_.writeResult(table))
    image.changes = false
    channels.map(_.changes=false)
    WindowManager.closeAllWindows()
    table.show("Zisa Results")
  }

}
