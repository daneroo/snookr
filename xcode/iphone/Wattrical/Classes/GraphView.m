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
#import "RandUtil.h"
#import "LogoPainter.h"

@implementation GraphView
@synthesize observations;
@synthesize rootViewController;
@synthesize animateUntil;

#pragma mark State Management
- (void) cycleScopeInController {
    // find the controller
    [self.rootViewController cycleScope];
}

//This was for local scope management in Weightrical
- (void) cycleTimeRange {
	switch (desiredScopeInDays) {
		case 7:
			desiredScopeInDays=14;
			break;
		case 14:
			desiredScopeInDays=30;
			break;
		case 30:
			desiredScopeInDays=60;
			break;
		case 60:
			desiredScopeInDays=365;
			break;
		default:
			desiredScopeInDays=7;
	}
    //[self randomize]; watch out replaces observations, disconects from controller
	[self setNeedsDisplay];
}

- (void) randomize {
    desiredScopeInDays = [RandUtil randomWithMin:1 andMax:365];
    double d1 = [RandUtil logRandomWithMin:10000 andMax:300000];
    double d2 = [RandUtil logRandomWithMin:10000 andMax:300000];
    NSMutableArray *newObs = [[NSMutableArray alloc] init];
    NSInteger newMinVal = (NSInteger)((d1<d2)?d1:d2);
    NSInteger newMaxVal = (NSInteger)((d1>d2)?d1:d2);
    NSLog(@"randomize: %.1f %.1f",newMinVal/1000.0,newMaxVal/1000.0);
    for (int i=0;i<100;i++) {
        Observation *observation = [[Observation alloc] init]; 
        NSTimeInterval pastRandom = [RandUtil randomWithMin:-desiredScopeInDays*24*3600 andMax:0];
        observation.stamp = [NSDate dateWithTimeIntervalSinceNow:pastRandom];
        observation.value = [RandUtil randomWithMin:newMinVal andMax:newMaxVal];
        
        //NSLog(@"-loadObs retainCount: %d stamp: %d",[observation retainCount],[observation.stamp retainCount]);
        [newObs addObject:observation];
        [observation release];
    }
    // resort
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"stamp" ascending:NO];
	NSArray *sortDescriptors = [[NSArray alloc] initWithObjects:&sortDescriptor count:1];
	[newObs sortUsingDescriptors:sortDescriptors];
	[sortDescriptors release];
	[sortDescriptor release];
    
    self.observations = newObs;
    [newObs release];
}

#pragma mark UIView Override
- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
        // webSafe Dark Green: 006600 ~approx
        self.backgroundColor = [UIColor colorWithRed:0.0 green:1.0/3.0 blue:0.0 alpha:1.0];
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.contentMode = UIViewContentModeRedraw;
        
        
        layoutBottomMargin = 22; // leaves room at bottom for x axis labels & ticks
        layoutTopMargin = 5;     // leaves room at top just for breathing
        layoutLeftMargin = 40;   // leaves room at left for y axis labels & ticks
        layoutRightMargin = 10;  // leaves room at right just for breathing
        
        startVal = 2.08 * 1000; // 50 kWh
        goalVal = 1.25 * 1000; // 30 kWh
		desiredScopeInDays = 7;
    }
    return self;
}

#pragma mark Event handling

// only handle single tap for now
- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    [self cycleScopeInController];
    //[self cycleTimeRange];
	NSLog(@"touch ended Event  desiredScopeInDays: %d",desiredScopeInDays);
}

-(void)reportRange {
    NSDate *minDate = [NSDate dateWithTimeIntervalSince1970:dataMinTime];
    NSDate *maxDate = [NSDate dateWithTimeIntervalSince1970:dataMaxTime];
    NSLog(@"range %@ - %@  val %.1f - %.1f",minDate,maxDate,dataMinValue,dataMaxValue);
}

