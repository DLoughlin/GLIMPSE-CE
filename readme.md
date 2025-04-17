# EPA GLIMPSE Project

## Overview

GLIMPSE (GCAM Long-term Interactive Multi-Pollutant Scenario Evaluator; https://epa.gov/glimpse) is a graphical user interface for GCAM (Global Change Analysis Model; https://github.com/JGCRI/gcam-core), and open-source human-Earth systems model. GCAM is not itself an EPA product. GCAM development is organized by the Joint Global Change Research Institute (JGCRI; https://www.pnnl.gov/jgcri).  

## Requirements

We recommend installation on computers with 20 GB of RAM or more and with more than 100 GB of free hard disk space. GLIMPSE consists of two major components: the GLIMPSE-ScenarioBuilder and the GLIMPSE-ModelInterface. The GLIMPSE-ScenarioBuilder currently requires Windows 10 or Windows 11. The GLIMPSE-ModelInterface can be used independently and has been succesfully tested on Mac and Linux operating systems. 

## Getting started with GLIMPSE

Please see the User's Guide, which can be found in the Docs folder, for installation instructions. We also recommend the tutorials as a good starting place for learning to operate many of GLIMPSE's features. Training materials are currently being revised, but we expect that they will be available on https://epa.gov/glimpse at a later date. 

Several additional notes for consideration: 

* Please do not install GLIMPSE in a folder that includes spaces in its full path. 
* It is recommended that you modify your computer's power settings such that it will not go to sleep while GCAM is running.
* Please wait for the GLIMPSE-v*.*.zip file to fully download before unzipping.
* Some Windows computers automatically disable execution rights for downloaded EXE and BAT files. Double-clicking will bring up a warning window. Click on the "More Info" button, which will reveal a "Run Anyway" button. This will change the permissions and allow you to execute that file. Alternatively, on some computers, you may need to right-click on the file and choose to unblock it. 
* When naming folders, scenarios, and scenario components, please use alpha-numerical characters, as well as "_" or "-". Spaces or special characters such as ">", "\", "%", or "$" may cause problems when the GLIMPSE software parses the text.
* Windows limits file paths to 256 characters. Because GLIMPSE and GCAM involve many nested folders, some users have experienced problems when installing GLIMPSE to a folder that has a long path. We recommend installing in a location such as C:\Projects\GLIMPSE or C:\Users\USERNAME\local_folder to avoid this problem.     
* As indicated in the Users' Guide, please do not install GLIMPSE to a location that is continuously backed up, such as OneDrive, as this may lead to model execution and synchronization issues.

## For more information and to keep up with GLIMPSE developments

For more information about GLIMPSE, please see the EPA website https://epa.gov/glimpse. You can also receive periodic updates about GLIMPSE by subscribing to the glimpse-news listserv by emailing glimpse-news-subscribe@lists.epa.gov. You will receive a confirmation email to which a response is needed. 

# EPA Open Source Reference

## Brief Project Description

This repository contains files for teams to reuse when working in and with EPA Open Source projects.

Also, this repository contains the link to [EPA's System Lifecycle Management Policy and Procedure](https://www.epa.gov/irmpoli8/policy-procedures-and-guidance-system-life-cycle-management-slcm) which lays out EPA's Open Source Software Policy and [EPA's Open Source Code Guidance](https://www.epa.gov/developers/open-source-software-and-epa-code-repository-requirements). 

## For EPA Teams

For EPA Teams, we have guidance on how EPA puts our open source software policies into practice on GitHub. Read [EPA's GitHub Guidance.](https://www.epa.gov/webguide/github-guidance)

[EPA's Open Source Project repo](https://github.com/USEPA/open-source-projects) is for EPA teams to reuse file for properly maintaining their open source project. All projects must include a readme.md, license.md, contributing.md file and the disclaimer below.   

### Credits

This repository reused material from [GSA](https://www.gsa.gov/), [18F](https://18f.gsa.gov/) , [Lawrence Livermore National Lab](https://www.llnl.gov/), and from the [Consumer Financial Protection Bureau's policy](https://github.com/cfpb/source-code-policy).

### Disclaimer

The United States Environmental Protection Agency (EPA) GitHub project code is provided on an "as is" basis and the user assumes responsibility for its use.  EPA has relinquished control of the information and no longer has responsibility to protect the integrity , confidentiality, or availability of the information.  Any reference to specific commercial products, processes, or services by service mark, trademark, manufacturer, or otherwise, does not constitute or imply their endorsement, recommendation or favoring by EPA.  The EPA seal and logo shall not be used in any manner to imply endorsement of any commercial product or activity by EPA or the United States Government.
