Tue Jul 14 11:19:52 EDT 2009
Composition of CC128 Simulation

Source Image: IMG_7903.JPG: 3456x2304
Source Overlay: LCDMask.xcf @ 942x1125
  Corresponds to the screen portion of IMG_7903.JPG
  contains layers LCDGradient,LCDMask,LCDDigits, and some Font Tests

Source Digit: Digit43px.xcf for 20x32 digit images
  saved as transparent png into digit43px/digit?.png 
  with blank,dash,colon,period
USE 43px font for 15px font simply by using img tags as:
  <img src="digit43px/digit3.png" width="7" height="11" alt="digit"/>
NOT: convert to 15px fonts with
  for i in *.png; do echo $i; convert -geometry 33% $i ../digit15px/$i; done
cc128-7903-bg-800.png:
  convert -geometry 800 IMG_7903.JPG cc128-7903-bg-800.png

cc128-lcd-218.png
  Save LCDMask.xcf: Gradient and Mask Layers, resized to 218x260
  convert -geometry 218 LCDMask.png cc128-lcd-218.png


Fonts:
  Install lcd fonts from dafont foundry:
    http://www.dafont.com/theme.php?cat=302
    digital_7.zip
    ds_digital.zip
  To install under ubuntu:
    cd /usr/share/fonts/truetype/
    mkdir ttf-dafont
    cd ttf-dafont/
    unzip -l ~daniel/CC128Sim/ds_digital.zip 
    unzip  ~daniel/CC128Sim/ds_digital.zip 
    unzip  ~daniel/CC128Sim/digital_7.zip 
    mv digital-7\ \(italic\).ttf digital-7-ITALIC.ttf 
    mv digital-7\ \(mono\).ttf digital-7-MONO.ttf 
    # rebuild font cache
    sudo fc-cache -f -v
