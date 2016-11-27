package Zisa.src
/**
  * @author james
  */
import ij._
import ij.plugin.filter.ParticleAnalyzer
import services.Stats

object Blobs{



  def kMeansIter(items:List[Float],groups:List[List[Float]]):Double = {
    val group_means:List[Double] = groups.map(g=>Stats.mean(g))
    val group_boundaries = group_means.sliding(2).map{
      p=>Stats.mean(p)
    }.toList
    val new_groups = (0 until group_boundaries.length).map{
      i=> i match{
        case 0 => items.filter(x=> x <= group_boundaries(i))
        case z => items.filter(x=> x <= group_boundaries(i) && x > group_boundaries(i-1))
      }
    }.toList
    println(new_groups)
    if (new_groups == groups) group_boundaries.last
    else kMeansIter(items,new_groups)
  }

  def kMeans[T](k:Int,items:Traversable[T])(implicit n: Numeric[T]):Double = {
    val fitems = items.toList.map(x=> n.toFloat(x))
    val interval = 2 * 1f/(k-1)
    val starting_groups:List[List[Float]] = (-1f until 1+interval by interval).toList.map(v => List(v))
    kMeansIter(fitems,starting_groups)
  }

  def nearestNeighbours(object_centroids:List[(Float,Float)]):List[Double] = {
    object_centroids.length match{
      case 0 => List(0)
      case 1 => List(0)
      case _ => object_centroids.map{
        c => {
          val other_points = object_centroids.filter{x=> x!= c}
          val distances = other_points.map{
            case (x,y) => {Stats.eucledian(c._1,c._2,x,y)}
          }
          distances.min
        }
      }
    }
  }

  def measurementStats[T](items:Traversable[T])(implicit n:Numeric[T]) : List[Option[Double]] = {
    val stats = List(Stats.mean(items),Stats.standardDeviation(items),Stats.skewness(items),Stats.kurtosis(items))
    stats.map{x=> x match{
      case x if x.isNaN => None
      case x => Some(x)
    }}
  }


  def analyzeBlobsInSlice(compartment:String,condition:String,pixels:Array[Array[Int]],colour:String,nuclear_slice_centroid:(Double,Double)):Array[ResultEntry] = {
    val image = ImageIO.makeImage(pixels)
    ij.IJ.run(image,"Make Binary", "method=Default background=Dark stack ")
    val image_width = pixels.head.length
    val image_height = pixels.length
    val slice_area = image_width * image_height
    var roim= new ij.plugin.frame.RoiManager()
    var results_table= new ij.measure.ResultsTable()
    val pa = new ParticleAnalyzer(ParticleAnalyzer.ADD_TO_MANAGER,
      ij.measure.Measurements.MEAN+ij.measure.Measurements.CENTROID+ij.measure.Measurements.AREA,
      results_table,
      1,20000,
      0,1.0)
    pa.analyze(image)
    val areas_index = results_table.getColumnIndex("Area")
    val labels = Array("BlobCount","MeanBlobSize","SDBlobSize","SkewnessBlobSize","KurtosisBlobSize",
      "MeanRadialBlobDistance","SDRadialBlobDistance","SkewnessRadialBlobDistance","KurtosisRadialBlobDistance",
      "MeanNearestNeighbour","SDNearestNeigbour","SkewnessNearestNeighbour","KurtosisNearestNeighbour").map{x=>colour+x}
    if (areas_index== -1){
      labels.map(l=>new ResultEntry(compartment,l,None,slice_area)).updated(0,new ResultEntry(compartment,colour+"BlobCount",Some(0),slice_area))
    }
    else {
      val areas:Array[Float] = results_table.getColumn(areas_index)
      val x_centres:Array[Float] = results_table.getColumn(results_table.getColumnIndex("X"))
      val y_centres:Array[Float] = results_table.getColumn(results_table.getColumnIndex("Y"))
      val centroids = x_centres.zip(y_centres)
      val nearest_neighbours = nearestNeighbours(centroids.toList)
      val slice_centre_x:Double = nuclear_slice_centroid._1
      val slice_centre_y:Double = nuclear_slice_centroid._2
      val radiality:Seq[Double] = centroids.map{
        case (x,y) => (Stats.eucledian(x,y,slice_centre_x,slice_centre_y))/slice_area
      }
      val areas_stats = measurementStats(areas)
      val nearest_neighbour_stats = measurementStats(nearest_neighbours)
      val radiality_stats = measurementStats(radiality)
      val blob_count = Some(areas.length.toDouble)
      val result_entry_values:List[Option[Double]] = List(blob_count) ++ areas_stats ++
        radiality_stats ++ nearest_neighbour_stats
      val result_entries = labels.zip(result_entry_values).map{
        case (l,v) => new ResultEntry(compartment,l,v,slice_area)
      }
      WindowManager.closeAllWindows()
      result_entries
    }
  }
  def analyzeAllBlobsInCompartmentInChannel(compartment:String,condition:String,pixels:Array[Array[Array[Int]]],colour:String,centroids:List[(Double,Double)]):Array[ResultEntry] = {
    //All the blobs in all the slices in one compartment, for one channel.
    pixels.zip(centroids).flatMap{
      case (pixels,centroid)=>analyzeBlobsInSlice(compartment,condition,pixels,colour,centroid)
    }
  }
}