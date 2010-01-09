//
//  PowerMetricalAppDelegate.m
//  PowerMetrical
//
//  Created by Daniel Lauzon on 08/01/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import "PowerMetricalAppDelegate.h"
#import "PowerMetricalViewController.h"

@implementation PowerMetricalAppDelegate

@synthesize window;
@synthesize viewController;


- (void)applicationDidFinishLaunching:(UIApplication *)application {    
    
    // Override point for customization after app launch    
    [window addSubview:viewController.view];
    [window makeKeyAndVisible];
}


- (void)dealloc {
    [viewController release];
    [window release];
    [super dealloc];
}


@end
