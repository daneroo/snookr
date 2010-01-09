//
//  TrackingWebCache.m
//  TransWeb
//
//  Created by Daniel Lauzon on 03/01/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "TrackingWebCache.h"
#import "PowerMetricalViewController.h"

@implementation TrackingWebCache

@synthesize viewController;

//NSMutableSet *ignoreSet=NULL;
NSMutableData *receivedData = NULL;

- (NSCachedURLResponse*)cachedResponseForRequest:(NSURLRequest*)request
{
	NSLog(@"TRACKING: %@",[request URL]);
	/*
	if (ignoreSet==NULL) {
		NSLog(@"Allocating set");
		//ignoreSet = [NSMutableSet setWithCapacity:1];
		ignoreSet = [[NSMutableSet alloc] initWithCapacity:1];
	}
	 if ([ignoreSet containsObject:request]){
	 NSLog(@"ignoring: %@",[request URL]);
	 return [super cachedResponseForRequest:request];
	 }*/
	if ([[request allHTTPHeaderFields] objectForKey:@"IGONRE"]!=NULL){
		NSLog(@"ignoring: %@",[request URL]);
		return [super cachedResponseForRequest:request];
	}
	
    NSURL *url = [request URL];
	BOOL verbose=YES;
	if (verbose){
		if ([[url description] hasSuffix:@"makeRequest"]) {
			NSLog(@"TRACKING: %@",url);
			//NSLog(@"  type:    %d",navigationType);
			//NSLog(@"  M+url:   %@",[[request mainDocumentURL] description]);
			//NSLog(@"  URL:     %@",[[request URL] description]);
			//NSLog(@"  method:  %@",[request HTTPMethod]);
			//NSLog(@"  headers: %@",[[request allHTTPHeaderFields] description]);
			//NSLog(@"  cachePolicy:    %d",[request cachePolicy]);
			//NSLog(@"  body:    %@",[[request HTTPBody] description]);
			//NSLog(@"  body:    %@",[request HTTPBody]);
			//NSString* bodyStr = [[NSString alloc] initWithData:[request HTTPBody] encoding:NSASCIIStringEncoding];
			//NSLog(@"  body:    %@",bodyStr);
			//[bodyStr release];
			[viewController trackRequest:request];
		}
	}
	
    return [super cachedResponseForRequest:request];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response 
{
	// this method is called when the server has determined that it
    // has enough information to create the NSURLResponse
    // it can be called multiple times, for example in the case of a
    // redirect, so each time we reset the data.
    // receivedData is declared as a method instance elsewhere
    [receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // append the new data to the receivedData
    // receivedData is declared as a method instance elsewhere
    [receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // release the connection, and the data object
    [connection release];
    // receivedData is declared as a method instance elsewhere
    [receivedData release];
    // inform the user
    NSLog(@"Connection failed! Error - %@ %@",
          [error localizedDescription],
          [[error userInfo] objectForKey:NSErrorFailingURLStringKey]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // do something with the data
    // receivedData is declared as a method instance elsewhere
    NSLog(@"Succeeded! Received %d bytes of data",[receivedData length]);
	
	//NSUTF8StringEncoding or NSASCIIStringEncoding
	NSString* responseStr = [[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding];
	NSLog(@"  response:    %@",responseStr);
	//NSLog(@"  response:    %@...%@",[responseStr substringToIndex:10],[responseStr substringFromIndex:([responseStr length]-10)]);
	//NSString *ellipse = [responseStr substringToIndex:25];
	//NSLog(@"  response:   %@",[responseStr substringToIndex:45]);
	[responseStr release];
	
    // release the connection, and the data object
    [connection release];
    [receivedData release];

	// Gotta remove the originating request....
	//[ignoreSet removeObject:[connection orig request]

}


- (void)dealloc
{	
	/*if (ignoreSet!=NULL){
		[ignoreSet release];
	}*/
	//[IGNORE release];
	[super dealloc];
}

@end


