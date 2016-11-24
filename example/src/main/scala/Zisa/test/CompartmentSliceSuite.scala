package Zisa.test

import Zisa.src.{CompartmentProcessing, ImageIO, ImageProcessing}
import org.scalatest.FunSuite

/**
  * Created by james on 1/09/16.
  */
class CompartmentSliceSuite extends FunSuite{

  test("GetMask"){
    val test_images_dir = "/home/james/ZisaDev/ZisaPlugin/example/src/main/scala/Zisa/test/resources/"
    val test_image_1_path = test_images_dir +"test-1.tif"
    val test_image = ImageIO.openImageFile(test_image_1_path)
    val test_image_blue = ImageProcessing.splitChannels(test_image)(2)
    val compartments = CompartmentProcessing.processChannelToCompartments("0",test_image_blue, 5)
    val test_image_red = ImageProcessing.splitChannels(test_image)(0)
    val compartment_masks = compartments.head.getMaskingPixels(test_image_red)

    val preview_image = ImageIO.makeImage(compartment_masks(18))
    ImageIO.drawPixels(preview_image)
  }

  test("GetPixels"){
    val test_images_dir = "/home/james/ZisaDev/ZisaPlugin/example/src/main/scala/Zisa/test/resources/"
    val test_image_1_path = test_images_dir +"test-1.tif"
    val test_image = ImageIO.openImageFile(test_image_1_path)
    val test_image_blue = ImageProcessing.splitChannels(test_image)(2)
    val compartments = CompartmentProcessing.processChannelToCompartments("0",test_image_blue, 5)
    val test_image_red = ImageProcessing.splitChannels(test_image)(0)
    val red_pixels = compartments.head.getPixels(test_image_red).head
    val red_width = red_pixels.head.length
    val red_max = red_pixels.flatten.max
    val scaled_red_pixels:Array[Array[Int]] = red_pixels.flatten.map{
      x => (255 *x/red_max).toInt
    }.grouped(red_width).toArray
    val red_preview = ImageIO.makeImage(scaled_red_pixels)
    ImageIO.drawPixels(red_preview)
  }
}
