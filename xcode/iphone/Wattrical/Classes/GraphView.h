//
//  GraphView.h
//  CustomCellView
//
//  Created by Daniel Lauzon on 22/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RootViewController.h"
/*
 
 TODO refactor
   Layout constants: colors sizes (fonts,margin consts)
   Isolate FLow logic and extract axis analysis (pre-draw)
   Value mapping /1000 (keep state in NSInteger longer)
 
 The graphview's jobs is to render the data contained in it's
   NSMutableArray *observations property
 
 The view has a state:
    desiredScopInDays: wich indicates the desired Time Axis Scope (filter)
 and extra Data: goalVal, startVal    
 
 Data analysis:
   finding value range bounds (stamp,value) in {}..{}
   is affected by desiredScopInDays, and optionaly goalVal,startVal
 
   findRange produces calculated state:
     minVal..maxVal, and minTime,maxTime
 
   Note: minVal,maxVal are kept in original observation space, and as such should be 
    NSInteger not CGFloat ?
 
**Should have a pre-Drawing state calculation to prepare
   mapX,mapY
 
Analysis Flow and actual Drawing:
    -find Value Range (possibly filtered (w/desiredScopeInDays)
 
    layout Geometry
      find vertical available space based on Height of X-axis Label Font
        -> layoutBottomMargin
      determine Y axis Tics and Labels,
          maximum Label width yields horizontal available Space
        -> layoutLeftMargin
 
 
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
@private
    // Data
    NSMutableArray *observations; // observation data
    CGFloat startVal,goalVal; // extra Data
    // data range
    NSTimeInterval dataMinTime,dataMaxTime; // Time X-axis
    NSTimeInterval dataRangeTime;           // Time max-min
    CGFloat dataMinValue,dataMaxValue;      // Value Y-axis
    CGFloat dataRangeValue;                 // Value Max-Min
    // layout State
    CGFloat layoutBottomMargin; // leaves room at bottom for x axis labels & ticks
    CGFloat layoutTopMargin;    // leaves room at top just for breathing
    CGFloat layoutLeftMargin;   // leaves room at left for y axis labels & ticks
    CGFloat layoutRightMargin;  // leaves room at right just for breathing

    // controller hook property
    RootViewController *rootViewController;
    // UI State
	int desiredScopeInDays; // desired Visible Days on Display
	NSDate *animateUntil;
}

@property(nonatomic, retain) NSMutableArray *observations;
@property(nonatomic, assign) RootViewController *rootViewController;
@property(nonatomic, retain) NSDate *animateUntil;

- (void) randomize; //temporary

- (void) cycleTimeRange;


@end
