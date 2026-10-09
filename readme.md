Note: This fork is a derivative of the EPA GLIMPSE Project. GLIMPSE-CE is not sponsored by EPA.

# GLIMPSE-CE Project

## Overview

GLIMPSE-CE https://github.com/DLoughlin/GLIMPSE-CE) is a derivative of GLIMPSE (https://epa.gov/glimpse), a graphical user interface for the GCAM (Global Change Analysis Model; https://github.com/JGCRI/gcam-core) open-source human-Earth systems model.

GLIMPSE-CE development is organized by Dan Loughlin (danloughlin@gmail.com). Contributors include Margaret Loughlin and Prabhat Hegde.

GCAM development is organized by the Joint Global Change Research Institute (JGCRI; https://www.pnnl.gov/jgcri).

Please note: GLIMPSE-CE is a separate project from PNNL's GLIMPSE, which is a tool for visualizing power grids and not related to GCAM or integrated assessment modeling.

## Requirements

We recommend installation on computers with 20 GB of RAM or more and with more than 100 GB of free hard disk space. GLIMPSE consists of two major components: the GLIMPSE-ScenarioBuilder and the GLIMPSE-ModelInterface. The GLIMPSE-ScenarioBuilder currently requires Windows 10 or Windows 11 (although a Mac/Linux version is under development). The GLIMPSE-ModelInterface can be used independently and has been succesfully tested on Mac and Linux operating systems.

## Important information

Please see the User's Guide, which can be found here: https://github.com/DLoughlin/GLIMPSE-CE/tree/main/docs/GLIMPSE-CE-UsersGuide, for installation instructions. We also recommend the tutorials as a good starting place for learning to operate many of GLIMPSE-CE's features.

Several additional notes for consideration:

* You can find the full GLIMPSE downloadable package at the "Releases" link to the right (GLIMPSE-CEx.zip). Those who would like to use the GLIMPSE-ModelInterface independently from the rest of the GLIMPSE package can download that executable and associated files (GLIMPSE-CE-ModelInterface-Only-x.zip).
* Please do not install GLIMPSE in a folder that includes spaces in its full path.
* It is recommended that you modify your computer's power settings such that it will not go to sleep while GCAM is running.
* Please wait for the GLIMPSE-CE*.zip file to fully download before unzipping.
* Some Windows computers automatically disable execution rights for downloaded EXE and BAT files. Double-clicking will bring up a warning window. Click on the "More Info" button, which will reveal a "Run Anyway" button. This will change the permissions and allow you to execute that file. Alternatively, on some computers, you may need to right-click on the file and choose to unblock it.
* When naming folders, scenarios, and scenario components, please use alpha-numerical characters, as well as "_" or "-". Spaces or special characters such as ">", "\", "%", or "$" may cause problems when the GLIMPSE software parses the text.
* Windows limits file paths to 256 characters. Because GLIMPSE and GCAM involve many nested folders, some users have experienced problems when installing GLIMPSE to a folder that has a long path. We recommend installing in a location such as C:\Projects\GLIMPSE or C:\Users\USERNAME\local_folder to avoid this problem.
* As indicated in the Users' Guide, please do not install GLIMPSE to a location that is continuously backed up, such as OneDrive, as this may lead to model execution and synchronization issues.
* The Contrib folder includes files that exceed GitHub size limits if it is unzipped. Unzip this folder after downloading to have access to its contents.
* If GLIMPSE-CE is running very slowly on your computer, you may need to exclude the java.exe and javaw.exe executables from scanning.

## Starting GLIMPSE-CE

* To start GLIMPSE double-click on "run_GLIMPSE-CE_GCAM-USA-9.1.bat". The tutorials in the Users' Guide were developed for a prior version of GLIMPSE and GCAM, but can still be used. There may be some differences in software appearance and options.

### Disclaimer

This code is provided on an "as is" basis and the user assumes responsibility for its use. GLIMPSE-CE developers have relinquished control of the information and no longer has responsibility to protect the integrity , confidentiality, or availability of the information.  Any reference to specific commercial products, processes, or services by service mark, trademark, manufacturer, or otherwise, does not constitute or imply their endorsement, recommendation or favoring.

### Acknowledgements

Contributors to software development of the EPA GLIMPSE software from which GLIMPSE-CE is derived include the following:
EPA - Dan Loughlin (ret'd), Tai Wu (ret'd), Chris Nolte (ret'd)
ORISE - Farid Alborzi
ARA - Aaron Parks, Yadong Xu





