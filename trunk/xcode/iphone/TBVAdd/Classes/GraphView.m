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
        
        startVal = 190.0 * 1000;
        goalVal = 169.0 * 1000;
		daysAgo = 7;
    }
    return self;
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
	switch (daysAgo) {
		case 7:
			daysAgo=30;
			break;
		case 30:
			daysAgo=60;
			break;
		case 60:
			daysAgo=365;
			break;
		default:
			daysAgo=7;
	}
	NSLog(@"touch ended Event  daysAgo: %d",daysAgo);
	[self setNeedsDisplay];
}

-(void)reportRange {
    NSDate *minDate = [NSDate dateWithTimeIntervalSince1970:minTime];
    NSDate *maxDate = [NSDate dateWithTimeIntervalSince1970:maxTime];
    NSLog(@"range %@ - %@  val %.1f - %.1f",minDate,maxDate,minVal,maxVal);
}

-(void)findRange {
	NSDate *ago = [NSDate dateWithTimeIntervalSinceNow:(-daysAgo*24*3600)];

    NSEnumerator * enumerator = [observations objectEnumerator];
    Observation *observation;
    NSDate *minDate=nil, *maxDate=nil;
    CGFloat localMinVal=100000000.0, localMaxVal=0.0;
    while(observation = (Observation *)[enumerator nextObject]) {
        //NSLog(@"findRange obs retainCount: %d stamp: %d",[observation retainCount],[observation.stamp retainCount]); // OK retain==1 looks like it is autorelaesed

		if ([observation.stamp compare:ago]<0) continue;
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
    if (localMinVal>goalVal) localMinVal = goalVal;
    if (localMaxVal<goalVal) localMaxVal = goalVal;
    // include startValue in range:
    //if (localMinVal>startVal) localMinVal = startVal;
    //if (localMaxVal<startVal) localMaxVal = startVal;
    
	if ([minDate compare:ago]<0) {
		minDate = ago;
	}
	
    minTime = [minDate timeIntervalSince1970];
    maxTime = [maxDate timeIntervalSince1970];
    minVal = localMinVal;
    maxVal = localMaxVal;
    [self reportRange];
    
}

- (CGFloat) mapX:(NSTimeInterval) obsx {
	double xRange = maxTime - minTime;
	return 20+(self.bounds.size.width-40)*(obsx-minTime)/xRange;
}

- (CGFloat) mapY:(CGFloat) obsy {
	double yRange = maxVal - minVal;
	return (self.bounds.size.height-20)-(self.bounds.size.height-40)*(obsy-minVal)/yRange;
}

- (void)fillWithGradient:(CGContextRef)context {
	CGColorSpaceRef rgb = CGColorSpaceCreateDeviceRGB();
	CGFloat colors[] = {
		0,0,0,1,
		0,1.0/3.0,0,1,
		/*
		0,1.0/3.0,0,1,
		0,1.0/3.0,0,1,
		0,1.0/3.0,0,1,
		0,1.0/3.0,0,1,
		0,1.0/3.0,0,1,
		1,1,1,1,
		 */
	};
	CGGradientRef gradient;
	gradient = CGGradientCreateWithColorComponents(rgb, colors, NULL, sizeof(colors)/(sizeof(colors[0])*4));
	CGColorSpaceRelease(rgb);

	CGContextSaveGState(context);
	//CGContextClipToRect(context, clips[0]);
	
	CGRect b = self.bounds;
	CGPoint start = CGPointMake(b.origin.x, b.origin.y + b.size.height * 0.0);
	CGPoint end = CGPointMake(b.origin.x, b.origin.y + b.size.height * 0.75);
	CGContextDrawLinearGradient(context, gradient, start, end, 0);
	  //not needed here kCGGradientDrawsBeforeStartLocation | kCGGradientDrawsAfterEndLocation
	CGContextRestoreGState(context);
	
	CGGradientRelease(gradient);
}

- (void)drawRect:(CGRect)rect {
    //NSLog(@"drawRect with %d observations",[observations count]);
    [self findRange];
    
    //[delegate drawView:self inContext:UIGraphicsGetCurrentContext() bounds:self.bounds];
    CGContextRef context = UIGraphicsGetCurrentContext();
    //CGRect bounds = self.bounds;
    
	[self fillWithGradient:context];

	CGContextSetRGBStrokeColor(context, 1.0, 1.0, 1.0, 1.0);
	
	CGFloat xleft = [self mapX:minTime]; // maybe less and clip
	CGFloat xright = [self mapX:maxTime]; // maybe more and clip
	CGContextSetLineWidth(context, 1.0);

	// Axis yT + Ticks
	CGFloat yForXaxis = [self mapY:minVal]+3;

	CGContextMoveToPoint(context, xleft, yForXaxis);
	CGContextAddLineToPoint(context, xright, yForXaxis);
	for (int i=0;i<5;i++) {
		CGFloat xTick = xleft+(xright-xleft)*i/4.0;
		CGContextMoveToPoint(context, xTick, yForXaxis-0);
		CGContextAddLineToPoint(context, xTick, yForXaxis+3);
	}

	CGFloat xForYaxis = [self mapX:minTime]-5;
	CGFloat ybot = [self mapY:minVal]; // maybe less and clip
	CGFloat ytop = [self mapY:maxVal]; // maybe more and clip
	CGContextMoveToPoint(context, xForYaxis,ybot);
	CGContextAddLineToPoint(context, xForYaxis,ytop);
	for (int i=0;i<5;i++) {
		CGFloat yTick = ybot-(ybot-ytop)*i/4.0;
		CGContextMoveToPoint(context, xForYaxis-0, yTick);
		CGContextAddLineToPoint(context, xForYaxis-3, yTick);
	}
	// actually draw both axis+ticks
	//[[UIColor whiteColor] set];
	[[UIColor lightGrayColor] set];
	CGContextStrokePath(context);
	
    // Text part
	[[UIColor darkGrayColor] set];
	NSString *text=[NSString stringWithFormat:@"%d Days",daysAgo];
	CGFloat fontSize = [UIFont smallSystemFontSize]; //=12
	UIFont *font = [UIFont systemFontOfSize:fontSize];
	CGPoint point = CGPointMake(
								[self mapX:maxTime] - [text sizeWithFont:font].width,
								[self mapY:maxVal]-15);
	[text drawAtPoint:point withFont:font];

    // Tick Mark Text
    char *xtickTitle[]={"Sep","Oct","Mon","Tue","Wed"};
	[[UIColor lightGrayColor] set];
	for (int i=0;i<5;i++) {
		CGFloat yTick = ybot-(ybot-ytop)*i/4.0;
		NSString *ytickText=[NSString stringWithFormat:@"%c",i+'A'];
		CGSize ytsz=[ytickText sizeWithFont:font];
		CGPoint point = CGPointMake(xForYaxis-ytsz.width-3,yTick-ytsz.height/2.0);
		[ytickText drawAtPoint:point withFont:font];

		NSString *xtickText=[NSString stringWithFormat:@"%s",xtickTitle[i]];
		CGSize xtsz=[xtickText sizeWithFont:font];
		CGFloat xTick = xleft+(xright-xleft)*i/4.0;
		point = CGPointMake(xTick-xtsz.width/2.0, yForXaxis+3);
		[xtickText drawAtPoint:point withFont:font];

	}
	
	
	// Draw y=Konstant lines from left to right
	CGFloat ystart = [self mapY:startVal];
	CGFloat ygoal =  [self mapY:goalVal];
	
	CGFloat dash[] = {5.0, 5.0};
	CGContextSetLineDash(context, 0.0, dash, 2);
	CGContextSetRGBStrokeColor(context, .5,.5,.5,1);
	CGContextMoveToPoint(context, xleft, ystart);
	CGContextAddLineToPoint(context, xright, ystart);
	CGContextStrokePath(context);
	
	CGContextSetRGBStrokeColor(context, 0.0, 1.0, 0.0, 1.0);
	CGContextMoveToPoint(context, xleft, ygoal);
	CGContextAddLineToPoint(context, xright, ygoal);
	CGContextStrokePath(context);

	CGContextSetLineDash(context, 0.0, NULL, 0);
	CGContextSetRGBStrokeColor(context, 1.0, 1.0, 1.0, 1.0);
	
	CGContextSetLineWidth(context, 2.0);
	// Draw a connected sequence of line segments
	int numberofitems = [observations count];
	CGPoint pointarray[numberofitems];
	
	
	for (int i=0;i<numberofitems;i++) {
		Observation *observation = (Observation *)[observations objectAtIndex:i];
		NSTimeInterval obsx = [observation.stamp timeIntervalSince1970];
		pointarray[i] = CGPointMake([self mapX:obsx],[self mapY:observation.value]);
	}
	CGContextSaveGState(context);
	CGContextClipToRect(context, CGRectMake(20, 20, self.bounds.size.width-40, self.bounds.size.height-40));
	// Bulk call to add lines to the current path.
	// Equivalent to MoveToPoint(points[0]); for(i=1; i<count; ++i) AddLineToPoint(points[i]);
	CGContextAddLines(context, pointarray, sizeof(pointarray)/sizeof(pointarray[0]));
	CGContextStrokePath(context);
	CGContextRestoreGState(context);
}


- (void)dealloc {
    [super dealloc];
}


@end
