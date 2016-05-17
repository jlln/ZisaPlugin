Z-image-stack analyzer (Zisa): An ImageJ plugin for the automated analysis of immunofluorescence Z-stack images.

Installation:

1) Download and install the Java SE Runtime for your platform from http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html

2) Download the Platform Independent version of ImageJ from http://rsb.info.nih.gov/ij/download.html

3) Extract and run ImageJ.exe. You may be prompted to provide the location of javaw.exe. This should be at C:/ProgramFiles/Java/jre1.8.091/Bin/javaw.exe

4) Download the zip file from https://github.com/jlln/ZisaPlugin/releases/tag/0.51 , and extract the files into the ImageJ plugins directory (the folder called "Plugins" in the main ImageJ folder).

5) Open the file ImageJ.cfg in the ImageJ folder. Find a line that looks like:
  -Xmx1500m -cp ij.jar ij.ImageJ
  And change the 1500 (or 640 or whatever it is) to 2048
  Save and close the file.

6) Launch ImageJ. Go to the plugins menu and click install, then navigate to the ImageJ plugins directory and select zisaplugin_2.11-0.5.0.jar. When prompted to choose a save location, choose the same folder and accept the prompt to overwrite the existing file.

7) Close and Reopen ImageJ. Zisa should now be present in the plugins menu.

Usage: The program requires 

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


2) Start the Zisa plugin and you will be asked to select the root directory for the experiment. Once you have done this you will be prompted to select the compartmentalization channels for the experiment. These will be listed in the order in which they appear in the images. To understand what these compartments do, have a look at this diagram:

![alt tag](https://github.com/jlln/ZisaPlugin/blob/master/example/src/Illustration.png)
