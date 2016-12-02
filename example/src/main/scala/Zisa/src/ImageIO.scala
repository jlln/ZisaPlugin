package Zisa.src

/**
  * @author james
  */


import java.io._

import ij.IJ._
import ij.{IJ, ImagePlus, WindowManager}
import ij.io.DirectoryChooser
import ij.process.{FloatProcessor, ImageProcessor}
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


  def tilePrepare(cells:Seq[Cell],image:ImagePlus):Seq[Array[Array[Float]]] = {
//    Generate a set of padded sub-images ready to make a nice looking tiled image.
    val cell_bounds = cells.map(_.getBoundingBox)
    val heights = cell_bounds.map(_.getFloatHeight.toInt)
    val widths = cell_bounds.map(_.getFloatWidth.toInt)
    val max_width = widths.max
    val max_height = heights.max
    val padding_widths = widths.map(max_width - _)
    val padding_heights = heights.map(max_height - _)
    val cell_pixel_arrays = cells.map{
      cell => {
        val middle_slice = scala.math.floor(services.Stats.mean(cell.getCompartments.map(_.getImageSlices).flatten)).toInt
        image.setSlice(middle_slice)
        image.setRoi(cell.getBoundingBox)
        image.getChannelProcessor.crop().getFloatArray()
      }
    }
    val padded_pixel_arrays = padding_heights.zip(padding_widths).zip(cell_pixel_arrays).map{
      case ((width,height),pixels) => {
        val padding_width_left = scala.math.floor(width/2.0).toInt
        val padding_width_right = scala.math.ceil(width/2.0).toInt
        val width_padded_pixels = pixels.map{
          row => Array.fill[Float](padding_width_left)(0f) ++ row ++ Array.fill[Float](padding_width_right)(0f)
        }
        val padding_height_top = scala.math.floor(height/2.0).toInt
        val padding_height_bottom = scala.math.ceil(height/2.0).toInt
        val top_padding = Array.fill[Array[Float]](padding_height_top)(Array.fill[Float](width_padded_pixels.head.length)(0f))
        val bottom_padding = Array.fill[Array[Float]](padding_height_bottom)(Array.fill[Float](width_padded_pixels.head.length)(0f))
        top_padding ++ width_padded_pixels ++ bottom_padding
      }
    }
    padded_pixel_arrays
  }


  def generateTiledImage(padded_image_arrays:Seq[Array[Array[Float]]]):ImagePlus = {
    padded_image_arrays.map{
      pi => {
        println(pi.head.length,pi.length)
      }
    }
    val grid_width = scala.math.floor(scala.math.sqrt(padded_image_arrays.length)).toInt
    val grid_height = padded_image_arrays.length / grid_width
    val grid_rows:Seq[Array[Array[Float]]] = padded_image_arrays.grouped(grid_width).toSeq.map{
      grouped_cells => {
        grouped_cells.tail.foldLeft(grouped_cells.head){
          case (cell_a,cell_b) => {
            if (cell_a.length != cell_b.length){
              throw new Exception("Cell Heights not Equal")
            }
            cell_a.zip(cell_b).map{

              case (row_a,row_b) => {
                if (row_a.length != row_b.length){
                  throw new Exception("Cell Widths not Equal")
                }
                row_a ++ row_b
              }
            }
          }
        }
      }
    }
    val assembled_grid:Array[Array[Float]] = grid_rows.tail.foldLeft(grid_rows.head)(_ ++ _)
    val ip = new FloatProcessor(assembled_grid)
    ip.resetMinAndMax()
    val image = new ImagePlus("Tiled Cells",ip)
    image
  }






  
}

