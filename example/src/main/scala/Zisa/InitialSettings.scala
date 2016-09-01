package Zisa

import ij.ImagePlus

import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.BooleanProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.cell.CheckBoxListCell
import scalafx.scene.control._
import scalafx.scene.layout.VBox
import scalafx.Includes._
import scalafx.scene.control.ButtonBar.ButtonData
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


  def initialSettings(stage: PrimaryStage, channels: Array[ImagePlus]): ExperimentSpecification = {

    val channels_with_index: Array[(ImagePlus, Int)] = channels.zipWithIndex
    val channel_indices = channels_with_index.map(_._2)

    val channel_selection_data = ObservableBuffer[ChannelSetting](
      channel_indices.map { i => new ChannelSetting(false, i.toString, i) }
    )

    val experiment_measurement_selection = ObservableBuffer[MeasurementSetting](
      new MeasurementSetting(false, "StainingIntensity", new ExperimentStage("StainingIntensity",Measurement.measureCellularIntensity))
    )


    val dialog = new Dialog[ObservableBuffer[ChannelSetting]](){
      initOwner(stage)
      title = "Compartment Settings"
      headerText = "Compartment Settings"
    }

    val button_accept = new ButtonType("Accept",ButtonData.OKDone)
    val channel_checklist = new VBox {
      children = Seq(
        new Label("Select channels for subcellular compartmentalization"),
        new ListView[ChannelSetting] {
          prefHeight=250
          items = channel_selection_data
          cellFactory = CheckBoxListCell.forListView(_.selected)
        }
      )
    }

    val checklist_pane = new DialogPane(){
      content = channel_checklist
      buttonTypes = Seq(button_accept)
    }
    dialog.resultConverter = dialogButton => {
      channel_selection_data
    }
    dialog.dialogPane = checklist_pane
    val selections:List[ChannelSetting] = dialog.showAndWait().asInstanceOf[Option[ObservableBuffer[ChannelSetting]]].get.toList // BAD PRACTICE***
    val selected_channels:List[Int] = selections.filter(x=>x.selected.value).map(x=>x.toString.toInt)
    if (selected_channels.isEmpty) new Error("No channels selected")



    val experiment_stages: List[ExperimentStage] = experiment_measurement_selection.toList.map(_.getContents)
    new ExperimentSpecification(selected_channels, experiment_stages)
  }
}
