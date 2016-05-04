package Zisa

import ij.ImagePlus

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
  val (first_image,images,middle_slice) = ImageIO.firstImageAndBegin()
  val channels = ImageProcessing.splitChannels(first_image)
  val experiment = InitialSettings.initialSettings(stage,channels)
  val compartment_channels = experiment.getChannels.map(i => channels(i))
  val compartment_working_images:Seq[ImagePlus] = compartment_channels.map(c=>c.duplicate())
  val radii = compartment_channels.map( c => CompartmentalizationSmoother.dialogSmoother(stage,c,middle_slice))
  val compartments:Seq[List[Compartment]] = compartment_working_images.zip(radii).map {
    case (c, r) => CompartmentProcessing.processImageToCompartments(c, r)
  }
  val cells = CompartmentProcessing.mergeCompartmentsToCells(List(),compartments.flatten.toList)
  cells.map{
    c=> println(c)
  }

  val focussed_cells = cells.map(_.focus)

  focussed_cells.map(x=>println(x))






  sys.exit(1)


}



