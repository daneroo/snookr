//
//  Observation.m
//  TBVAdd
//
//  Created by Daniel Lauzon on 17/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "Observation.h"


@implementation Observation

@synthesize stamp,value;

-(void)dealloc {
	[stamp release];
	[super dealloc];
}

@end
