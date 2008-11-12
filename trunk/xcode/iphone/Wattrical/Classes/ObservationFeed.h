//
//  ObservationFeed.h
//  Wattrical
//
//  Created by Daniel Lauzon on 12/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface ObservationFeed : NSObject {
    NSInteger count;
    NSMutableArray *stack;
}

- (void)parseXMLFileAtURL:(NSURL *)xmlURL;

@end
