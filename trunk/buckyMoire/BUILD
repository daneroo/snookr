This is how to reproduce all the renders.

  svn checkout https://snookr.googlecode.com/svn/trunk/buckyMoire buckyMoire --username daniel.lauzon

Extract Binaries:
  Commited the sources and built-binaries, into svn.
  distr/
   +povray
   +dome
   +ffmpeg:  **the latest rpmforge release behaves differently for -b <bitrate>
        new: -b 9000000, old (which I use): -b 9000 (i.e. in kb/s vs b/s)
          so be careful if you switch, not sure where I got the sources.

  cd buckyMoire
  tar xjvf distr/dome_4_80-bin.tar.bz2
  tar xjvf distr/ffmpeg-bin.tar.bz2
  tar xjvf distr/povray-3.6-bin.tar.bz2

Rendering:
  All script are meant to be run with cwd  = buckyMoire (top dir)

first doMesh:
   for both aspect ratios, 1.778, 1.333 (16:9 and 4:3)
   creates {24 27 30 33 36 39 42 45 48}^2 combinations of 
   inner/outer dome frequency renders:
   every iteration createss a work directory and the resulting
   render is put into output/mesh-1.xxx

euler: doMesh - 122747.134s
./doMesh 
# mv output to renders, if you  wish to keep !
mkdir -p renders
mv output/mesh-1.778 renders
mv output/mesh-1.333 renders


now for the storyBoard...

euler:  79295.812s
cantor: 88329.171s
./doStory.sh
  mv output/story-1.778 renders
  mv output/story-1.333 renders

And finally the annotated final sequences

./doAnnotate.sh : depends on renders/mesh-XXX/, and renders/story-XXX/

  mkdir renders/annotate-final-1.778
  mv output/meshF*-1.778.mpg renders/annotate-final-1.778
  mv output/story-1.778.mpg renders/annotate-final-1.778

  mkdir renders/annotate-final-1.333
  mv output/meshF*-1.333.mpg renders/annotate-final-1.333
  mv output/story-1.333.mpg renders/annotate-final-1.333
