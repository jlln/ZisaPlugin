/**
  * Created by james on 3/05/16.
  */
/**
  * @author james
  */
import Zisa.{CompartmentProcessing, ImageIO, ImageProcessing}
import ij.ImagePlus
import org.scalatest._

class CompartmentSuite extends FunSuite with Matchers {
  val test_image_1_path = "images/test_image_1.tif"
  val test_image_2_path = "images/0_3_R3D_D3D.tif"
  val test_image_3_path = "images/CompartmentTest3.tif"
  val test_image_1 = ImageProcessing.splitChannels(ImageIO.openImageFile(test_image_1_path)).head
  val test_image_2 = ImageProcessing.splitChannels(ImageIO.openImageFile(test_image_2_path)).head
  val test_image_3 = ImageIO.openImageFile(test_image_3_path)
  test("LoadImage") {
    test_image_1 shouldBe a [ij.ImagePlus]
  }

  test("MergeCompartments") {
    CompartmentProcessing.processImageToCompartments(test_image_1, 5).length should equal(4)
    CompartmentProcessing.processImageToCompartments(test_image_2, 5).length should equal(2)
  }
}
