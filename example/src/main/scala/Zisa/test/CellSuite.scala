/**
  * Created by james on 5/05/16.
  */
import Zisa._
import Zisa.src._
import ij.{IJ, ImagePlus, ImageStack}
import ij.measure.ResultsTable
import org.scalatest._

class CellSuite extends FunSuite with Matchers{
  val test_image_1_path = "/home/james/ZisaDev/ZisaPlugin/example/src/main/scala/Zisa/test/resources/test-1.tif"


  test("ImageProcessingToCells"){
    val t = new ResultsTable()
    val stage = new ExperimentStage("Intensity",Measurement.measureCellularIntensity)
    val specification = new ExperimentSpecification(List(2),List(stage))
    val cells = ImageProcessing.processImage(List(5),test_image_1_path,t,specification)
    t.getColumn(0).length should equal(4)
  }

  test("CellBoundariesMask"){
    val blurring_radii = Seq(9,9,5)
    val compartment_channels = Seq(1,2)
    val image = ImageIO.openImageFile(test_image_1_path)
    val channels: Array[ImagePlus] = ImageProcessing.splitChannels(image)
    val experimental_condition = "test"

    val compartments: Seq[List[Compartment]] = compartment_channels.zip(blurring_radii).map {
      case (c, r) => CompartmentProcessing.processChannelToCompartments(c.toString, channels(c), r)
    }
    val cells:List[Cell] = CompartmentProcessing.mergeCompartmentsToCells(experimental_condition, List(), compartments.flatten.toList)
    val focussed_cells = cells.map(_.focus())
    val trial_image_outer_mask = focussed_cells.head.getCombinedMask(channels.head)
    println(trial_image_outer_mask.length)
    val slices = trial_image_outer_mask.map(ImageIO.makeImage(_))
    slices.map(_.show())
    IJ.run("Tile")
    Thread.sleep(5000)
  }

}
