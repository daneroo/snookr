package net.snookr.flickr;

import groovy.util.slurpersupport .*;   // for parsing utils at end
import java.text.SimpleDateFormat;

import net.snookr.util.MD5;
import net.snookr.util.Spawner;
import net.snookr.util.Progress;
import net.snookr.util.Environment;
import net.snookr.util.DateFormat;
import net.snookr.model.FlickrImage;

class Photos {
    static int PHOTO_ALREADY_PRESENT=-1;
    Flickr flickr = new Flickr();

    // this checks if the photo is not already present (by md5tag)
    // return -1 as photoid if already present.
    // could return a FlickrImage through getFlickrImage instead,
    // with null return value if already present.
    int uploadPhoto(File f) {
        String md5tag = "snookr:md5=${MD5.digest(f)}";
        boolean present = isMD5PresentOnFlickr(md5tag);
        if (present) {
            println "Photo ${f} with tag: ${md5tag} is already present on flickr";
            return PHOTO_ALREADY_PRESENT;
        }
        def rsp = parse( flickr.uploadPhoto(f,md5tag) );
        assert 1 == rsp.photoid.list().size()
        return Integer.valueOf(rsp.photoid.text());
    }

    // this is an example of counting images for seach params.
    // could be refactored. for later re-use
    // pass a list of tags ["tag1","tag2"], possible tag_mode etc.
    boolean isMD5PresentOnFlickr(String md5tag) {
        int perPage=500;
        int page=1;
        Map searchParams = [
            "user_id":Environment.user_id,
            "per_page":"${perPage}",
            "page":"${page}",
            "tags":"${md5tag}",
        ]
        def rsp = parse( flickr.getPhotoSearch(searchParams) );
        // assert invariants
        assert page == Integer.valueOf(rsp.photos.@page.text());
        assert perPage == Integer.valueOf(rsp.photos.@perpage.text());

        int countPhotosMatchingTag  = Integer.valueOf(rsp.photos.@total.text());
        if (countPhotosMatchingTag>1) {
            println "multiple (${countPhotosMatchingTag}) photos matching tag: ${md5tag} are already present on flickr";
        }
        //println "photos matching tag: ${md5tag}: ${countPhotosMatchingTag}"
        return (countPhotosMatchingTag);
        // if i wanted the actual list...
        // List returnedPhotosMatchingTag = rsp.photos.photo.list().'@id'*.text();
        // then parse as in getPage below, using extras, to fill in all info
    }

    int getTotal() {
        def rsp = parse( flickr.getPhotoCounts() );

        // the date range (1900-2099) in getPhotoCounts should only return one range : one count
        assert 1 == rsp.photocounts.photocount.list().size()

        return Integer.valueOf(rsp.photocounts.photocount.'@count'.text());
    }

    /* getPhotoList Could have variable parameters later:
       perPage, sort, other search criteeria.
     */

    List getPhotoList(int numThreads) {
        int perPage = 500; // max 500
        int expectedTotal = getTotal();
        int expectedPages = (total+perPage-1)/perPage
        
        println "  Expecting total of ${expectedTotal} photos in ${expectedPages} pages of ${perPage} photos"
        
        List flickrList = null;
        if (numThreads>1) {
            flickrList = getListMultiThreaded  (perPage,expectedPages,expectedTotal,numThreads)
        } else {
            flickrList = getListSingleThreaded (perPage,expectedPages,expectedTotal)
        }
        
        println "Flickr List size: ${flickrList.size()}"
        
        assert expectedTotal == flickrList.size();
        assertUniquenessOfPhotoid(flickrList);
        return flickrList;
        
    }
    List getListMultiThreaded(int perPage,int expectedPages,int expectedTotal,int numThreads) {
        // this Lists acess needs to be synchronized
        List flickrList = [];
        List pageList = (1..expectedPages);
        Closure getPhotoPageClosure = { page ->
            List pageFlickrList = new Photos().getPage(page,perPage,expectedPages,expectedTotal);
            // this Lists acess needs to be synchronized
            flickrList.addAll(pageFlickrList);
        }
        new Spawner(pageList,getPhotoPageClosure,numThreads).run();
        return flickrList;
    }

