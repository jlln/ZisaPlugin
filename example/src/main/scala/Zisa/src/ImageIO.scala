package Zisa.src

/**
  * @author james
  */


import java.io._

import ij.IJ._
import ij.{IJ, ImagePlus, WindowManager}
import ij.io.DirectoryChooser
object ImageIO {
  def openImageFile(filepath:String):ij.ImagePlus = {
    println("Loading Image: " + filepath)
    IJ.openImage(filepath)
  }
  def makeImage(image:Array[Array[Int]]):ij.ImagePlus = {
    val image_height:Int = image.length
    val image_width:Int  = image.head.length
    val processor = new ij.process.ByteProcessor(image_height,image_width)
    processor.setIntArray(image)
    processor.resetMinAndMax()
    new ij.ImagePlus("thresholded_image",processor)
  }


  def drawPixels(image:ij.ImagePlus){
    image.show()
    ij.IJ.run("Tile")
    Thread.sleep(500)
    WindowManager.closeAllWindows()
  }

  def getDirectory() = {
    val dc = new DirectoryChooser("Choose Dataset Root Directory")
    dc.getDirectory()

  }

  def getListOfSubDirectories(directory_name: String): Array[String] =
    (new File(directory_name)).listFiles.filter(_.isDirectory).map(_.getName)
    .map(s=> directory_name + "/" + s)

  def getListOfFilesInSubDirectory(directory_name: String): Array[String] =
    (new File(directory_name)).listFiles.map(_.getName)
      .filter(_.contains(".tif"))
      .map(s=> directory_name + "/" + s)



  def getImagePaths():(Array[String],String) = {
    val rd = getDirectory()
    val subdirectories:Array[String] = ImageIO.getListOfSubDirectories(rd)
    if (subdirectories.length == 0) {
      error("No subdirectories found ")
      0
    }
    val images:Array[String] = subdirectories.flatMap(s => getListOfFilesInSubDirectory(s))
    if (images.length == 0){
      error("No tif files found")
      0
    }
    (images,rd)
  }

  def prepareFirstImage(image_path:String):(ij.ImagePlus,Int) = {
    val first_image = openImageFile(image_path)
    val middle_slice = scala.math.ceil(first_image.getNSlices/2).toInt
    (first_image,middle_slice)
  }






  
}

