/**
  * Created by james on 5/05/16.
  */
import Zisa._
import Zisa.src.{ExperimentSpecification, ExperimentStage, ImageProcessing, Measurement}
import ij.measure.ResultsTable
import org.scalatest._

class CellSuite extends FunSuite with Matchers{
  val test_image_1_path = "/home/james/ZisaDev/ZisaPlugin/example/src/main/scala/Zisa/test/resources/sample_1.tif"


  test("ImageProcessingToCells"){
    val t = new ResultsTable()
    val stage = new ExperimentStage("Intensity",Measurement.measureCellularIntensity)
    val specification = new ExperimentSpecification(List(2),List(stage))
    val cells = ImageProcessing.processImage(List(5),test_image_1_path,t,specification)
    t.getColumn(0).length should equal(2)
  }

}
