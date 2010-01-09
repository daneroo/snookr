//
//  TrackingWebCache.h
//  TransWeb
//
//  Created by Daniel Lauzon on 03/01/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PowerMetricalViewController.h"

@interface TrackingWebCache : NSURLCache {
	PowerMetricalViewController *viewController;
}

@property (nonatomic, retain) IBOutlet PowerMetricalViewController *viewController;

@end
