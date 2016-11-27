package Zisa.src.UI_and_Settings

/**
  * Created by james on 4/09/16.
  */
object BlobThresholder {


  //A function that creates an interactive dialog for the purpose of specifying thresholding parameters in a single channel.
  // This function accepts the primary GUI stage and an image. It returns an integer.
//  def dialogThresholder(stage:PrimaryStage, image:ImagePlus, middle_slice:Int):Float = {
//    WindowManager.closeAllWindows()
//    val preview_image = image.duplicate
//    preview_image.setSlice(middle_slice)
//    image.setSlice(middle_slice)
//    image.show()
//    image.getWindow.setLocationAndSize(100,100,700,700)
//    preview_image.show()
//    preview_image.getWindow.setLocationAndSize(1200,100,700,700)
//    IJ.run("Tile")
//    def updateImagePreview(image_preview:ImagePlus,original_image:ImagePlus,radius:Int) = {
//      val working_image = original_image.duplicate()
//      IJ.run(working_image,"Gaussian Blur...", "sigma="+radius + " stack")
//
//      IJ.run(working_image, "Make Binary", "method=RenyiEntropy background=Default calculate stack")
//      IJ.run(working_image, "Fill Holes", "stack")
//      IJ.run(working_image, "Remove Outliers...", outlier_search_string);
//      IJ.run(working_image, "Watershed", "stack")
//
//      val wip = working_image.getStack()
//      image_preview.setStack(image_preview.getTitle(),wip)
//      image_preview.getProcessor.resetMinAndMax()
//      image_preview.updateAndRepaintWindow()
//    }
//  }
}
