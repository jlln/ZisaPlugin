package Zisa.test

import Zisa.src._
import org.scalatest._

class ThresholdSuite extends FunSuite with Matchers {
  test("KMeans2Groups"){
    val items = List(1,2,3,7,8,9)
    val standard_items = Stats.standardScores(items)
    val upper_group_cuttoff = Blobs.kMeans(2,standard_items) * Stats.standardDeviation(items) + Stats.mean(items)
    val upper_group = items.filter(x => x >= upper_group_cuttoff)
    upper_group shouldBe List(7,8,9)
  }
//
//
//  test("ApplyThreshold"){
//    val sample = ImageIO.openImageFile("/home/james/ZisaDev/ZisaPlugin/example/src/main/scala/Zisa/test/resources/test-1.tif")
//
//    val channels = ImageProcessing.splitChannels(sample)
//    val compartments:Seq[Compartment] = CompartmentProcessing.processChannelToCompartments("Blue",channels(2),6)
//    val cells:List[Cell] = CompartmentProcessing.mergeCompartmentsToCells("test", List(), compartments.toList)
//    val first_cell = cells.head
//    val thresholds = channels.map(c=>Blobs.thresholdCellChannel(5,c,first_cell))
//    val thresholded_channels = channels.zip(thresholds).map{
//      case (c,t)=> first_cell.applyThreshold(c,t)
//    }
//    val first_channel = thresholded_channels.head
//    first_channel.show()
//
//
//  }



}