//
//  PowerMetricalViewController.h
//  PowerMetrical
//
//  Created by Daniel Lauzon on 08/01/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PowerMetricalViewController : UIViewController {
	UIWebView *webView;
	NSMutableArray *trackedRequests;
}

@property (nonatomic, retain) IBOutlet UIWebView *webView;

- (void)trackRequest:(NSURLRequest *)aRequest;
- (void)navigateAwwayIfConfirmed;
- (void) loadPageFromPath: (NSString *) pageRelativePath;
	

@end

