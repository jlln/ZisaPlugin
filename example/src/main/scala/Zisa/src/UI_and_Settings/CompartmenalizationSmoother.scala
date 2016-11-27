package Zisa.src.UI_and_Settings

import java.text.NumberFormat

import ij.{IJ, ImagePlus, WindowManager}

import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, Region}
import scalafx.util.converter.FormatStringConverter


/**
  * Created by james on 2/05/16.
  */

object CompartmentalizationSmoother {

  //A function that creates an interactive dialog for the purpose of specifying compartmentalization in a single channel.
  // This function accepts the primary GUI stage and an image. It returns an integer.
  def dialogSmoother(stage:PrimaryStage,image:ImagePlus,middle_slice:Int):Int = {
    WindowManager.closeAllWindows()
    val preview_image = image.duplicate
    preview_image.setSlice(middle_slice)
    image.setSlice(middle_slice)
    image.getProcessor().resetMinAndMax()
    image.show()
    image.getWindow.setLocationAndSize(100,100,700,700)
    preview_image.show()
    preview_image.getWindow.setLocationAndSize(1200,100,700,700)
    IJ.run("Tile")
    val calibration = image.getCalibration()
    calibration.setUnit("micron")
    val outlier_radius = calibration.getRawX(1.9)
    val cell_lower = math.pow(calibration.getRawX(2),2)*3.14156
    val cell_upper = math.pow(calibration.getRawX(50),2)*3.14156
    val outlier_search_string = "radius="+outlier_radius+" threshold=50 which=Dark stack"
    val checkbox_full_preview = new CheckBox("FullPreview")
    def updateImagePreview(image_preview:ImagePlus,original_image:ImagePlus,radius:Int) = {
      val working_image = original_image.duplicate()
      IJ.run(working_image,"Gaussian Blur...", "sigma="+radius + " stack")
      if (checkbox_full_preview.selected.value) {
        IJ.run(working_image, "Make Binary", "method=RenyiEntropy background=Default calculate stack")
        IJ.run(working_image, "Fill Holes", "stack")
        IJ.run(working_image, "Remove Outliers...", outlier_search_string);
        IJ.run(working_image, "Watershed", "stack")
      }
      val wip = working_image.getStack()
      image_preview.setStack(image_preview.getTitle(),wip)
      image_preview.getProcessor.resetMinAndMax()
      image_preview.updateAndRepaintWindow()
    }
    var radius = 3
    updateImagePreview(preview_image,image,radius)
    val dialog = new Dialog[Int](){
      initOwner(stage)
      title = "Compartmentalization"
      headerText = "Compartmentalization"
    }
    val slider = new Slider(0,15,3)
    slider.value.onChange{(_,oldValue,newValue) => {
      radius = slider.getValue.toInt
      updateImagePreview(preview_image,image,radius)
      }
    }
    checkbox_full_preview.selected.onChange{(_,oldValue,newValue) => updateImagePreview(preview_image,image,radius)}

    val text_field = {
      val numberFormat = NumberFormat.getNumberInstance()
      val converter = new FormatStringConverter[Number](numberFormat)
      new TextField() {
        textFormatter = new TextFormatter(converter) {
          value <==> slider.value
        }
        maxWidth = 140
        maxHeight = Region.USE_COMPUTED_SIZE
      }
    }
    val button_accept = new ButtonType("Accept",ButtonData.OKDone)

    val grid = new GridPane{
      hgap = 10
      vgap = 10
      padding = Insets(20,100,10,10)
      add(slider,1,0,5,1)
      add(text_field,2,1)
      add(checkbox_full_preview,3,2)
    }
    val dialog_pane = new DialogPane(){
      content = grid
      buttonTypes = Seq(button_accept)
    }
    dialog.resultConverter = dialogButton => radius
    dialog.dialogPane() = dialog_pane
    dialog.showAndWait()
    preview_image.close()
    radius
  }

}