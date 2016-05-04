package Zisa


/**
  * @author james
  */


import ij.ImagePlus
import ij.IJ
import ij.WindowManager
import java.io._
import java.util.concurrent.CountDownLatch

import ij.IJ._
import ij.gui.GenericDialog
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


  def firstImageAndBegin():(ij.ImagePlus,Array[String],Int) ={
    //Get the root directory.
    val rd:String = getDirectory()
    //Identify subdirectories
    val subdirectories:Array[String] = ImageIO.getListOfSubDirectories(rd)
    //Throw error and terminate if root directory contains no subdirectories
    if (subdirectories.length == 0) {
      error("No subdirectories found ")
      0
    }
    val images:Array[String] = subdirectories.flatMap(s => getListOfFilesInSubDirectory(s))
    val first_image = openImageFile(images.head)
    val middle_slice = scala.math.ceil(first_image.getNSlices/2).toInt
    (first_image,images,middle_slice)
  }


  
}

