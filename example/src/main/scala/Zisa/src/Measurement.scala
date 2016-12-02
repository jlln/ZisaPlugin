package Zisa.src

import ij.ImagePlus
import ij.measure.ResultsTable
import ij.plugin.filter.Analyzer
import services.Stats

object Measurement {
  //make a function that accepts images and a cell and an experiment specification, and returns results.
  def conductExperiment(channels:Seq[ImagePlus],cell:Cell,specification:ExperimentSpecification):CellResultCollection = {
    val stages = specification.getExperimentStages
    val experiment_results = stages.map(s=> s.run(channels,cell))
    val cell_result = experiment_results.tail.foldLeft(experiment_results.head)(_.addNewResultCollection(_))
    cell_result
  }

  val measureCellularIntensity:((Seq[ImagePlus],Cell) => List[Seq[Result]]) = (channels:Seq[ImagePlus],cell:Cell) => {
    //Each compartment gets measurements of each the intensity of each channel.
    val experimentalCondition = cell.getCondition
    val results = cell.getCompartments.map { c =>
      {
        channels.zipWithIndex.map {
          case (image, image_label) => measureIntensity(image, c, experimentalCondition, image_label.toString)
        }
      }
    }
    results
  }

  def measureIntensity(channel:ImagePlus,compartment:Compartment,condition_label:String,image_label:String):Result = {
    //Measure the mean intensity of a given image channel across the slices of a compartment
    val intensity_results = new ResultsTable()
    val measurements = ij.measure.Measurements.MEAN
    val analyzer = new Analyzer(channel, measurements, intensity_results)
    for (s <- compartment.getSlices) {
      channel.setSlice(s.getSlice)
      channel.setRoi(s.getRoi)
      analyzer.measure()
    }
    val intensity_values = intensity_results.getColumn(intensity_results.getColumnIndex("Mean"))
    val compartment_name = compartment.getCompartment
    val areas = compartment.getSlices.map(_.getArea)
    val measurement_label = "Channel" + image_label + "Compartment" + compartment.getCompartment + "MeanIntensity"
    val result_entries = intensity_values.zip(areas).map{
      case (v,a) => new ResultEntry(compartment_name,measurement_label,Some(v),a)
    }
    new Result(compartment_name,condition_label,result_entries)
  }

  val measureCorrelationPearson:((Seq[ImagePlus],Cell) => List[Seq[Result]]) = (channels:Seq[ImagePlus],cell:Cell) => {
    val experimental_condition = cell.getCondition
    cell.getCompartments.map { c => {
      val compartment_name = c.getCompartment
      val compartment_slices = c.getSlices
      val compartment_coloc_pearson_entries: List[ResultEntry] = compartment_slices.flatMap {
        c => {
          val slice_area = c.getArea
          val slice_channels_pixels: Seq[Array[Float]] = channels.map(ch => c.getPixels(ch).flatten)
          correlationPearsonEntries(slice_channels_pixels, 0, slice_area, compartment_name)
        }
      }
      val coloc_measurement_labels = compartment_coloc_pearson_entries.map(_.getLabel).distinct
      val compartment_results = coloc_measurement_labels.map {
        l => {
          val entries = compartment_coloc_pearson_entries.filter(_.getLabel == l)
          new Result(compartment_name, experimental_condition, entries.toArray)
          }
        }
      compartment_results
      }
    }
  }

  def correlationPearsonEntries(channels:Seq[Array[Float]],n:Int,area:Int,compartment_name:String):Seq[ResultEntry] = {
    //This returns all the permutations of pairwise correlation measurements for the channels in a compartment.
    //It is therefore necessary to separate the result entries by their measurement_labels prior to aggregating them into results.
    val current_channel_lab = n+1
    if (channels.length == 2){
      val next_channel_lab = current_channel_lab+1
      val label = f"Channel$current_channel_lab%sChannel$next_channel_lab%sPearsonCorrelation"
      List(new ResultEntry(compartment_name,label,Some(Stats.correlationPearson(channels.head,channels.last)),area))
    }
    else{
      val current_channel = channels.head
      val channel_tail = channels.tail
      val current_pairs:Seq[ResultEntry] = channel_tail.zipWithIndex.map{
        case (c,i) =>{
          val next_channel_lab = current_channel_lab+i+1
          val label = f"Channel$current_channel_lab%sChannel$next_channel_lab%sPearsonCorrelation"
          new ResultEntry(compartment_name,label,Some(Stats.correlationPearson(current_channel,c)),area)
        }
      }
      current_pairs ++ correlationPearsonEntries(channels.tail,n+1,area,compartment_name)
    }
  }

  val blobAnalysis:((Seq[ImagePlus],Cell) => List[Seq[Result]]) = (channels:Seq[ImagePlus],cell:Cell) => {

    }








}
