import groovy.xml.*
import groovy.util.slurpersupport .*
import java.text.SimpleDateFormat;

import net.snookr.flickr.Flickr;
import net.snookr.flickr.Photos;
import net.snookr.db.Database;
import net.snookr.db.FlickrImageDAO;
import net.snookr.util.Spawner;
import net.snookr.util.Progress;
import net.snookr.util.MD5;
import net.snookr.model.FlickrImage;
/*
  What this script does: 
    Fetches photos...
getSizes returns data as:
source: is the url for the image itself
url: is a web page for that photo at that size

<sizes canblog="1" canprint="1" candownload="1">
        <size label="Square" width="75" height="75" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3_s.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=sq" />
        <size label="Thumbnail" width="100" height="75" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3_t.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=t" />
        <size label="Small" width="240" height="180" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3_m.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=s" />
        <size label="Medium" width="500" height="375" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=m" />
        <size label="Large" width="1024" height="768" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3_b.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=l" />
        <size label="Original" width="2592" height="1944" source="http://farm1.static.flickr.com/145/419443247_1195f586b4_o.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=o" />
</sizes>

*/

Flickr f = new Flickr();

/*
	<album title="Portraits" description="" imagePath="" thumbnail="http://static.flickr.com/2330/2241991842_20d52101b0_s.jpg">
		<img src="http://static.flickr.com/2395/2241984512_2b501f4d9e.jpg" link="http://www.flickr.com/photos/theturninggate/2241984512" target="_blank" thumbnail="http://static.flickr.com/2395/2241984512_2b501f4d9e_s.jpg" title="Hye Neung" description="Shots for Minty Bum." />
		<img src="http://static.flickr.com/2391/2241985390_036f95de4d.jpg" link="http://www.flickr.com/photos/theturninggate/2241985390" target="_blank" thumbnail="http://static.flickr.com/2391/2241985390_036f95de4d_s.jpg" title="Hye Neung" description="Shots for Minty Bum." />
                  ....
        </album>
*/
    public String getMonoLine(FlickrImage photo) {
       String photoid = photo.photoid;
        //println("Fetching sizes for " + photoid);
        Map mapOfSizeUrls =  new Photos().getSizes(photoid);
        listOfSizesToTry = ["Large","Medium","Small","Thumbnail","Square"];
        String srcUrl = null; // if no Large->Medium, no Medium-> Small,...
        listOfSizesToTry.each() { whichSize -> //
           // how do you break a closure iteration ?
           if (srcUrl==null) srcUrl = mapOfSizeUrls[whichSize];
        }


        String thumbUrl = mapOfSizeUrls["Square"];
        String linkUrl = "http://www.flickr.com/photos/sulbalcon/${photoid}"
        //println "${photoid} -> ${srcUrl} ";

        if (srcUrl==null) println "no image for monoline: ${mapOfSizeUrls}";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "<img src=\"${srcUrl}\" link=\"${linkUrl}\" target=\"_blank\" thumbnail=\"${thumbUrl}\" title=\"${sdf.format(photo.taken)}\" description=\"Description for ${photoid}\" />";

    }

// getting the list from flickr, could get from db instead.
int getPhotoListThreads=10;
def flickrList  = new Photos().getPhotoList(getPhotoListThreads);

// <!ENTITY prefs SYSTEM "prefs.xml">
// slice
//flickrList = flickrList[1..1000];

Map photoidToMonoLine = [:];
Closure mapPhotoSizesClosure = { photo -> //
    photoidToMonoLine[photo.photoid]= getMonoLine(photo);
    println "Got sizes for ${photo.photoid}";
}
int getPhotoSizesThreads=10;
new Spawner(flickrList,mapPhotoSizesClosure,getPhotoSizesThreads).run();

SimpleDateFormat mdf = new SimpleDateFormat("MMMM yyyy");
Calendar cal = new GregorianCalendar();
List years = new ArrayList(); years.addAll(2008..2002); years.add(1970);
years.each() { year-> //
    println "Processing Year: ${year}";
    FileWriter fw = new FileWriter("mono-${year}.xml");
    fw.write("""\
<?xml version="1.0"?>
<slideshow>
    <preferences>
    </preferences>
""");
    (12..1).each() { month -> //
      //println "Processing Month: ${month}";
        List monthList = flickrList.findAll() { photo -> //
            cal.setTime(photo.taken);
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH);
            //println "${photo.taken} -> ${y} ${m}";
            return (year==y && month==(m+1) );
        }
        int count = monthList.size();
        if (count>0) {
            String fmtMonth = mdf.format(monthList.get(0).taken)
            println "Count for Month: $year-${month}: ${fmtMonth} : ${count}";
            fw.write( "<album thumbnail=\"snookrThumb.jpg\" title=\"${fmtMonth}\" description=\"All photos for the month of ${fmtMonth}\">" +"\n");
            monthList.sort() { photoa, photob -> return photoa.taken.compareTo(photob.taken); }
            monthList.each() { photo -> //
                fw.write( "  "+ photoidToMonoLine[photo.photoid]+ "\n" );
            }
            fw.write( "</album>" +"\n");

        }
    }
    fw.write( "</slideshow>" +"\n");
    fw.close();
}

