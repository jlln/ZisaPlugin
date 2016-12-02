package Zisa.src

import ij.ImagePlus

import scalafx.application.JFXApp.PrimaryStage

/**
  * Created by james on 6/05/16.
  */

//This class should be generated during the initial set-up, and should contain the analysis function and any parameters required.
class ExperimentStageOption(stage_name:String,analysisFunctionPrep:(Int,ImagePlus,Seq[Int],PrimaryStage) =>((Seq[ImagePlus],Cell)=>List[Seq[Result]]) ){
  def createExperimentStage(blurring_radii:Seq[Int],image:ImagePlus,compartment_channels:Seq[Int],stage:PrimaryStage):((Seq[ImagePlus],Cell)=>List[Seq[Result]]) = {


  }
}


class ExperimentStage(stage_name:String,analysis_function:((Seq[ImagePlus],Cell)=>List[Seq[Result]])){
  def run(channels:Seq[ImagePlus], cell:Cell):CellResultCollection = new CellResultCollection(cell.getCondition,analysis_function(channels,cell).flatten)
}
