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


class ExperimentDefinition(channels:Seq[Int]){
  //Used to define the channels for compartmentalization, and the options for analysis
  def getChannels = channels
}
class Item(initialSelection: Boolean, val name: Int) {
  val selected = BooleanProperty(initialSelection)
  override def toString = name.toString
}


object InitialSettings {


  def initialSettings(stage:PrimaryStage,channels:Array[ImagePlus]): ExperimentDefinition ={

    val channels_with_index:Array[(ImagePlus,Int)] = channels.zipWithIndex
    val channel_indices = channels_with_index.map(_._2)

    val selection_data = ObservableBuffer[Item](
      channel_indices.map { i => new Item(false,i) }
    )

    val dialog = new Dialog[ObservableBuffer[Item]](){
      initOwner(stage)
      title = "InitialSettings"
      headerText = "InitialSettings"
    }

    val button_accept = new ButtonType("Accept",ButtonData.OKDone)
    val channel_checklist = new VBox {
      children = Seq(
        new Label("Select channels for subcellular compartmentalization"),
        new ListView[Item] {
          prefHeight=250
          items = selection_data
          cellFactory = CheckBoxListCell.forListView(_.selected)
        }
      )
    }




    val checklist_pane = new DialogPane(){
      content = channel_checklist
      buttonTypes = Seq(button_accept)
    }
    dialog.resultConverter = dialogButton => {
      selection_data
    }
    dialog.dialogPane = checklist_pane
    val selections:List[Item] = dialog.showAndWait().asInstanceOf[Option[ObservableBuffer[Item]]].get.toList // FIX ***
    val selected_channels:List[Int] = selections.filter(x=>x.selected.value).map(x=>x.name.toInt)
    if (selected_channels.isEmpty) new Error("No channels selected")
    new ExperimentDefinition(selected_channels)
  }

}
