package Zisa.src

/**
  * Created by james on 8/09/16.
  */
class ExperimentSpecification(channels:Seq[Int], experiment_stages:List[ExperimentStage]){
  //Used to define the channels for compartmentalization, and the analyses to conduct.
  def getChannels = channels
  def getExperimentStages = experiment_stages
}
