Z-image-stack analyzer (Zisa): An ImageJ plugin for the automated analysis of immunofluorescence Z-stack images.

Installation:

At this time the plugin does not play nicely with Fiji. The best approach to get the plugin working is as follows, and involves creating a separate copy of ImageJ for use with Zisa:

1) Download and install the Java SE Runtime 8u91 for your OS from http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html

2) Download the Platform Independent version of ImageJ from http://rsb.info.nih.gov/ij/download.html

3) Extract and run ImageJ.exe. You may be prompted to provide the location of javaw.exe. This should be at C:/ProgramFiles/Java/jre1.8.091/Bin/javaw.exe

4) Download the zip file from https://github.com/jlln/ZisaPlugin/releases/tag/0.51 , and extract the files into the ImageJ plugins directory (the folder called "Plugins" in the main ImageJ folder).

5) Open the file ImageJ.cfg in the ImageJ folder. Find a line that looks like:
  -Xmx1500m -cp ij.jar ij.ImageJ
  And change the 1500 (or 640 or whatever it is) to 2048
  Save and close the file.

6) Launch ImageJ. Go to the plugins menu and click install, then navigate to the ImageJ plugins directory and select zisaplugin_2.11-0.5.0.jar. When prompted to choose a save location, choose the same folder and accept the prompt to overwrite the existing file.

7) Close and Reopen ImageJ. Zisa should now be present in the plugins menu.

Usage: 

I strongly suggest reading the full instructions before attempting to use the plugin.

1) You will need full stack images in tiff format (ie a single tiff containing all slices and channels from the image). These can be created using Fiji. The channels will need to be in the same order for each image. The images need to be saved in separate directories for each experimental condition; the names of these subdirectories will be used to label the output of the analysis. 
A typical experiment directory structure might look like this:

Experiment Directory -- 
  --Condition1
      --Image1.tif
      --Image2.tif
  --Condition2
      --Image3.tif
      --Image4.tif


Make sure that there are no other tif files present in the experimental directories.


2) I recommend moving the ImageJ toolbox to the top right hand corner of the screen at this point. Start the Zisa plugin and you will be asked to select the root directory for the experiment (Once you have launched the plugin, ImageJ needs to be restarted before it can be used again).
Once you have chosen the directory you will be prompted to select the compartmentalization channels for the experiment. These will be listed in the order in which they appear in the images. To understand what these compartments mean, have a look at this diagram:

![alt tag](https://github.com/jlln/ZisaPlugin/blob/master/example/src/Illustration.png)

As you can see, these channels will be used to define spatial zones of analysis. These zones are used to both define and compartmentalize the cells. The intensity of every channel in the image within these compartments will then be measured.

3) Once you have selected the channels, you will be shown two images and a slider dialog. If you click the full preview checkbox, you will be shown the defined boundaries based on the currently chosen parameter. You can change the parameter if necessary (changing the parameter with full preview selected can be quite slow), until the compartments are properly defined. You can check the other slices using the z-axis sliders on the preview image. You will be asked to repeat this process for each compartmentalization channel you have chosen.

4) At this point the process becomes fully automatic. I suggest observing the process until you are comfortable that it is working correctly. If you click on the wrong thing at this it can cause the program to crash, so I suggest looking but not touching.

When the program is finished it will display a box labeled "Zisa" and an ImageJ table containing your results. You need to save your results from this table (by clicking File... Save As) before closing the Zisa window.



