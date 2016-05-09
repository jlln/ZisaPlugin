/**
  * Created by james on 5/05/16.
  */
import Zisa.{ImageIO, ImageProcessing}
import ij.measure.ResultsTable
import org.scalatest._

class CellSuite extends FunSuite with Matchers{
  val test_image_1_path = "images/test_image_1.tif"


  test("ImageProcessingToCells"){
    val t = new ResultsTable()
    val cells = ImageProcessing.processImage(Seq(0,2),Seq(5,5),test_image_1_path,t)
    t.getColumn(0).length should equal(4)
  }

}
