//
//  FeedParser.m
//  Wattrical
//
//  Created by Daniel on 2008-12-04.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "FeedParser.h"
#import "Feed.h"
#import "Observation.h"


@implementation FeedParser

+ (NSDictionary *)feedsByNameAtURL:(NSURL *)xmlURL {
	FeedParser *feedParser = [[FeedParser alloc] init];
    NSDictionary *feedDict = [feedParser feedsByNameAtURL:xmlURL];
	[feedParser release];
	return feedDict;
}

- (NSDictionary *)feedsByNameAtURL:(NSURL *)xmlURL {
	NSMutableArray *feedArray = [[self parseXMLFileAtURL:xmlURL] retain];
	
	NSMutableDictionary *feedDict = [[NSMutableDictionary alloc] init];
	for (id feed in feedArray) {
		//NSLog(@"feed: %@ - %d obs",((Feed *)feed).name,[((Feed *)feed).observations count]);
		[feedDict setValue:feed forKey:((Feed *)feed).name];
	}
    [feedArray release];
	
	return [feedDict autorelease];
}

- (NSMutableArray *)parseXMLFileAtURL:(NSURL *)xmlURL {    
	NSXMLParser *parser = [[NSXMLParser alloc] initWithContentsOfURL:xmlURL];
    
	// Set self as the delegate and features of NSXMLParser.
	[parser setDelegate:self];
	[parser setShouldProcessNamespaces:NO];
	[parser setShouldReportNamespacePrefixes:NO];
	[parser setShouldResolveExternalEntities:NO];
	
	feeds = [[NSMutableArray alloc] init];

	[parser parse];

    [parser release];     // release the xmlparser;
    
    // autorelease the returning array
	NSMutableArray *returnedFeeds = [feeds autorelease];
	feeds = nil;
    return returnedFeeds;
}

#pragma mark Utility funcs

- (void) printAttributes:(NSDictionary *)attributes {
	for (id key in attributes) {
        NSLog(@"  >>>>: %@ -> %@", key,[attributes objectForKey:key]);
    }
}

- (NSDate *)dateFromAttibuteNamed:(NSString *)name inDict:(NSDictionary *)dict {
	if (!dict || !name) return nil;
	NSString *attribute = [dict objectForKey:name];
	if (!attribute) return nil;
	
	NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
	NSString *iMDateFormatPlist = @"yyyy-MM-dd'T'HH:mm:ss'Z'"; // with a Z
	[formatter setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:0]];
	//NSLog(@"formatter time zone: %@",[formatter timeZone]);
	[formatter setDateFormat:iMDateFormatPlist];
	NSDate *theDate = [formatter dateFromString:attribute];
	[formatter release];
	return theDate;
}

- (NSInteger)intFromAttibuteNamed:(NSString *)name inDict:(NSDictionary *)dict defaultValue:(NSInteger)defaultValue {
	if (!dict || !name) return defaultValue;
	NSString *attribute = [dict objectForKey:name];
	if (!attribute) return defaultValue;
	NSScanner* scanner = [NSScanner scannerWithString:attribute];
	NSInteger value;
	if([scanner scanInteger:&value] == YES) {
		return value;
	}
	return defaultValue;
}

#pragma mark NSXMLParser delegate


// error state should be preserved so we can show error message.
- (void)parser:(NSXMLParser *)parser parseErrorOccurred:(NSError *)parseError {
    
	NSString * errorString = [NSString stringWithFormat:@"Unable to fetch feed (Error code %i )", [parseError code]];
	NSLog(@"error parsing XML: %@", errorString);
	
    // Set status instead...
    // TODO THIS is probably not the place for this....
	//UIAlertView * errorAlert = [[UIAlertView alloc] initWithTitle:@"Network Problem" message:errorString delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
	//[errorAlert show];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict {  
	//NSLog(@"- Element: %@", elementName);
	//[self printAttributes:attributeDict];
	if ([elementName isEqualToString:@"feed"]) {
		NSInteger scopeId = [self intFromAttibuteNamed:@"scopeId" inDict:attributeDict defaultValue:-1];
		NSString *name = [attributeDict objectForKey:@"name"];
		NSDate *stamp   = [self dateFromAttibuteNamed:@"stamp" inDict:attributeDict];
		NSInteger value = [self intFromAttibuteNamed:@"value" inDict:attributeDict defaultValue:-1];
		//NSLog(@"  feed(id:%d) %07s %@ %d",scopeId,[name cString],stamp,value);
		Feed *feed = [[Feed alloc] init];
		feed.scopeId = scopeId;
		feed.name = name;
		feed.stamp = stamp;
		feed.value = value;
        [feeds addObject:feed];
		[feed release];
	} else 	if ([elementName isEqualToString:@"observation"]) {
		NSDate *stamp   = [self dateFromAttibuteNamed:@"stamp" inDict:attributeDict];
		NSInteger value = [self intFromAttibuteNamed:@"value" inDict:attributeDict defaultValue:-1];
		//NSLog(@"    observation %@ %d",stamp,value);
		Observation *observation = [[Observation alloc] init]; 
        observation.stamp = stamp;
        observation.value = value;
		Feed *feed = [feeds lastObject]; 
        [feed.observations addObject:observation];
        [observation release];
	}

}

#pragma mark Constructor/Destructor
- (id) init {
    if ((self = [super init])) {
        //count = 0;
    }
    return self;
}

- (void)dealloc {
    // these should all be clean (nil)
    if (feeds) NSLog(@"FeedParser dealloc: feeds is not nil");
    [feeds release];
    [super dealloc];
}

@end
