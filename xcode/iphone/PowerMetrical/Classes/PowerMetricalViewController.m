//
//  PowerMetricalViewController.m
//  PowerMetrical
//
//  Created by Daniel Lauzon on 08/01/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import "PowerMetricalViewController.h"
#import "TrackingWebCache.h"

@implementation PowerMetricalViewController

@synthesize webView;

// The designated initializer. Override to perform setup that is required before the view is loaded.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if (self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        // Custom initialization
		trackedRequests = [NSMutableArray arrayWithCapacity:2];
		
    }
    return self;
}

// make a copy of the request for later...
- (void)trackRequest:(NSURLRequest *)aRequest {
	
	// Not deep enough.
	//NSURLRequest *requestCopy = [request copy];

	/*
	 NSMutableURLRequest *requestCopy = [NSMutableURLRequest requestWithURL:[[request URL] copy]
	 cachePolicy:[request cachePolicy]
	 timeoutInterval:[request timeoutInterval]];
	 [requestCopy setHTTPMethod:[request HTTPMethod]];
	 [requestCopy setMainDocumentURL:[request mainDocumentURL]];
	 [requestCopy setAllHTTPHeaderFields:[NSMutableDictionary dictionaryWithDictionary:[request allHTTPHeaderFields]]];
	 [requestCopy setHTTPBody:[NSData dataWithData:[request HTTPBody]]];
	 [[requestCopy allHTTPHeaderFields] setValue:@"IGNORE" forKey:@"IGONRE"];
	 
	 //[ignoreSet addObject:requestCopy];
	 
	 NSURLConnection *theConnection=[[NSURLConnection alloc] initWithRequest:requestCopy delegate:self];
	 if (theConnection) {
	 // Create the NSMutableData that will hold
	 // the received data
	 // receivedData is declared as a method instance elsewhere
	 receivedData=[[NSMutableData data] retain];
	 } else {
	 // inform the user that the download could not be made
	 }
	 */
	
	
	if (YES){
		NSString *pageRelativePath = @"html/iphone-powermetrical.html";
		[self loadPageFromPath: pageRelativePath];
	}
}

- (void) loadPageFromURL: (NSString *) pageURL  {
	NSURL *igURL = [NSURL URLWithString:pageURL];
	NSURLRequest *requestObj = [NSURLRequest requestWithURL:igURL];
	[webView loadRequest:requestObj];
}

- (void) loadPageFromPath: (NSString *) pageRelativePath  {
	NSString *basePath = [[NSBundle mainBundle] resourcePath];
	//NSLog(@"basePath: %@",basePath);
	NSString *pageURL = [NSString stringWithFormat:@"file:/%@/%@",basePath,pageRelativePath];
	pageURL = [pageURL stringByReplacingOccurrencesOfString:@"/" withString:@"//"];
	pageURL = [pageURL stringByReplacingOccurrencesOfString:@" " withString:@"%20"];
	//NSLog(@"pageURL: %@",pageURL);
	
	NSURL *fileURL = [NSURL URLWithString:pageURL];
	NSURLRequest *requestObj = [NSURLRequest requestWithURL:fileURL];
	[webView loadRequest:requestObj];
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
	
	// set upthe new tracking url cache
	NSURLCache *defaultCache = [NSURLCache sharedURLCache];
	NSLog(@"old cache - mem: %d disk:%d",[defaultCache memoryCapacity],[defaultCache diskCapacity]);
	TrackingWebCache *cache = [[TrackingWebCache alloc] initWithMemoryCapacity: 512000 diskCapacity: 0 diskPath:@"./"];
	cache.viewController=self;
	[NSURLCache setSharedURLCache:cache];
	[cache release];
	
	NSString *pageURL = @"http://www.google.com/ig";
	[self loadPageFromURL: pageURL];
	//NSString *pageRelativePath = @"html/iphone-powermetrical.html";
	//[self loadPageFromPath: pageRelativePath];

}

- (void)webViewDidFinishLoad:(UIWebView *)aWebView
{
	NSLog(@"-Did finish loading");
	NSString *jsFindPMFrameScr=@"document.URL";
	NSString *ifr0 = [aWebView stringByEvaluatingJavaScriptFromString:jsFindPMFrameScr];
	NSLog(@"Here is the js-eval:  %@",ifr0);

	NSLog(@"Here is the status:  %@",[aWebView stringByEvaluatingJavaScriptFromString:@"document.getElementById('status').innerHTML"]);
	[aWebView stringByEvaluatingJavaScriptFromString:@"document.getElementById('inject').innerHTML='INJECTED'"];
	//[aWebView stringByEvaluatingJavaScriptFromString:@"setTimeout('alert(\'Surprise!\')', 5000)"];
	[aWebView stringByEvaluatingJavaScriptFromString:@"internalFunc('param value')"];
}	

/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


- (void)dealloc {
    [super dealloc];
}

@end
