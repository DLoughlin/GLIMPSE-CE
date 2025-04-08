The latest version of GLIMPSE (v1.1 sub-release 2025.03.14) can be obtained by downloading the GLIMPSE-v1.1-release-2025.03.14.zip and following the installation instructions in the Users' Guide (GLIMPSE-v1.1_UsersGuide_508_Compliant.pdf), which can be found in the docs folder. 

Currently, GLIMPSE is only supported on Windows 10 and 11 operating systems.
The GLIMPSE-ModelInterface can be executed on additional platforms that support Java. 

----
Resource requirements: 
- 16 GB of RAM or more are required, 32 GB is recommended
- at least 100 free GB of space is recommended 

----
A few additional notes:

* Please do not install GLIMPSE in a folder that includes spaces in its full path. 

* It is recommended that you modify your computer's power settings such that it will not go to sleep while GCAM is running.

* Please wait for the GLIMPSE-v1.1*.zip file to fully download before unzipping.

* Some Windows computers automatically disable downloaded EXE and BAT files. Double-clicking will bring up a warning window. Click on the "More Info" button, which will reveal a "Run Anyway" button. This will change the permissions and allow you to execute that file. Alternatively, on some computers, you may need to right-click on the file and choose to unblock it. 

* When naming folders, scenarios, and scenario components, please use alpha-numerical characters, as well as "_" or "-". Spaces or special characters such as ">", "\", "%", or "$" may cause problems when the GLIMPSE software parses the text.

* Windows limits file paths to 256 characters. Because GLIMPSE and GCAM involve many nested folders, some users have experienced problems when installing GLIMPSE to a folder that has a long path. We recommend installing in a location such as C:\Projects\GLIMPSE or C:\Users\USERNAME\local_folder to avoid this problem.     

* As indicated in the Users' Guide, please do not install GLIMPSE to a location that is continuously backed up, such as OneDrive, as this may lead to model execution and synchronization issues.

* Additional XML files have been zipped into the Contrib.zip file due to space considerations.  If you would like to use these files, please unzip the Contrib.zip into the root directory of the project.

----
Starting GLIMPSE:

* To start GLIMPSE configured for GCAM-USA 7.0, double-click on "run_GLIMPSE_GCAM-USA-7.0.bat". The tutorials in the Users' Guide have been developed to work with this version.

* To start GLIMPSE configured for global GCAM 7.0, double-click on "run_GLIMPSE_GCAM-global-7.0.bat". This version has not been tested to the same degree, and the Scenario Component Library is includes only a single component.
