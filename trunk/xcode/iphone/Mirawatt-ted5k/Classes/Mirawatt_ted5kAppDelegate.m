//
//  Mirawatt_ted5kAppDelegate.m
//  Mirawatt-ted5k
//
//  Created by Daniel Lauzon on 22/12/09.
//  Copyright __MyCompanyName__ 2009. All rights reserved.
//

#import "Mirawatt_ted5kAppDelegate.h"
#import "Mirawatt_ted5kViewController.h"

@implementation Mirawatt_ted5kAppDelegate

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
