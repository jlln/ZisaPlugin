package Zisa

/**
  * Created by james on 27/04/16.
  */


import ij.ImagePlus
import ij.plugin.ChannelSplitter
object ImageProcessing {


  def splitChannels(image:ImagePlus):Array[ImagePlus] = {
    ChannelSplitter.split(image)
  }

}
