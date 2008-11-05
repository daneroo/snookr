//
//  GraphView.h
//  CustomCellView
//
//  Created by Daniel Lauzon on 22/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

/*
 
 TODO refactor
   Layout constants: colors sizes (fonts,margin consts)
   Isolate FLow logic and extract axis analysis (pre-draw)
   Value mapping /1000 (keep state in NSInteger longer)
 
 The graphview's jobs is to render the data contained in it's
   NSMutableArray *observations property
 
 The view has a state:
    daysAgo: wich indicates the desired Time Axis Scope (filter)
 and extra Data: goalVal, startVal    
 
 Data analysis:
   finding value range bounds (stamp,value) in {}..{}
   is affected by daysAgo, and optionaly goalVal,startVal
 
   findRange produces calculated state:
     minVal..maxVal, and minTime,maxTime
 
   Note: minVal,maxVal are kept in original observation space, and as such should be 
    NSInteger not CGFloat ?
 
**Should have a pre-Drawing state calculation to prepare
   mapX,mapY
 
Flow and actual Drawing:
    findRange
    fillWithGradient
   -axis: range and resolution -> labels and ticks
    drawXAxis
    drawYAxis
   -dash for goal and start Vals
   -clip to value rect and draw all points (not on scoped ones)
 
   Lets Time the draw to see how much time this all takes.
      .01 seconds on simulator
      .09 on Phone, in debug. (slightly less .08 in non debug)
 */
@interface GraphView : UIView {
    NSMutableArray *observations;

    NSTimeInterval minTime,maxTime;
    CGFloat minVal,maxVal,startVal,goalVal;
	int daysAgo;
}

@property(nonatomic, retain) NSMutableArray *observations;

- (void) cycleTimeRange;

@end
