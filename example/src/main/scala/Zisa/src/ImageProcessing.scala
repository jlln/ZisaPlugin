package Zisa.src

/**
  * Created by james on 27/04/16.
  */


import ij.measure.ResultsTable
import ij.plugin.ChannelSplitter
import ij.process.{FloatProcessor, ImageProcessor}
import ij.{ImagePlus, WindowManager}

import scalafx.application.JFXApp.PrimaryStage
object ImageProcessing {


  def splitChannels(image:ImagePlus):Array[ImagePlus] = {
    ChannelSplitter.split(image)
  }




  def processImage(blurring_radii:Seq[Int],image_path:String,table:ResultsTable,experiment_specification: ExperimentSpecification):Array[Cell] = {
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
    val preview_tiles = ImageIO.tilePrepare(focussed_cells,channels(compartment_channels(0)))
    val preview_image = ImageIO.generateTiledImage(preview_tiles)
    preview_image.show()
    Thread.sleep(10000)

    val results:List[CellResultCollection] = focussed_cells.map{ c=> Measurement.conductExperiment(channels,c,experiment_specification)}
    results.map(_.writeResult(table))
    image.changes = false
    channels.map(_.changes=false)
    WindowManager.closeAllWindows()
    table.show("Zisa Results")
    focussed_cells.toArray
  }

}
