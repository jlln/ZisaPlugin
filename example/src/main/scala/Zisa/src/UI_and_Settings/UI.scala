package Zisa.src.UI_and_Settings

import Zisa.src.{ImageIO, ImageProcessing}
import ij.ImagePlus
import ij.measure.ResultsTable

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType, Label}
import scalafx.scene.layout.BorderPane
/**
  * Created by james on 27/04/16.
  */

object UI extends JFXApp{

  //core thread for FX UI

  stage = new PrimaryStage{
    title = "Zisa"
    scene = new Scene{
      root = new BorderPane{
        padding = Insets(25)
        center = new Label("Zisa")
        bottom = new Label("Close this window to terminate the program.")
      }
    }
    alwaysOnTop = true
  }
  stage.show()
  val alert = new Alert(AlertType.Information) {
    initOwner(stage)
    title = "Zisa"
    headerText = "Welcome to the Z-Image-Analyser."
    contentText = "Ready to begin"
  }
  val result = alert.showAndWait()

  // React to user's selection
  result match {
    case Some(ButtonType.OK) => execute
    case _                   => System.exit(0)
  }


  // Execute Zisa
  val execute =  {
    val images_and_rd= ImageIO.getImagePaths
    val images = images_and_rd._1
    val rd = images_and_rd._2.last match {
      case '/' => images_and_rd._2
      case _ => images_and_rd._2 + "/"
    }
    val (first_image, middle_slice) = ImageIO.prepareFirstImage(images.head)
    val channels = ImageProcessing.splitChannels(first_image)
    val experiment = InitialSettings.initialSettings(stage, channels)
    val compartment_channels = experiment.getChannels.map(i => channels(i))
    val compartment_working_images: Seq[ImagePlus] = compartment_channels.map(c => c.duplicate())
    val radii = compartment_channels.map(c => CompartmentalizationSmoother.dialogSmoother(stage, c, middle_slice))
    val results_table = new ResultsTable()
    images.map(i => ImageProcessing.processImage(radii, i, results_table,experiment))
    results_table.save(rd + "ZisaAnalysis.csv")
    println("Results saved to" + rd + "ZisaAnalysis")
  }









}



