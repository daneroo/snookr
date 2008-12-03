//
//  LogoPainter.h
//  Wattrical
//
//  Created by Daniel on 2008-12-02.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


@interface LogoPainter : NSObject {
	NSDate *animateUntil;
}

@property(nonatomic, retain) NSDate *animateUntil;

- (void)paint:(CGRect)bounds;

@end
