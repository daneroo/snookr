# 2012-09-26 Note for OSX
 Install Groovy for OSX: with homebrew
  $ brew install groovy
  $ export GROOVY_HOME=/usr/local/Cellar/groovy/2.0.4/libexec

Snookr v2 Plan

   Write Algorithm Parts as jdesktop Tasks
   Invoke Task from TUI - including TaskMonitor or uquivalent
   Mixin for Tasks

Tasks-Core
   Filesystem(root) -> FSImage[]
   FS2DB: include deletes.

   Flickr -> FlickrImage[]
   Flickr2DB: include deletes

   SnookrGAE (scalr|sulbalcon)
   Snookr2DB

   Synch
	Push

   Report

Refactor: Fillin Exif/MD5 (always/partial/never)->Modified FSIma[]
New
   Flatten Dirs
   Gorm/Hibernate/JPA replacement for Database
