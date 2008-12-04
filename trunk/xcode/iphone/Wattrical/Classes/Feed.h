//
//  Feed.h
//  Wattrical
//
//  Created by Daniel on 2008-12-04.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface Feed : NSObject {
	NSInteger scopeId;
	NSString *name;
	NSDate *stamp; // a time stamp representing the feed
	NSInteger value;
	//NSTimeInterval refreshRate; // in seconds
    NSMutableArray *observations; // array of Observation Objects
}
@property (nonatomic, assign) NSInteger scopeId;
@property (nonatomic, retain) NSString *name;
@property (nonatomic, retain) NSDate *stamp;
@property (nonatomic, assign) NSInteger value;
@property (nonatomic, retain) NSMutableArray *observations;

@end
