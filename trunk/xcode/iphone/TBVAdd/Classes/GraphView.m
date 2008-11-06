//
//  GraphView.m
//  CustomCellView
//
//  Created by Daniel Lauzon on 22/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "GraphView.h"
#import "Observation.h"
#import "DateUtil.h"

@implementation GraphView
@synthesize observations;

#pragma mark State Management
- (void) cycleTimeRange {
	switch (daysAgo) {
		case 7:
			daysAgo=14;
			break;
		case 14:
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
	[self setNeedsDisplay];
}


#pragma mark UIView Override
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

#pragma mark Event handling

// only handle single tap for now
- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    [self cycleTimeRange];
	NSLog(@"touch ended Event  daysAgo: %d",daysAgo);
}

-(void)reportRange {
    NSDate *minDate = [NSDate dateWithTimeIntervalSince1970:minTime];
    NSDate *maxDate = [NSDate dateWithTimeIntervalSince1970:maxTime];
    NSLog(@"range %@ - %@  val %.1f - %.1f",minDate,maxDate,minVal,maxVal);
}

double myRandom(double min,double max){
    static long rez = 100000;
    double r01 = ((double)(random()%rez))/rez;
    return r01*(max-min)+min;
}

double myLogRandom(double min,double max){
    double lr = myRandom(log(min),log(max));
    return exp(lr);
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
    
    BOOL shuffle = NO;
    if (shuffle) {
        double d1 = myLogRandom(10000, 900000);
        double d2 = myLogRandom(10000, 900000);
        minVal = (NSInteger)((d1<d2)?d1:d2);
        maxVal = (NSInteger)((d1>d2)?d1:d2);
    }    
    [self reportRange];
    
}

