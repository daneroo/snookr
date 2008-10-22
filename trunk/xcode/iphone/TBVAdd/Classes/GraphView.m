//
//  GraphView.m
//  CustomCellView
//
//  Created by Daniel Lauzon on 22/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "GraphView.h"


@implementation GraphView


- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
        // webSafe Dark Green: 006600 ~approx
        self.backgroundColor = [UIColor colorWithRed:0.0 green:1.0/3.0 blue:0.0 alpha:1.0];
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.contentMode = UIViewContentModeRedraw;
    }
    return self;
}

- (void)drawRect:(CGRect)rect {
    //[delegate drawView:self inContext:UIGraphicsGetCurrentContext() bounds:self.bounds];
    CGContextRef context = UIGraphicsGetCurrentContext();
    //CGRect bounds = self.bounds;
    
    // Drawing code
    // Drawing lines with a white stroke color
	CGContextSetRGBStrokeColor(context, 1.0, 1.0, 1.0, 1.0);
	// Draw them with a 2.0 stroke width so they are a bit more visible.
	CGContextSetLineWidth(context, 1.0);
	
    CGFloat yAxis = self.bounds.size.height/2.0;
    CGFloat yRange = (self.bounds.size.height-10)/2.0; 
    
	// Draw a single line from left to right
	CGContextMoveToPoint(context, 10.0, yAxis);
	CGContextAddLineToPoint(context, self.bounds.size.width-10, yAxis);
	CGContextStrokePath(context);
	
	CGContextSetLineWidth(context, 2.0);
	// Draw a connected sequence of line segments
    int numberofitems = (self.bounds.size.width-20)/3;
    CGPoint pointarray[numberofitems];
    
    for (int i=0;i<numberofitems;i++) {
        CGFloat yRamp = ((i*1.0/numberofitems)-.5)*2; // -1..1
        CGFloat yRand = (random()%2000-1000)/1000.0; // -1..1
        CGFloat y = (i%2==0)?yRamp:yRand;
        pointarray[i] = CGPointMake(10+3*i,yAxis-y*yRange);
    }
	// Bulk call to add lines to the current path.
	// Equivalent to MoveToPoint(points[0]); for(i=1; i<count; ++i) AddLineToPoint(points[i]);
	CGContextAddLines(context, pointarray, sizeof(pointarray)/sizeof(pointarray[0]));
	CGContextStrokePath(context);
    
}


- (void)dealloc {
    [super dealloc];
}


@end
