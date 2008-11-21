//
//  StatusSectionHeaderView.h
//  Wattrical
//
//  Created by Daniel Lauzon on 19/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface StatusSectionHeaderView : UIView {
    CGFloat desiredSpeed;
    CGFloat currentSpeed;
    CGFloat zeroOneOffset;
    NSString *statusMessage;
    NSDate *statusMessageExpiry;
}

@property(nonatomic, retain) NSString *statusMessage;
@property(nonatomic, retain) NSDate *statusMessageExpiry;

- (void) setDesiredSpeed:(CGFloat)aSpeed;
- (void) setFadingStatus:(NSString *)statusMessage;

@end
