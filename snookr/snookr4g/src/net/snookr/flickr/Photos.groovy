
import groovy.util.slurpersupport .*;   // for parsing utils at end


class Photos {
    Flickr flickr = new Flickr();

    int getTotal() {
        def rsp = parse( flickr.getPhotoCounts() );

        // the date range (1900-2099) in getPhotoCounts should only return one range : one count
        assert 1 == rsp.photocounts.photocount.list().size()

        return Integer.valueOf(rsp.photocounts.photocount.'@count'.text());
    }

    /* getList Could have variable parameters later:
       perPage, sort, other search criteeria.
     */

    List getPhotoList(int numThreads) {
        int perPage = 500; // max 500
        int expectedTotal = getTotal();
        int expectedPages = (total+perPage-1)/perPage
        
        println "  Expecting total of ${expectedTotal} photos in ${expectedPages} pages of ${perPage} photos"
        
        List flickrIdList = null;
        if (numThreads>1) {
            flickrIdList = getListMultiThreaded  (perPage,expectedPages,expectedTotal,numThreads)
        } else {
            flickrIdList = getListSingleThreaded (perPage,expectedPages,expectedTotal)
        }
        
        println "Flickr List size: ${flickrIdList.size()}"
        
        assert expectedTotal == flickrIdList.size();
        assertUniqueness(flickrIdList);
        return flickrIdList;
        
    }
    List getListMultiThreaded(int perPage,int expectedPages,int expectedTotal,int numThreads) {
        // this Lists acess needs to be synchronized
        List flickrIdList = [];
        List pageList = (1..expectedPages);
        Closure getPhotoPageClosure = { page ->
            List pageIdList = new Photos().getPage(page,perPage,expectedPages,expectedTotal);
            // this Lists acess needs to be synchronized
            flickrIdList.addAll(pageIdList);
        }
        new Spawner(pageList,getPhotoPageClosure,numThreads).run();
        return flickrIdList;
    }

    List getListSingleThreaded(int perPage,int expectedPages,int expectedTotal) {
        Progress progress = new Progress(expectedPages,"page");
        List flickrIdList = [];
        for ( page in 1..expectedPages) { 
            List pageIdList = getPage(page,perPage,expectedPages,expectedTotal);
            flickrIdList.addAll(pageIdList);

            // show progress
            progress.increment();
            //println "  ${flickrIdList.size()}/${expectedTotal} page:${page}/${expectedPages} "
        }
        return flickrIdList;
    }


    List getPage(int page,int perPage,int expectedPages,int expectedTotal) {
        String sortOrder="date-taken-asc";
        Map searchParams = [
            "user_id":Environment.user_id,
            "per_page":"${perPage}",
            "page":"${page}",
            "sort":sortOrder,
            // extras: license, date_upload, date_taken, owner_name, icon_server, original_format, last_update, geo, tags, machine_tags
            "extras":"date_upload,date_taken,tags,machine_tags,last_update",
        ]
        def rsp = parse( flickr.getPhotoSearch(searchParams) );

        // assert invariants while iterating
        assert page == Integer.valueOf(rsp.photos.@page.text());
        assert perPage == Integer.valueOf(rsp.photos.@perpage.text());
        assert expectedPages == Integer.valueOf(rsp.photos.@pages.text());
        assert expectedTotal == Integer.valueOf(rsp.photos.@total.text());
        
        List list = rsp.photos.photo.list().'@id'*.text();
        assertUniqueness(list);
        return list;
    }
    
    void assertUniqueness(List listToCheck) {
        def uniqueMap = [:]
        listToCheck.each() { uniqueMap[it]=it }
        assert listToCheck.size() == uniqueMap.size();
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
        attr.lastupdate = rsp.photo.dates.'@lastupdate'.text();

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
