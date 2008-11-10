//
//  OrlojAppDelegate.m
//  Orloj
//
//  Created by Daniel on 2008-11-05.
//  Copyright __MyCompanyName__ 2008. All rights reserved.
//

#import "OrlojAppDelegate.h"
#import "RootViewController.h"

@implementation OrlojAppDelegate


@synthesize window;
@synthesize rootViewController;


- (void)applicationDidFinishLaunching:(UIApplication *)application {
    
    [window addSubview:[rootViewController view]];
    [window makeKeyAndVisible];
}


- (void)dealloc {
    [rootViewController release];
    [window release];
    [super dealloc];
}

@end
