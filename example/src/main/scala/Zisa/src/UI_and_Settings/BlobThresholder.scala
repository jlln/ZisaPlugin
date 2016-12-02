package Zisa.src.UI_and_Settings
import java.text.NumberFormat

import Zisa.src._
import ij.measure.ResultsTable
import ij.process.FloatProcessor
import ij.{IJ, ImagePlus, WindowManager}

import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{Cell => _, _}
import scalafx.scene.layout.{GridPane, Region}
import scalafx.util.converter.FormatStringConverter
/**
  * Created by james on 4/09/16.
  */
object BlobThresholder {

  def dialogBlobThreshold(blurring_radii:Seq[Int],image:ImagePlus,compartment_channels:Seq[Int],stage:PrimaryStage):Array[Int]= {

    val channels: Array[ImagePlus] = ImageProcessing.splitChannels(image)
    val compartments: Seq[List[Compartment]] = compartment_channels.zip(blurring_radii).map {
      case (c, r) => CompartmentProcessing.processChannelToCompartments(c.toString, channels(c), r)
    }
    val cells:List[Cell] = CompartmentProcessing.mergeCompartmentsToCells("", List(), compartments.flatten.toList)
    val focussed_cells = cells.map(_.focus())
    channels.map {
      channel => {
        val image_preview_arrays = ImageIO.tilePrepare(cells,channel)
        WindowManager.closeAllWindows()
        val original = image_preview_arrays
        val preview = image_preview_arrays
        val original_image = ImageIO.generateTiledImage(original)
        val preview_image = original_image
        def applyThresholdAndUpdatePreview(k: Int, image_array: Seq[Array[Array[Float]]], preview_image: ImagePlus): Unit = {
          val updated_cell_preview_arrays = image_array.map {
            c => {
              Blobs.prepareImage(k, Array(c)).head
            }
          }
          val updated_image = ImageIO.generateTiledImage(updated_cell_preview_arrays).getProcessor()
          preview_image.setProcessor(updated_image)
          preview_image.updateAndRepaintWindow()
        }
        var k = 5
        original_image.show()
        preview_image.show()
        IJ.run("Tile")
        applyThresholdAndUpdatePreview(k, image_preview_arrays, preview_image)

        val dialog = new Dialog[Int]() {
          initOwner(stage)
          title = "Thresholding"
          headerText = "Thresholding"
        }
        val slider = new Slider(2, 10, 5)
        slider.value.onChange { (_, oldValue, newValue) => {
          k = slider.getValue.toInt
          applyThresholdAndUpdatePreview(k, image_preview_arrays, preview_image)
        }
        }
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
        val button_accept = new ButtonType("Accept", ButtonData.OKDone)

        val grid = new GridPane {
          hgap = 10
          vgap = 10
          padding = Insets(20, 100, 10, 10)
          add(slider, 1, 0, 5, 1)
          add(text_field, 2, 1)
        }
        val dialog_pane = new DialogPane() {
          content = grid
          buttonTypes = Seq(button_accept)
        }
        dialog.resultConverter = dialogButton => k
        dialog.dialogPane() = dialog_pane
        dialog.showAndWait()
        preview_image.close()
        original_image.close()
        k
      }
    }
  }

  def prepareBlobAnalysisFunction(blurring_radii:Seq[Int],image:ImagePlus,compartment_channels:Seq[Int],stage:PrimaryStage):((Seq[ImagePlus],Cell)=>List[Seq[Result]]) = {
    val thresholds = dialogBlobThreshold(blurring_radii,image,compartment_channels,stage)

  }





}
