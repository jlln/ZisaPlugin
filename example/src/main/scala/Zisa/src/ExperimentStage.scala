package Zisa.src

import ij.ImagePlus

/**
  * Created by james on 6/05/16.
  */


class ExperimentStage(stage_name:String,analysis_function:((Seq[ImagePlus],Cell)=>List[Seq[Result]])){
  def run(channels:Seq[ImagePlus], cell:Cell):CellResultCollection = new CellResultCollection(cell.getCondition,analysis_function(channels,cell).flatten)
}
