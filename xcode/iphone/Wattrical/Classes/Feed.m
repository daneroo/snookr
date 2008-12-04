//
//  Feed.m
//  Wattrical
//
//  Created by Daniel on 2008-12-04.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "Feed.h"


@implementation Feed
@synthesize scopeId,name,stamp,value,observations;

#pragma mark Constructor/Destructor
- (id) init {
    if ((self = [super init])) {
        observations = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc {
	[name release];
	[stamp release];
    [observations release];
    [super dealloc];
}

@end
