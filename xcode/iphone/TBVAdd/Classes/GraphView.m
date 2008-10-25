//
//  GraphView.m
//  CustomCellView
//
//  Created by Daniel Lauzon on 22/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "GraphView.h"
#import "Observation.h"

@implementation GraphView
@synthesize observations;

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
        // webSafe Dark Green: 006600 ~approx
        self.backgroundColor = [UIColor colorWithRed:0.0 green:1.0/3.0 blue:0.0 alpha:1.0];
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.contentMode = UIViewContentModeRedraw;
        
        startVal = 189.2 * 1000;
        goalVal = 169.0 * 1000;
    }
    return self;
}


-(void)reportRange {
    NSDate *minDate = [NSDate dateWithTimeIntervalSince1970:minTime];
    NSDate *maxDate = [NSDate dateWithTimeIntervalSince1970:maxTime];
    NSLog(@"range %@ - %@  val %.1f - %.1f",minDate,maxDate,minVal,maxVal);
}

-(void)findRange {
    NSEnumerator * enumerator = [observations objectEnumerator];
    Observation *observation;
    NSDate *minDate=nil, *maxDate=nil;
    CGFloat localMinVal=100000000.0, localMaxVal=0.0;
    while(observation = (Observation *)[enumerator nextObject]) {
        //NSLog(@"-- stamp: %@, value: %d", observation.stamp, observation.value);
        if (!minDate || [minDate compare:observation.stamp]>0) {
            minDate = observation.stamp;
        }
        if (!maxDate || [maxDate compare:observation.stamp]<0) {
            maxDate = observation.stamp;
        }
        if (localMinVal>observation.value) {
            localMinVal = observation.value;
        }
        if (localMaxVal<observation.value) {
            localMaxVal = observation.value;
        }
    }
    // include goal in range:
    if (localMinVal>goalVal) {
        localMinVal = goalVal;
    }
    if (localMaxVal<goalVal) {
        localMaxVal = goalVal;
    }
    // include startValue in range:
    if (localMinVal>startVal) {
        localMinVal = startVal;
    }
    if (localMaxVal<startVal) {
        localMaxVal = startVal;
    }
    
    minTime = [minDate timeIntervalSince1970];
    maxTime = [maxDate timeIntervalSince1970];
    minVal = localMinVal;
    maxVal = localMaxVal;
    [self reportRange];
    
}

- (void)drawRect:(CGRect)rect {
    NSLog(@"drawRect with %d observations",[observations count]);
    [self findRange];
    
    //[delegate drawView:self inContext:UIGraphicsGetCurrentContext() bounds:self.bounds];
    CGContextRef context = UIGraphicsGetCurrentContext();
    //CGRect bounds = self.bounds;
    
    // Drawing code
    // Drawing lines with a white stroke color
	CGContextSetRGBStrokeColor(context, 1.0, 1.0, 1.0, 1.0);
	// Draw them with a 2.0 stroke width so they are a bit more visible.
	
        double xRange = maxTime - minTime;
        double yRange = maxVal - minVal;

        // drawing goes from
        // origin         : 10.0,self.bounds.size.height-10 
        // to upper right : self.bounds.size.width-10, 10
        
        // Draw a single line from left to right
        CGFloat ygoal =  (self.bounds.size.height-10)-(self.bounds.size.height-20)*(goalVal-minVal)/yRange;
        CGFloat ystart =  (self.bounds.size.height-10)-(self.bounds.size.height-20)*(startVal-minVal)/yRange;

        CGContextSetLineWidth(context, 1.0);
        CGFloat dash[] = {5.0, 5.0};
        CGContextSetLineDash(context, 0.0, dash, 2);
        CGContextMoveToPoint(context, 10.0, ystart);
        CGContextAddLineToPoint(context, self.bounds.size.width-10, ystart);
        CGContextMoveToPoint(context, 10.0, ygoal);
        CGContextAddLineToPoint(context, self.bounds.size.width-10, ygoal);
        CGContextStrokePath(context);
        CGContextSetLineDash(context, 0.0, NULL, 0);
        
        
        CGContextSetLineWidth(context, 1.0);
        // Draw a connected sequence of line segments
        int numberofitems = [observations count];
        CGPoint pointarray[numberofitems];
        
        for (int i=0;i<numberofitems;i++) {
            Observation *observation = (Observation *)[observations objectAtIndex:i];
            NSTimeInterval obsx = [observation.stamp timeIntervalSince1970];
            CGFloat x = 10+(self.bounds.size.width-20)*(obsx-minTime)/xRange;

            CGFloat obsy = observation.value;
            CGFloat y = (self.bounds.size.height-10)-(self.bounds.size.height-20)*(obsy-minVal)/yRange;
            NSLog(@"p = %f,%f",x,y);
            
            pointarray[i] = CGPointMake(x,y);
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
