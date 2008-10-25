//
//  GraphView.h
//  CustomCellView
//
//  Created by Daniel Lauzon on 22/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface GraphView : UIView {
    NSMutableArray *observations;

    NSTimeInterval minTime,maxTime;
    CGFloat minVal,maxVal,startVal,goalVal;
}

@property(nonatomic, assign) NSMutableArray *observations;

@end
