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

- (void)trackRequest:(NSURLRequest *)aRequest;

@property (nonatomic, retain) IBOutlet UIWebView *webView;

@end

