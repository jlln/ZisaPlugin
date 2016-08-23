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



class Setting(initialSelection: Boolean, val name: String) {
  val selected = BooleanProperty(initialSelection)
  override def toString = name
}





object InitialSettings {


  def initialSettings(stage:PrimaryStage,channels:Array[ImagePlus]): ExperimentSpecification ={

    val channels_with_index:Array[(ImagePlus,Int)] = channels.zipWithIndex
    val channel_indices = channels_with_index.map(_._2)

    val channel_selection_data = ObservableBuffer[Setting](
      channel_indices.map { i => new Setting(false,i.toString) }
    )


    val dialog = new Dialog[ObservableBuffer[Setting]](){
      initOwner(stage)
      title = "InitialSettings"
      headerText = "InitialSettings"
    }

    val button_accept = new ButtonType("Accept",ButtonData.OKDone)
    val channel_checklist = new VBox {
      children = Seq(
        new Label("Select channels for subcellular compartmentalization"),
        new ListView[Setting] {
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
    val selections:List[Setting] = dialog.showAndWait().asInstanceOf[Option[ObservableBuffer[Setting]]].get.toList // BAD PRACTICE***
    val selected_channels:List[Int] = selections.filter(x=>x.selected.value).map(x=>x.name.toInt)
    if (selected_channels.isEmpty) new Error("No channels selected")
    new ExperimentSpecification(selected_channels)
  }

}
