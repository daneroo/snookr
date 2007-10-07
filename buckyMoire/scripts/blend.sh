#!/bin/sh

workDir=work;

function logStderr { #echo to stderr.
    echo $1 1>&2
}

function makeWorkDir { 
  mkdir -p $workDir
}

makeWorkDir

geometry="400";
geometry="720x480!";

currentFrame=0;
mkdir -p $workDir/final
function sequential {
    file=`printf "$workDir/final/final-%04d.png" $currentFrame`;
    echo $file;
    convert -geometry $geometry $1 $file;
    let currentFrame=currentFrame+1;
}

function blend {
    file=`printf "$workDir/final-%03d.png" $currentFrame`;
    echo $file;
    convert -geometry $geometry $1 $file;
    let currentFrame=currentFrame+1;
}

#renumber sequence 0-119 

echo convert A
mkdir -p $workDir/seqA
convert -geometry $geometry ../renders/justMoireGlassCam1.4/*.png   $workDir/seqA/seqA-%03d.png

echo convert B
mkdir -p $workDir/seqB
convert -geometry $geometry ../renders/justMoireC4F9081/*.png $workDir/seqB/seqB-%03d.png

echo convert C
mkdir -p $workDir/seqC
convert -geometry $geometry ../renders/justMoireC4F4527/*.png $workDir/seqC/seqC-%03d.png

mkdir -p $workDir/seqAB
mkdir -p $workDir/seqBC

#produce seqAB -blend not in ImageMagick 5.x
for i in `seq -f %03g 0 119`; do
    percent=`echo "scale=0;100*$i/120"|bc`;
    complement=`echo "scale=3;100-$percent"|bc`;
    echo AB $i $percent $complement
    composite -dissolve ${percent}x${complement} $workDir/seqB/seqB-$i.png $workDir/seqA/seqA-$i.png $workDir/seqAB/seqAB-$i.png
    echo BC $i $percent $complement
    composite -dissolve ${percent}x${complement} $workDir/seqC/seqC-$i.png $workDir/seqB/seqB-$i.png $workDir/seqBC/seqBC-$i.png
done


#for d in justMoireGlassCam0.9 justMoireGlassCam0.9 zoomOutGlass justMoireGlassCam1.4; do
for d in justMoireGlassCam0.9 justMoireGlassCam0.9 zoomOutGlass; do
    for i in ../renders/$d/*.png; do
        sequential $i
    done
done
for d in seqA seqAB seqB seqBC seqC; do
    for i in $workDir/$d/*.png; do
        sequential $i
    done
done

for d in zoomOutC4 fullerMoireC4 fullerMoireC4 fullerMoireC4; do
    for i in ../renders/$d/*.png; do
        sequential $i
    done
done


./ffmpeg/ffmpeg/ffmpeg -r 30 -i work/final/final-%04d.png -f dvd -b 9000 output.mpg
