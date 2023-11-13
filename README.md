# Prototype for Laser Detection on Still Images
 
This repository was created for my Bachelor's thesis project. It uses image proceessing techniques to highlight surfaces where lasers bounce off of. It is currently hard coded to detect the light from a 520nm laser (green light). The user can manually set the source of the laser on the image as well as set a path between the detected points.

Functionality was prioritised over efficiency due to the nature of the thesis units, thus it was not been refactored or streamlined with coding best practices in mind.

# Installation

Import as a maven project with the following libraries:
- javacv-platform 1.5.7
- opencv-platform-gpu 4.5.5-1.5.7
- ffmpeg-platform-gpl 5.0-1.5.7

# Usage

Compile and run from your favourite IDE.