    List getListSingleThreaded(int perPage,int expectedPages,int expectedTotal) {
        Progress progress = new Progress(expectedPages,"page");
        List flickrList = [];
        for ( page in 1..expectedPages) { 
            List pageFlickrList = getPage(page,perPage,expectedPages,expectedTotal);
            flickrList.addAll(pageFlickrList);

            // show progress
            progress.increment();
        }
        return flickrList;
    }


    List getPage(int page,int perPage,int expectedPages,int expectedTotal) {
        String sortOrder="date-taken-asc";
        Map searchParams = [
            "user_id":Environment.user_id,
            "per_page":"${perPage}",
            "page":"${page}",
            "sort":sortOrder,
            // extras: license, date_upload, date_taken, owner_name, icon_server, original_format, last_update, geo, tags, machine_tags
            "extras":"date_upload,date_taken,tags,last_update",
        ]
        def rsp = parse( flickr.getPhotoSearch(searchParams) );

        // assert invariants while iterating
        assert page == Integer.valueOf(rsp.photos.@page.text());
        assert perPage == Integer.valueOf(rsp.photos.@perpage.text());
        assert expectedPages == Integer.valueOf(rsp.photos.@pages.text());
        assert expectedTotal == Integer.valueOf(rsp.photos.@total.text());
        
        //List list = rsp.photos.photo.list().'@id'*.text();
        List list = [];

        rsp.photos.photo.each() { photo -> // for each photo
            FlickrImage flima = new FlickrImage();
            flima.photoid = photo.'@id';

            //TODO taken granularity is always 0.??
            String takenStr = photo.'@datetaken';
            flima.taken = DateFormat.parse(takenStr);

            String postedStr = photo.'@dateupload';
            flima.posted = new Date(Long.parseLong(postedStr)*1000l);

            String lastUpdateStr = photo.'@lastupdate';
            flima.lastUpdate = new Date(Long.parseLong(lastUpdateStr)*1000l);

            String tags = photo.'@tags';
            //tags.tokenize().each() { println "t: ${it}" }
            // md5
            def md5List = tags.tokenize().findAll(){ it =~ /snookr:md5=/};
            assert md5List.size()<=1;
            if (md5List.size==1) {
                flima.md5 = (md5List[0] =~ /snookr:md5=/).replaceFirst("");
            }
            //println "${flima}";
            list << flima;
        }

        assertUniquenessOfPhotoid(list);
        return list;
    }
    
    void assertUniquenessOfPhotoid(List listToCheck) {
        def uniqueMap = [:]
        listToCheck.each() { uniqueMap[it.photoid]=it }
        assert listToCheck.size() == uniqueMap.size();
    }

    FlickrImage getFlickrImage(String photoid) {
        def attr = getInfo(photoid);
        FlickrImage flima = new FlickrImage();
        flima.photoid = attr.photoid;
        flima.md5 = attr.md5;
        //TODO verify taken granularity is always 0.
        flima.taken = DateFormat.parse(attr.taken);
        flima.posted = new Date(Long.parseLong(attr.posted)*1000l);
        flima.lastUpdate = new Date(Long.parseLong(attr.lastUpdate)*1000l);
        return flima;
    }

    Map getInfo(String photoid) {
        def attr = ["photoid":photoid];

        def rsp = parse( flickr.getPhotoInfo(["photo_id":photoid]) );
        assert photoid == rsp.photo.'@id'.text();

        // taken
        attr.taken = rsp.photo.dates.'@taken'.text();
        // posted
        attr.posted = rsp.photo.dates.'@posted'.text();
        // lastupdate
        attr.lastUpdate = rsp.photo.dates.'@lastupdate'.text();

        // md5
        def md5List = rsp.photo.tags.tag.findAll(){ it.text() =~ /snookr:md5=/};
        assert md5List.size()<=1;
        attr.md5 = (md5List[0].text() =~ /snookr:md5=/).replaceFirst("");
        
        return attr;
    }


    //utility function for parser
    GPathResult parseV(String stringResponse) { // V for Verbose
        println "------------------------------"
        println(stringResponse);
        println "------------------------------"
        return parse(stringResponse);
    }

    // might inject Error handling throwing Exception on Error
    // Might Also push the slurping back to Flickr or REST
    GPathResult parse(String stringResponse) {
        boolean validating=false;
        boolean namespaceAware = true;
        return new XmlSlurper(validating,namespaceAware).parseText(stringResponse);
    }

}
