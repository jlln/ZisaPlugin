package Zisa

/**
  * Created by james on 27/04/16.
  */


import ij.{ImagePlus, WindowManager}
import ij.measure.ResultsTable
import ij.plugin.ChannelSplitter
object ImageProcessing {


  def splitChannels(image:ImagePlus):Array[ImagePlus] = {
    ChannelSplitter.split(image)
  }



  def processImage(compartment_channels:Seq[Int],blurring_radii:Seq[Int],image_path:String,table:ResultsTable) = {
    val image = ImageIO.openImageFile(image_path)
    val channels: Array[ImagePlus] = splitChannels(image)
    val experimental_condition = {
      val segments = image_path.split("/")
      segments(segments.length - 2)
    }

    val compartments: Seq[List[Compartment]] = compartment_channels.zip(blurring_radii).map {
      case (c, r) => CompartmentProcessing.processChannelToCompartments(c.toString, channels(c), r)
    }
    val cells = CompartmentProcessing.mergeCompartmentsToCells(experimental_condition, List(), compartments.flatten.toList)
    val focussed_cells = cells.map(_.focus())

    val intensity_results:Seq[CellResultCollection] = cells.map {
      cell => new CellResultCollection(experimental_condition,channels.zipWithIndex.map{
        case(channel,i) => Measurement.measureCellularIntensity(channel,cell,i.toString)
      }.toSeq.flatten)
    }
    intensity_results.map(_.writeResult(table))
    image.changes = false
    channels.map(_.changes=false)
    WindowManager.closeAllWindows()
    table.show("Zisa Results")
  }

}
