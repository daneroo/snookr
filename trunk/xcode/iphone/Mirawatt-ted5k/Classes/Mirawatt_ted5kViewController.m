//
//  Mirawatt_ted5kViewController.m
//  Mirawatt-ted5k
//
//  Created by Daniel Lauzon on 22/12/09.
//  Copyright __MyCompanyName__ 2009. All rights reserved.
//

#import "Mirawatt_ted5kViewController.h"

@implementation Mirawatt_ted5kViewController

@synthesize webView;

/*
// The designated initializer. Override to perform setup that is required before the view is loaded.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if (self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        // Custom initialization
    }
    return self;
}
*/

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
}
*/



// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
	//NSString *path = [[NSBundle mainBundle] pathForResource:@"iphone-cache" ofType:@"html"];
	NSString *path = [[NSBundle mainBundle] pathForResource:@"iphone-t5k" ofType:@"html" inDirectory:@"html"];
	//NSString *path = [[NSBundle mainBundle] pathForResource:@"iphone-ted" ofType:@"html"];
	NSFileHandle *readHandle = [NSFileHandle fileHandleForReadingAtPath:path];
	
	NSString *htmlString = [[NSString alloc] initWithData: 
							[readHandle readDataToEndOfFile] encoding:NSUTF8StringEncoding];
	
	NSString *imagePath = [[NSBundle mainBundle] resourcePath];
	imagePath = [imagePath stringByReplacingOccurrencesOfString:@"/" withString:@"//"];
	imagePath = [imagePath stringByReplacingOccurrencesOfString:@" " withString:@"%20"];
	
	NSLog(@"path: %@",path);
	NSLog(@"imagePath: %@",imagePath);
	
	// to make html content transparent to its parent view -
	// 1) set the webview's backgroundColor property to [UIColor clearColor]
	// 2) use the content in the html: <body style="background-color: transparent">
	// 3) opaque property set to NO
	//
	webView.opaque = NO;
	webView.backgroundColor = [UIColor clearColor];
	
	NSURL *baseURL = nil;
	baseURL = [NSURL URLWithString: [NSString stringWithFormat:@"file:/%@//html//",imagePath]];
	[self.webView loadHTMLString:htmlString baseURL:baseURL];
	
	//NSURL *fileURL = [NSURL URLWithString: [NSString stringWithFormat:@"file:/%@//en.lproj//webViewContent.html",imagePath]];
	//NSURLRequest *requestObj = [NSURLRequest requestWithURL:fileURL];
	//[webView loadRequest:requestObj];
	
	[htmlString release];	
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
    [webView release];
    [super dealloc];
}

@end
