package Zisa

import ij.measure.ResultsTable
import ij.{ImagePlus, WindowManager}

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Dialog, Label}
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
      }
    }
  }

  val images = ImageIO.getImagePaths
  val (first_image,middle_slice) = ImageIO.prepareFirstImage(images.head)
  val channels = ImageProcessing.splitChannels(first_image)
  val experiment = InitialSettings.initialSettings(stage,channels)
  val compartment_channels = experiment.getChannels.map(i => channels(i))
  val compartment_working_images:Seq[ImagePlus] = compartment_channels.map(c=>c.duplicate())
  val radii = compartment_channels.map( c => CompartmentalizationSmoother.dialogSmoother(stage,c,middle_slice))
  val results_table = new ResultsTable()
  images.map(i=> ImageProcessing.processImage(experiment.getChannels,radii,i,results_table))







}



