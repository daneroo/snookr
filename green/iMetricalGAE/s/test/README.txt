Directory Structire on iMetricalGAE
iMetricalGAE
`-- s
    |-- css :: my css: imetrical-all.css imetrical.theme.css
    |   `-- img :: my theme images im-variant-color.png
    |-- jq  :: jQuery+ui distro (1.3.1 (ui 1.6rc6))
    |   |-- i18n
    |   `-- theme
    |       `-- images
    |-- js  :: my javascript: imetrical-1.0.js, jquery.timer.js
    `-- test :: my botstrap and testing stuff

--------------------
Minifier: yui does js and css
  java -jar /Users/daniel/jqsrc/trunk/jquery/build/yuicompressor-2.4.2.jar

java -jar /Users/daniel/jqsrc/trunk/jquery/build/yuicompressor-2.4.2.jar imetrical-1.0.js > imetrical-1.0.min.js

Inventory:

README.txt
  This file.

html:
  boot.html
  boot-fb.html
  googleviz-fb.html
  imetricaltbl.html
  ixhr.html
  jq.html
  yearly.html
  jqui.html:
    use jquery ui theming api

javascript:
  imetrical.js
  jquery.corner.js
  jquery.js
  jquery.timer.js
  jquery.ui.effects.js
  obsfeed.js

xml:opensocial: 
  boot-html.xml
  boot-ning.xml
  imetricaljq.xml
  imetricaltbl.xml


Themeroller fetching:
 glass:
  ui-bg_glass_75_e6e6e6_1x400.png
  http://jqueryui.com/themeroller/images/?new=e6e6e6&w=1&h=400&f=png&q=100&fltr[]=over|textures/02_glass.png|0|0|75
 highlight soft:
  ui-bg_highlight-soft_75_cccccc_1x100.png
  http://jqueryui.com/themeroller/images/?new=cccccc&w=1&h=100&f=png&q=100&fltr[]=over|textures/03_highlight_soft.png|0|0|75
 gloss-wave:
  ui-bg_gloss-wave_85_3b5998_500x100.png
  http://jqueryui.com/themeroller/images/?new=3b5598&w=500&h=100&f=png&q=100&fltr[]=over|textures/12_gloss_wave.png|0|0|85
