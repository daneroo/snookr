//
//  Observation.h
//  TBVAdd
//
//  Created by Daniel Lauzon on 17/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//


@interface Observation : NSObject {
	NSDate *stamp;
	NSInteger value;
    
}

@property (nonatomic, retain) NSDate *stamp;
@property (nonatomic, assign) NSInteger value;

@end