- (CGFloat) mapX:(NSTimeInterval) obsx {
	double xRange = maxTime - minTime;
	return 40+(self.bounds.size.width-60)*(obsx-minTime)/xRange;
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

- (NSDate *)startOfMonth:(NSDate  *)aDate after:(BOOL)flag offsetInMonths:(int)offset {
    NSCalendar *cal = [NSCalendar currentCalendar];
    NSDate *startOfMonth = nil;
    NSTimeInterval lengthOfMonth;
    [cal rangeOfUnit:NSMonthCalendarUnit startDate:&startOfMonth
                      interval:&lengthOfMonth forDate: aDate];
    /*
     BOOL ok = [cal rangeOfUnit:NSMonthCalendarUnit startDate:&startOfMonth
     interval:&lengthOfMonth forDate: aDate];
     if (ok) NSLog(@"length of month: %@ -> %f",startOfMonth,lengthOfMonth/(24*3600));
     
     */

    if (flag && [startOfMonth compare:aDate]<0) {
        //NSLog(@"rounding month up!");
        offset+=1;
    }
    if (offset!=0) {
        NSDateComponents *comps = [[NSDateComponents alloc] init];
        [comps setMonth:offset];
        startOfMonth = [cal dateByAddingComponents:comps toDate:startOfMonth  options:0];
        [comps release];
    }
    return startOfMonth;
}

// will roundup if after flag is set
- (NSDate *)startOfDay:(NSDate  *)aDate after:(BOOL)flag offsetInDays:(int)offset {
    NSCalendar *cal = [NSCalendar currentCalendar];
    NSDate *startOfDay = nil;
    [cal rangeOfUnit:NSDayCalendarUnit startDate:&startOfDay
            interval:NULL forDate: aDate];
    /*
     NSTimeInterval lengthOfDay;
     BOOL ok = [cal rangeOfUnit:NSDayCalendarUnit startDate:&startOfDay
                       interval:&lengthOfDay forDate: aDate];
     if (ok) NSLog(@"length of day: %@ -> %f",startOfDay,lengthOfDay/(3600.0));
     */
    
    if (flag && [startOfDay compare:aDate]<0) {
        //NSLog(@"rounding day up!");
        offset+=1;
    }
    if (offset!=0) {
        NSDateComponents *comps = [[NSDateComponents alloc] init];
        [comps setDay:offset];
        startOfDay = [cal dateByAddingComponents:comps toDate:startOfDay  options:0];
        [comps release];
    }
    return startOfDay;
}


- (void)xTicks:(CGContextRef)context withFont:(UIFont *)font {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    //[dateFormatter setDateFormat:@"d"];
    //[dateFormatter setDateFormat:@"MMM"];
    [dateFormatter setDateFormat:@"EEEEE"]; // EEEEE is one leter only S,M,T

	CGFloat yForXaxis = [self mapY:minVal]+3;
    CGFloat xleft = [self mapX:minTime]; // maybe less and clip
	CGFloat xright = [self mapX:maxTime]; // maybe more and clip

    double valRange = (maxTime - minTime);
    CGFloat pixRange = xright - xleft;
    NSLog(@"xTicks valRange: %.1f pixRange:%.1f",valRange,pixRange);

    NSDate *minDate = [NSDate dateWithTimeIntervalSince1970:minTime];
    NSDate *minDay = [self startOfDay:minDate after:YES offsetInDays:0];    
    NSDate *minMonth = [self startOfMonth:minDate after:YES offsetInMonths:0];
    //NSLog(@"min Day,Month: %@, %@",minDay,minMonth);
    NSDate *maxDate = [NSDate dateWithTimeIntervalSince1970:maxTime];
    NSDate *maxDay = [self startOfDay:maxDate after:NO offsetInDays:0];    
    NSDate *maxMonth = [self startOfMonth:maxDate after:NO offsetInMonths:0];
    //NSLog(@"max Day,Month: %@, %@",maxDay,maxMonth);
    
    CGFloat approxDays = 1*((maxTime-minTime)/(24*3600));
    //NSLog(@"approx days: %f",approxDays);

    NSMutableDictionary *tickDates = [[NSMutableDictionary alloc] init];
    if (approxDays<20) { // days
        if (approxDays<8) {
            [dateFormatter setDateFormat:@"EEE"]; // short weekday name
        } else if (approxDays<15) {
            [dateFormatter setDateFormat:@"EEEEE"]; // narrow weekday name
        } else {
            [dateFormatter setDateFormat:@"d"]; // day of month
        }
        NSDate *xTickDate=minDay;
        while ([xTickDate compare:maxDay]<=0) {
            NSString *xtickText= [dateFormatter stringFromDate: xTickDate];
            [tickDates setObject:xtickText forKey:xTickDate];
            xTickDate = [self startOfDay:xTickDate after:NO offsetInDays:1];
        }
    } else { // months
        NSDate *xTickDate=[self startOfMonth:minMonth after:NO offsetInMonths:-1];
        while ([xTickDate compare:maxMonth]<=0) {
            if (approxDays<90) {
                [dateFormatter setDateFormat:@"MMM"]; // short month name 
            } else {
                [dateFormatter setDateFormat:@"MMMMM"]; // narrow name 
            }
            NSString *xtickText= [dateFormatter stringFromDate: xTickDate];
            if ([xTickDate compare:minDate]>=0) [tickDates setObject:xtickText forKey:xTickDate];
            if (approxDays < 90 ) {
                NSDate *halfMonth = [self startOfDay:xTickDate after:NO offsetInDays:14];
                [dateFormatter setDateFormat:@"d"]; // narrow name 
                NSString *halfMonthText= [dateFormatter stringFromDate: halfMonth];
                if ([halfMonth compare:minDate]>=0 && [halfMonth compare:maxDate]<=0) {
                    [tickDates setObject:halfMonthText forKey:halfMonth];
                }
            }
            xTickDate = [self startOfMonth:xTickDate after:NO offsetInMonths:1];
        }
    }
    
    
    NSEnumerator *enumerator = [tickDates keyEnumerator];
    NSDate *xTickDate;
    while(xTickDate = (NSDate *)[enumerator nextObject]) {
        NSString *xtickText= [tickDates objectForKey:xTickDate];
        //NSLog(@"tick date: %@ -> %@",xTickDate,xtickText);
        CGFloat xTick = [self mapX:[xTickDate timeIntervalSince1970]];
        CGContextMoveToPoint(context, xTick, yForXaxis-0);
        CGContextAddLineToPoint(context, xTick, yForXaxis+3);
        
        CGSize xtsz=[xtickText sizeWithFont:font];
        CGPoint point = CGPointMake(xTick-xtsz.width/2.0, yForXaxis+3);
        [xtickText drawAtPoint:point withFont:font];
    }
    CGContextStrokePath(context);
    
    [dateFormatter release];
    [tickDates release];
}
- (void)drawXAxisIn:(CGContextRef)context withFont:(UIFont *)font {
    [self xTicks:context withFont:font];
	CGFloat yForXaxis = [self mapY:minVal]+3;
    CGFloat xleft = [self mapX:minTime]; // maybe less and clip
	CGFloat xright = [self mapX:maxTime]; // maybe more and clip
    
	// X Axis + Ticks
	CGContextMoveToPoint(context, xleft, yForXaxis);
	CGContextAddLineToPoint(context, xright, yForXaxis);
	for (int i=0;i<0;i++) {
		CGFloat xTick = xleft+(xright-xleft)*i/4.0;
		CGContextMoveToPoint(context, xTick, yForXaxis-0);
		CGContextAddLineToPoint(context, xTick, yForXaxis+3);
	}
	CGContextStrokePath(context);

    // Tick Mark Text
    char *xtickTitle[]={"Sep","Oct","Mon","Tue","Wed"};
	[[UIColor lightGrayColor] set];
	for (int i=0;i<0;i++) {
        
		NSString *xtickText=[NSString stringWithFormat:@"%s",xtickTitle[i]];
		CGSize xtsz=[xtickText sizeWithFont:font];
		CGFloat xTick = xleft+(xright-xleft)*i/4.0;
		CGPoint point = CGPointMake(xTick-xtsz.width/2.0, yForXaxis+3);
		[xtickText drawAtPoint:point withFont:font];
        
	}
    
}

- (void)yTicks:(CGContextRef)context withFont:(UIFont *)font {
    CGFloat xForYaxis = [self mapX:minTime];
	CGFloat ybot = [self mapY:minVal]; // maybe less and clip
	CGFloat ytop = [self mapY:maxVal]; // maybe more and clip

    CGFloat valRange = (maxVal - minVal)/1000.0;
    CGFloat pixRange = ybot - ytop;
    NSLog(@"yTicks valRange: %.1f pixRange:%.1f",valRange,pixRange);
    
	[[UIColor lightGrayColor] set];
    // draw different scales: 100,50,10,5,1,.5,.1
    //CGFloat scales[]={100,50,10,5,1,.5,.1};
    //CGFloat scales[]={100,50,20,10,5,2,1,.5,.2,.1};
    //CGFloat scales[]={.1,.2,.5,1,2,5,10,20,50,100};
    //CGFloat scales[]={.2,.5,1,2,5,10,20,50,100};
    CGFloat scales[]={.1,.5,1,5,10,50,100};
    BOOL doneTicks=NO;
    BOOL doneText=NO;
    int maxTextCount = 4;
    int maxTickCount = 15;
    for (int s=0; s<sizeof(scales)/sizeof(CGFloat); s++) {
        CGFloat scale = scales[s];
        CGFloat ceilMin = ceil((minVal/1000.0)/scale)*scale;
        CGFloat floorMax = floor((maxVal/1000.0)/scale)*scale;
        int numTicks =  1+floor((maxVal/1000.0)/scale) - ceil((minVal/1000.0)/scale);
        //NSLog(@" scale %d : %f numTicks:%d--(%f,%f)",s,scale,numTicks,ceilMin,floorMax);
        
        int countTicks = 0;
        for (CGFloat tick=ceilMin; tick<=floorMax; tick+=scale ) {
            countTicks++;
            //NSLog(@"tick #%2d : %f",countTicks,tick);
            CGFloat yVal = tick*1000;
            CGFloat yTick = [self mapY:yVal];

            if (!doneTicks && numTicks<=maxTickCount) {
                CGContextMoveToPoint(context, xForYaxis-0, yTick);
                CGContextAddLineToPoint(context, xForYaxis-3, yTick);
            }
            if (!doneText && numTicks<=maxTextCount) {
                CGContextMoveToPoint(context, xForYaxis-3, yTick);
                CGContextAddLineToPoint(context, xForYaxis+3, yTick);

                NSString *ytickText=[NSString stringWithFormat:@"%.1f",yVal/1000.0];
                CGSize ytsz=[ytickText sizeWithFont:font];
                //NSLog(@"ytick width:%f",ytsz.width);
                CGPoint point = CGPointMake(xForYaxis-ytsz.width-3,yTick-ytsz.height/2.0);
                [ytickText drawAtPoint:point withFont:font];
            }            
        }
        if (numTicks<=maxTextCount) doneText=YES;
        if (numTicks<=maxTickCount) doneTicks=NO;
    }
    CGContextStrokePath(context);
    
}

- (void)drawYAxisIn:(CGContextRef)context withFont:(UIFont *)font {
    [self yTicks:context withFont:font];
	// Y Axis + Ticks
    CGFloat xForYaxis = [self mapX:minTime];
	CGFloat ybot = [self mapY:minVal]; // maybe less and clip
	CGFloat ytop = [self mapY:maxVal]; // maybe more and clip
	CGContextMoveToPoint(context, xForYaxis,ybot);
	CGContextAddLineToPoint(context, xForYaxis,ytop);
	for (int i=0;i<0;i++) {
		//CGFloat yTick = ybot-(ybot-ytop)*i/4.0;
		CGFloat yVal = minVal+(maxVal-minVal)*(.1+.9*(i/3.0));
        CGFloat yTick = [self mapY:yVal];
		CGContextMoveToPoint(context, xForYaxis-0, yTick);
		CGContextAddLineToPoint(context, xForYaxis-3, yTick);
	}
	CGContextStrokePath(context);

    // Tick Mark Text
	[[UIColor lightGrayColor] set];
	for (int i=0;i<0;i++) {
		CGFloat yVal = minVal+(maxVal-minVal)*(.1+.9*(i/3.0));
        CGFloat yTick = [self mapY:yVal];
		NSString *ytickText=[NSString stringWithFormat:@"%.1f",yVal/1000.0];
		CGSize ytsz=[ytickText sizeWithFont:font];
        //NSLog(@"ytick width:%f",ytsz.width);
		CGPoint point = CGPointMake(xForYaxis-ytsz.width-3,yTick-ytsz.height/2.0);
		[ytickText drawAtPoint:point withFont:font];
	}
    
}

- (void)drawRect:(CGRect)rect {
    NSDate *drawStart = [NSDate date];
    
    //NSLog(@"drawRect with %d observations",[observations count]);
    [self findRange];
    
    //[delegate drawView:self inContext:UIGraphicsGetCurrentContext() bounds:self.bounds];
    CGContextRef context = UIGraphicsGetCurrentContext();
    //CGRect bounds = self.bounds;
    
	[self fillWithGradient:context];

	CGContextSetRGBStrokeColor(context, 1.0, 1.0, 1.0, 1.0);
	CGContextSetLineWidth(context, 1.0);
	[[UIColor lightGrayColor] set];
    CGFloat fontSize = [UIFont smallSystemFontSize]; //=12
	UIFont *font = [UIFont systemFontOfSize:fontSize];

	// X Axis + Ticks + TickText
    [self drawXAxisIn:context withFont:font];
	// Y Axis + Ticks + TickText
    [self drawYAxisIn:context withFont:font];
	
    // X Scope Label Text part
	[[UIColor lightGrayColor] set];
	NSString *text=[NSString stringWithFormat:@"%d Days",daysAgo];
	CGPoint point = CGPointMake(
								[self mapX:maxTime] - [text sizeWithFont:font].width,
								[self mapY:maxVal]-15);
	[text drawAtPoint:point withFont:font];
	
	// Draw y=Konstant lines from left to right
	CGFloat ystart = [self mapY:startVal];
	CGFloat ygoal =  [self mapY:goalVal];
    CGFloat xleft = [self mapX:minTime]; // maybe less and clip
	CGFloat xright = [self mapX:maxTime]; // maybe more and clip
	
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
        BOOL shuffle = NO;
        if (shuffle) {
            pointarray[i] = CGPointMake([self mapX:obsx],[self mapY:myRandom(minVal, maxVal)]);
        } else {
            pointarray[i] = CGPointMake([self mapX:obsx],[self mapY:observation.value]);
        }
	}
	CGContextSaveGState(context);
	CGContextClipToRect(context, CGRectMake(40, 20, self.bounds.size.width-60, self.bounds.size.height-40));
	// Bulk call to add lines to the current path.
	// Equivalent to MoveToPoint(points[0]); for(i=1; i<count; ++i) AddLineToPoint(points[i]);
	CGContextAddLines(context, pointarray, sizeof(pointarray)/sizeof(pointarray[0]));
	CGContextStrokePath(context);
	CGContextRestoreGState(context);
    
    NSTimeInterval duration = -[drawStart timeIntervalSinceNow];
    NSLog(@"Drawing time: %f",duration);
    
#define ITERATIONS 10000
    DateUtil *du = [[DateUtil alloc] init];
    //[du speedTest];
    drawStart = [NSDate date];
    NSString *fmt =  @"yyyy-MM-dd HH:mm:ss";
    for (int i=0;i<ITERATIONS;i++) {
        NSString *text= [du formatDate:drawStart withFormat:fmt];
        if (i%(ITERATIONS-1)==0) {
            NSLog(@" it %28d : %@",i,text);
        }
    }
    duration = -[drawStart timeIntervalSinceNow];
    NSLog(@"7-meth format: %12.1f i/s %5.3f s",(1.0*ITERATIONS)/duration,duration);

    drawStart = [NSDate date];
    for (int i=0;i<ITERATIONS;i++) {
        NSString *text= [DateUtil formatDate:drawStart withFormat:fmt];
        if (i%(ITERATIONS-1)==0) {
            NSLog(@" it %28d : %@",i,text);
        }
    }
    duration = -[drawStart timeIntervalSinceNow];
    NSLog(@"8+meth format: %12.1f i/s %5.3f s",(1.0*ITERATIONS)/duration,duration);
    
}


- (void)dealloc {
    [observations release];
    [super dealloc];
}


@end
