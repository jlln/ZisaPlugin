/**
  * Created by james on 3/05/16.
  */
/**
  * @author james
  */
import Zisa.{CompartmentProcessing, CompartmentSlice, ImageIO, ImageProcessing}
import ij.ImagePlus
import org.scalatest._

class CompartmentSuite extends FunSuite with Matchers {
  val test_image_1_path = "images/test_image_1.tif"
  val test_image_2_path = "images/0_3_R3D_D3D.tif"
  val test_image_3_path = "images/CompartmentTest3.tif"
  val test_image_1 = ImageProcessing.splitChannels(ImageIO.openImageFile(test_image_1_path)).head
  val test_image_2 = ImageProcessing.splitChannels(ImageIO.openImageFile(test_image_2_path)).head
  val test_image_3 = ImageIO.openImageFile(test_image_3_path)






  test("CreateCompartmentSlices"){
    val slices = CompartmentProcessing.identifyCompartmentSlices("0",test_image_1,5)
    val slice_ids = slices.map(_.getCompartment)
    slice_ids.distinct.length should equal(1)
    slice_ids.distinct.head should equal("0")
  }


  test("MergeCompartments") {
    CompartmentProcessing.processChannelToCompartments("0",test_image_1, 5).length should equal(4)
    CompartmentProcessing.processChannelToCompartments("0",test_image_2, 5).length should equal(2)
  }
}