-(void)findRange {
	NSDate *ago = [NSDate dateWithTimeIntervalSinceNow:(-desiredScopeInDays*24*3600)];

    NSEnumerator * enumerator = [observations objectEnumerator];
    Observation *observation;
    NSDate *minDate=nil, *maxDate=nil;
    CGFloat localMinVal=100000000.0, localMaxVal=0.0;
    while(observation = (Observation *)[enumerator nextObject]) {

		bool filterDesiredScopeInDays = NO;
		if (filterDesiredScopeInDays && [observation.stamp compare:ago]<0) continue;

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
    BOOL includeYZero = YES;
    if (includeYZero) {
        if (localMinVal>0) localMinVal = 0;
        if (localMaxVal<0) localMaxVal = 0;
    }
    BOOL includeGoal = NO;
    if (includeGoal) {
        if (localMinVal>goalVal) localMinVal = goalVal;
        if (localMaxVal<goalVal) localMaxVal = goalVal;
    }
    BOOL includeStart = YES;
    if (includeStart) {
        if (localMinVal>startVal) localMinVal = startVal;
        if (localMaxVal<startVal) localMaxVal = startVal;
    }    
    
    // force scope in days even if no data present. or not up to NOW
    bool strictDesiredScopeInDays = NO;
	if (strictDesiredScopeInDays){
        if ([minDate compare:ago]>0) minDate = ago;
        if ([maxDate compare:[NSDate date]]<0) maxDate = [NSDate date];
	}
    
    // Set instace variables for data Ranges
    dataMinTime = [minDate timeIntervalSince1970];
    dataMaxTime = [maxDate timeIntervalSince1970];
    dataMinValue = localMinVal;
    dataMaxValue = localMaxVal;

    // protect against zero ranges:
    if (dataMaxTime==dataMinTime) {
        dataMinTime-=1;
        dataMaxTime+=1;
    }
    if (dataMaxValue==dataMinValue) {
        dataMinValue-=100;
        dataMaxValue+=100;
    }
    // ranges
    dataRangeTime = dataMaxTime-dataMinTime;    // Time Max-Min
    dataRangeValue = dataMaxValue-dataMinValue; // Value Max-Min
    
    [self reportRange];
    
}

- (CGFloat) mapX:(NSTimeInterval) obsx {
    CGFloat activeWidth = self.bounds.size.width - layoutLeftMargin - layoutRightMargin;
	return layoutLeftMargin+(activeWidth)*(obsx-dataMinTime)/dataRangeTime;
}

- (CGFloat) mapY:(CGFloat) obsy {
    CGFloat activeBottom = self.bounds.size.height-layoutBottomMargin; 
    CGFloat activeHeight = activeBottom-layoutTopMargin; 
	return (activeBottom)-(activeHeight)*(obsy-dataMinValue)/dataRangeValue;
}

- (void)fillWithGradient:(CGContextRef)context {
	CGColorSpaceRef rgb = CGColorSpaceCreateDeviceRGB();
	CGFloat colors[] = {
		//1,1,1,1,
		//0,1.0/3.0,0,1,
		//1,1,1,1,
        
		0,0,0,1,
		0,1.0/3.0,0,1,
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
    NSString *dateFormat = iMDateFormatNarrowWeekDay;

	CGFloat yForXaxis = [self mapY:dataMinValue]+3;
    CGFloat xleft = [self mapX:dataMinTime]; // maybe less and clip
	CGFloat xright = [self mapX:dataMaxTime]; // maybe more and clip

    CGFloat pixRange = xright - xleft;
    NSLog(@"xTicks range: %.1f days  pixRange:%.1f",dataRangeTime/(3600*24),pixRange);

    NSDate *minDate = [NSDate dateWithTimeIntervalSince1970:dataMinTime];
    NSDate *minDay = [self startOfDay:minDate after:YES offsetInDays:0];    
    NSDate *minMonth = [self startOfMonth:minDate after:YES offsetInMonths:0];
    //NSLog(@"min Day,Month: %@, %@",minDay,minMonth);
    NSDate *maxDate = [NSDate dateWithTimeIntervalSince1970:dataMaxTime];
    NSDate *maxDay = [self startOfDay:maxDate after:NO offsetInDays:0];    
    NSDate *maxMonth = [self startOfMonth:maxDate after:NO offsetInMonths:0];
    //NSLog(@"max Day,Month: %@, %@",maxDay,maxMonth);
    
    CGFloat approxDays = 1*((dataMaxTime-dataMinTime)/(24*3600));
    //NSLog(@"approx days: %f",approxDays);

    NSMutableDictionary *tickDates = [[NSMutableDictionary alloc] init];
    if (approxDays<=1) { // Hours
		dateFormat = iMDateFormatHM24;
		NSTimeInterval offset = dataRangeTime / 4;
		NSLog(@"offset = %f",offset);
		NSDate *xTickDate = [minDate addTimeInterval:offset/2];//[self startOfDay:minDate after:NO offsetInDays:0];    
		NSDate *endDate = maxDate;//[self startOfDay:xTickDate after:NO offsetInDays:2];    
        while ([xTickDate compare:endDate]<=0) {
            NSString *xtickText = [DateUtil formatDate:xTickDate withFormat:dateFormat];
            [tickDates setObject:xtickText forKey:xTickDate];
            xTickDate = [xTickDate addTimeInterval:offset];
        }
	} else if (approxDays<20) { // days
        if (approxDays<8) {
            dateFormat = iMDateFormatShortWeekDay;
        } else if (approxDays<15) {
            dateFormat = iMDateFormatNarrowWeekDay;
        } else {
            dateFormat = iMDateFormatDayOfMonth;
        }
        NSDate *xTickDate=minDay;
        while ([xTickDate compare:maxDay]<=0) {
            NSString *xtickText = [DateUtil formatDate:xTickDate withFormat:dateFormat];
            [tickDates setObject:xtickText forKey:xTickDate];
            xTickDate = [self startOfDay:xTickDate after:NO offsetInDays:1];
        }
    } else { // months
        NSDate *xTickDate=[self startOfMonth:minMonth after:NO offsetInMonths:-1];
        while ([xTickDate compare:maxMonth]<=0) {
            if (approxDays<90) {
                dateFormat = iMDateFormatShortMonth;
            } else {
                dateFormat = iMDateFormatNarrowMonth;
            }
            NSString *xtickText = [DateUtil formatDate:xTickDate withFormat:dateFormat];

            if ([xTickDate compare:minDate]>=0) [tickDates setObject:xtickText forKey:xTickDate];
            if (approxDays < 90 ) {
                NSDate *halfMonth = [self startOfDay:xTickDate after:NO offsetInDays:14];
                NSString *halfMonthText = [DateUtil formatDate:halfMonth withFormat:iMDateFormatDayOfMonth];
                if ([halfMonth compare:minDate]>=0 && [halfMonth compare:maxDate]<=0) {
                    [tickDates setObject:halfMonthText forKey:halfMonth];
                }
            }
            xTickDate = [self startOfMonth:xTickDate after:NO offsetInMonths:1];
        }
    }
    
    
    NSEnumerator *enumerator = [tickDates keyEnumerator];
    NSDate *xTickDate;
	CGContextMoveToPoint(context, xleft, yForXaxis);
	CGContextAddLineToPoint(context, xright, yForXaxis);
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
    
    [tickDates release];
}

- (void)yTicks:(CGContextRef)context withFont:(UIFont *)font {
    CGFloat xForYaxis = [self mapX:dataMinTime];
	CGFloat ybot = [self mapY:dataMinValue]; 
	CGFloat ytop = [self mapY:dataMaxValue]; 

    CGFloat pixRange = ybot - ytop;
    NSLog(@"yTicks range: %.1f - %.1f (%.1f) pixRange:%.1f",dataMinValue/1000,dataMaxValue/1000,dataRangeValue/1000,pixRange);
    
    // draw different scales: 100,50,10,5,1,.5,.1
    CGFloat scales[]={.1,.5,1,5,10,50,100};
    BOOL doneTicks=NO;
    BOOL doneText=NO;
    int maxTextCount = 4;
    int maxTickCount = 15;
	CGContextMoveToPoint(context, xForYaxis,ybot);
	CGContextAddLineToPoint(context, xForYaxis,ytop);
    for (int s=0; s<sizeof(scales)/sizeof(CGFloat); s++) {
        CGFloat scale = scales[s];
        CGFloat ceilMin = ceil((dataMinValue/1000.0)/scale)*scale;
        CGFloat floorMax = floor((dataMaxValue/1000.0)/scale)*scale;
        int numTicks =  1+floor((dataMaxValue/1000.0)/scale) - ceil((dataMinValue/1000.0)/scale);
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

- (void)drawOther:(CGRect)rect {
    NSDate *drawStart = [NSDate date];
    [self findRange];
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
	[self fillWithGradient:context];

	CGContextSetRGBStrokeColor(context, 1.0, 1.0, 1.0, 1.0);
	CGContextSetLineWidth(context, 1.0);
    
    CGFloat fontSize = [UIFont smallSystemFontSize]; //=12
	UIFont *font = [UIFont systemFontOfSize:fontSize];

	[[UIColor lightGrayColor] set];
	// X Axis + Ticks + TickText
    [self xTicks:context withFont:font];
	// Y Axis + Ticks + TickText
    [self yTicks:context withFont:font];
	
    // X Scope Label Text part
	[[UIColor lightGrayColor] set];
	NSString *text=[NSString stringWithFormat:@"%d Days",desiredScopeInDays];
    if (dataRangeTime<60.0) {
        text=[NSString stringWithFormat:@"%.0f seconds",dataRangeTime];
    } else if (dataRangeTime<3600.0) {
        text=[NSString stringWithFormat:@"%.0f minutes",round(dataRangeTime/60.0)];
    } else if (dataRangeTime<86400.0) {
        text=[NSString stringWithFormat:@"%.0f hours",round(dataRangeTime/3600.0)];
    } else {
        text=[NSString stringWithFormat:@"%.0f days",round(dataRangeTime/86400.0)];
    }
    
	CGPoint point = CGPointMake([self mapX:dataMaxTime] - [text sizeWithFont:font].width,
								[self mapY:dataMaxValue]);
	[text drawAtPoint:point withFont:font];
	
	// Draw y=Konstant lines from left to right
	CGContextSaveGState(context);
    BOOL drawDashes = YES;
    if (drawDashes) {
        CGContextSaveGState(context);

        CGFloat ystart = [self mapY:startVal];
        CGFloat ygoal =  [self mapY:goalVal];
        CGFloat xleft = [self mapX:dataMinTime]; // maybe less and clip
        CGFloat xright = [self mapX:dataMaxTime]; // maybe more and clip
        
        CGFloat dash[] = {5.0, 5.0};
        CGContextSetLineDash(context, 0.0, dash, 2);
        
        // Dash y==startVal  in gray50
        CGContextSetRGBStrokeColor(context, .5,.5,.5,1);
        CGContextMoveToPoint(context, xleft, ystart);
        CGContextAddLineToPoint(context, xright, ystart);
        CGContextStrokePath(context);
        
        // Dash y==goalVal  in green
        CGContextSetRGBStrokeColor(context, 0.0, 1.0, 0.0, 1.0);
        CGContextMoveToPoint(context, xleft, ygoal);
        CGContextAddLineToPoint(context, xright, ygoal);
        CGContextStrokePath(context);

        CGContextRestoreGState(context);
	}

    //BOOL drawData = NO;
    BOOL drawData = (dataRangeTime<=3600); // 15 minutes
    if (drawData) {
        CGContextSaveGState(context);
        // Draw a connected sequence of line segments
        int numberofitems = [observations count];
        CGPoint pointarray[numberofitems];
        for (int i=0;i<numberofitems;i++) {
            Observation *observation = (Observation *)[observations objectAtIndex:i];
            NSTimeInterval obsx = [observation.stamp timeIntervalSince1970];
            pointarray[i] = CGPointMake([self mapX:obsx],[self mapY:observation.value]);
        }
        
        //CGRect clip = CGRectMake(40, 20, self.bounds.size.width-60, self.bounds.size.height-40);
        CGRect clip = CGRectUnion(CGRectMake([self mapX:dataMinTime], [self mapY:dataMinValue], 0,0),
                                  CGRectMake([self mapX:dataMaxTime], [self mapY:dataMaxValue], 0,0));
        CGContextClipToRect(context, clip);
        CGContextSetRGBStrokeColor(context, 1.0, 1.0, 1.0, 1.0);
        CGContextSetLineWidth(context, 2.0);
        // Bulk call to add lines to the current path.
        // Equivalent to MoveToPoint(points[0]); for(i=1; i<count; ++i) AddLineToPoint(points[i]);
        CGContextAddLines(context, pointarray, sizeof(pointarray)/sizeof(pointarray[0]));
        CGContextStrokePath(context);
        
        CGContextRestoreGState(context);
    }    
    //BOOL drawDataBars = YES;
    BOOL drawDataBars = ! drawData;
    if (drawDataBars) {
        CGContextSaveGState(context);
        // Draw a connected sequence of line segments
        
        CGFloat activeWidth = self.bounds.size.width - layoutLeftMargin - layoutRightMargin;
        CGFloat widthOfBar4 = activeWidth/(4.0*[observations count]);

        int numberofitems = [observations count];
        CGPoint pointarray[numberofitems*2];
        CGPoint shadowpointarray[numberofitems*2];
        for (int i=0;i<numberofitems;i++) {
            Observation *observation = (Observation *)[observations objectAtIndex:i];
            NSTimeInterval obsx = [observation.stamp timeIntervalSince1970];
            
            CGFloat xActual = [self mapX:obsx];
            CGFloat yActual = [self mapY:observation.value];
            CGFloat yPrev = [self mapY:observation.value+(50+[RandUtil randomWithMin:0*-100 andMax:0*100])];
            BOOL fakeData = NO;
            if (fakeData) {
                CGFloat yFake = (.5+.5*sin(i*3*M_PI/numberofitems-(dataMaxTime*60/(numberofitems*2*M_PI))))*dataRangeValue+dataMinValue;
                yActual = [self mapY:(yFake)];
                srandom(obsx);
                yPrev = [self mapY:yFake+[RandUtil randomWithMin:-100 andMax:100]];
            }
            shadowpointarray[2*i+0] = CGPointMake(xActual-widthOfBar4,[self mapY:0]);
            shadowpointarray[2*i+1] = CGPointMake(xActual-widthOfBar4,yPrev);
            
            pointarray[2*i+0]       = CGPointMake(xActual,  [self mapY:0]);
            pointarray[2*i+1]       = CGPointMake(xActual,  yActual);
        }
        
        //CGRect clip = CGRectMake(40, 20, self.bounds.size.width-60, self.bounds.size.height-40);
        CGRect clip = CGRectUnion(CGRectMake([self mapX:dataMinTime], [self mapY:dataMinValue], 0,0),
                                  CGRectMake([self mapX:dataMaxTime], [self mapY:dataMaxValue], 0,0));
        CGContextClipToRect(context, clip);
        CGContextSetLineWidth(context, 2.0*widthOfBar4);
        // Bulk call to add lines to the current path.
        // Equivalent to MoveToPoint(points[0]); for(i=1; i<count; ++i) AddLineToPoint(points[i]);
        CGContextSetRGBStrokeColor(context,.7,.7,.7,1);
        CGContextStrokeLineSegments(context, shadowpointarray, sizeof(shadowpointarray)/sizeof(shadowpointarray[0]));
        CGContextSetRGBStrokeColor(context, 1.0, 1.0, 1.0, 1.0);
        CGContextStrokeLineSegments(context, pointarray, sizeof(pointarray)/sizeof(pointarray[0]));
        
        CGContextRestoreGState(context);
    }    
    
    BOOL shadowTest = drawData; //YES;
    if (shadowTest) {
        // shadow test
        CGContextSaveGState(context);
        
        
        //CGContextSetRGBFillColor (context, .5,.5,.5,1);
        //CGContextFillRect (context, CGRectMake (0,0,400,400));
        
        CGSize          myShadowOffset = CGSizeMake (0,0);
        float           myColorValues[] = {1, 1, 0, 2};
        CGColorSpaceRef myColorSpace = CGColorSpaceCreateDeviceRGB ();
        CGColorRef      myColor  = CGColorCreate (myColorSpace, myColorValues);//
        CGFloat blur = 20;
        
        //CGContextSetShadow(context, myShadowOffset, blur );
        CGContextSetShadowWithColor (context, myShadowOffset, blur, myColor);//
        
        CGContextSetLineWidth(context, 3);
        CGContextSetRGBStrokeColor(context, 0,1,0, 1.0);
        CGContextSetRGBFillColor (context, 1, 1, 0, 1);
        
        if ([observations count]>0) {
            Observation *observation =  (Observation *)[observations objectAtIndex:0];
            CGPoint c = CGPointMake([self mapX:[observation.stamp timeIntervalSince1970]],[self mapY:observation.value]);
            //CGPoint c = self.center;
            //c = self.center;
            CGRect dot = CGRectMake(c.x-5, c.y-5, 10, 10);
            //CGContextAddEllipseInRect(context, dot);
            //CGContextFillPath(context);
            CGContextAddEllipseInRect(context, dot);
            CGContextStrokePath(context);
        }
        
        CGContextRestoreGState(context);
    }        
    BOOL ctmTest = NO;
    if (ctmTest) {
        for (int i=0;i< 8;i++) {

            CGContextSaveGState(context);
            
            CGContextTranslateCTM(context,self.center.x,self.center.y);
            CGContextRotateCTM(context,(i/(8.0))*(M_PI*2));
            //CGContextScaleCTM(context,i/8.0 * 4,1);
            
            CGContextMoveToPoint(context, 0,0);
            CGContextAddLineToPoint(context, 1000,0);
            CGContextSetLineWidth(context, 2);
            CGContextSetRGBStrokeColor(context, 1.0, 0.0, 0.0, 1.0);
            CGContextStrokePath(context);

            CGContextTranslateCTM(context,-self.center.x,-self.center.y);
            
            
            CGContextRestoreGState(context);
        }
    }    
        
    NSTimeInterval duration = -[drawStart timeIntervalSinceNow];
    NSLog(@"Drawing time: %f",duration);
    
}

- (void)drawRect:(CGRect)rect {
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSaveGState(context);
	
	if (animateUntil) {
		[self fillWithGradient:context];
		LogoPainter *logoPainter = [[LogoPainter alloc] init];
		logoPainter.animateUntil = self.animateUntil;
		[logoPainter paint:self.bounds];
		[logoPainter release];
	} else {
		[self drawOther:rect];
	}
    CGContextRestoreGState(context);
}


- (void)dealloc {
    [observations release];
	[animateUntil release];
    [super dealloc];
}


@end
