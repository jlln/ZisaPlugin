package Zisa.src.UI_and_Settings

import Zisa.src.{ExperimentSpecification, ExperimentStage, Measurement}
import ij.ImagePlus

import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.BooleanProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.control.cell.CheckBoxListCell
import scalafx.scene.layout.{HBox, VBox}

/**
  * Created by james on 3/05/16.
  */

class ChannelSetting(initialSelection: Boolean, name: String,contents:Int) {
  val selected = BooleanProperty(initialSelection)
  override def toString = name
  val getContents = contents
}

class MeasurementSetting(initialSelection: Boolean, name: String,contents:ExperimentStage) {
  val selected = BooleanProperty(initialSelection)
  override def toString = name
  val getContents = contents
}

object InitialSettings {

  case class UserInput(c:List[Int],m:List[ExperimentStage])

  def initialSettings(stage: PrimaryStage, channels: Array[ImagePlus]): ExperimentSpecification = {

  //Determine the choices that will be presented
    //For compartments
    val channels_with_index: Array[(ImagePlus, Int)] = channels.zipWithIndex
    val channel_indices = channels_with_index.map(_._2)
    val channel_selection_data = ObservableBuffer[ChannelSetting](
      channel_indices.map { i => new ChannelSetting(false, i.toString, i) }
    )
    //For Measurments
    val measurement_selection_data = ObservableBuffer[MeasurementSetting](
      new MeasurementSetting(false, "StainingIntensity", new ExperimentStage("StainingIntensity",Measurement.measureCellularIntensity)),
      new MeasurementSetting(false, "PearsonCorrelation", new ExperimentStage("StainingIntensity",Measurement.measureCorrelationPearson))
    )
  //The ScalaFX code to present the checklists and interpret the user input
    val dialog = new Dialog[UserInput]() {
      initOwner(stage)
      title = "Experiment Settings"
      headerText = "Select Compartments and Experiment Types"
    }
    val button_accept = new ButtonType("Accept",ButtonData.OKDone)
    dialog.dialogPane().buttonTypes = Seq(button_accept, ButtonType.Cancel)

    //Create the checklists and their associated inputs
    val compartment_checklist = new ListView[ChannelSetting] {
      prefHeight=250
      items = channel_selection_data
      cellFactory = CheckBoxListCell.forListView(_.selected)
    }
    val measurement_checklist = new ListView[MeasurementSetting] {
      prefHeight=250
      items = measurement_selection_data
      cellFactory = CheckBoxListCell.forListView(_.selected)
    }
    //Show the options to the user
    val dialog_content = new HBox{
      spacing = 30.0
      children = Seq(
        //Compartments
        new VBox{
          children = Seq(
            new Label("Select channels for subcellular compartmentalization"),
            new ListView[ChannelSetting] {
              prefHeight = 250
              items = channel_selection_data
              cellFactory = CheckBoxListCell.forListView(_.selected)
            }
          )
        }
        //Measurements
      ,new VBox{
          children = Seq(
            new Label("Select measurements for experiment"),
            new ListView[MeasurementSetting]{
              prefHeight = 250
              items = measurement_selection_data
              cellFactory = CheckBoxListCell.forListView(_.selected)
            }
          )
        }

      )
    }
    //Should put validation code here: disable the OK button until at least one channel and measurement have been selected.

    //Put the contents into the dialog
    dialog.dialogPane().content = dialog_content
    //Get the results out of the dialog
    dialog.resultConverter = dialogButton => {
        val channels_selected:List[Int] = channel_selection_data.filter(_.selected.value).map(_.getContents).toList
        val measurements_selected:List[ExperimentStage]  = measurement_selection_data.filter(_.selected.value).map(_.getContents).toList

        new UserInput(channels_selected,measurements_selected)
      }
    //Execute the dialog.
    val results = dialog.showAndWait()
    //Parse the user results
    results match{
      case Some(UserInput(c,m)) => new ExperimentSpecification(c,m)
      case None => throw new Exception("Nothing Selected")
      }
    }

}
