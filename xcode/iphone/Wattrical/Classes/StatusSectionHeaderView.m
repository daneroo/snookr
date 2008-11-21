//
//  StatusSectionHeaderView.m
//  Wattrical
//
//  Created by Daniel Lauzon on 19/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "StatusSectionHeaderView.h"


@implementation StatusSectionHeaderView

@synthesize statusMessage;
@synthesize statusMessageExpiry;

- (void) setFadingStatus:(NSString *)newMessage {
    self.statusMessage = newMessage;
    self.statusMessageExpiry = [NSDate dateWithTimeIntervalSinceNow:5.0];
}
- (void) setDesiredSpeed:(CGFloat)aSpeed {
    desiredSpeed = aSpeed;
    // desired speed set from 0--1
    if (desiredSpeed<0) {
        //NSLog(@"Speed out of bounds %f<0 : capping at 0",desiredSpeed);
        desiredSpeed=0;
    }
    if (desiredSpeed>1) {
        //NSLog(@"Speed out of bounds %f>1 : capping at 1",desiredSpeed);
        desiredSpeed=1;
    }
}
// callback for timer redraw
- (void) updateIfNeeded {
    //NSLog(@"updateroo");
    [self setNeedsDisplay];
}

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
        self.opaque = NO;
        self.backgroundColor = [UIColor clearColor];
        //self.backgroundColor = [UIColor darkGrayColor];

        desiredSpeed=0;
        [NSTimer scheduledTimerWithTimeInterval:0.05 target:self selector:@selector(updateIfNeeded) userInfo:nil repeats:YES];

    }
    return self;
}

// http://www.tecgraf.puc-rio.br/~mgattass/color/HSVtoRGB.htm
void HSVtoRGB(CGFloat h, CGFloat s, CGFloat v, CGFloat* r, CGFloat* g, CGFloat* b) {
    if ( s == 0 ) {
        *r = v;
        *g = v;
        *b = v;
    } else {
        CGFloat var_h = h * 6;
        CGFloat var_i = floor( var_h );
        CGFloat var_1 = v * ( 1 - s );
        CGFloat var_2 = v * ( 1 - s * ( var_h - var_i ) );
        CGFloat var_3 = v * ( 1 - s * ( 1 - ( var_h - var_i ) ) );
        if      ( var_i == 0 ) { *r = v     ; *g = var_3 ; *b = var_1; }
        else if ( var_i == 1 ) { *r = var_2 ; *g = v     ; *b = var_1; }
        else if ( var_i == 2 ) { *r = var_1 ; *g = v     ; *b = var_3; }
        else if ( var_i == 3 ) { *r = var_1 ; *g = var_2 ; *b = v;     }
        else if ( var_i == 4 ) { *r = var_3 ; *g = var_1 ; *b = v;     }
        else                   { *r = v     ; *g = var_1 ; *b = var_2; }
    }
}

- (void)drawFadingMessage:(CGRect)rect {
    if ([statusMessageExpiry timeIntervalSinceNow]<=0) return;
    CGFloat fontSize = 10.0;
    CGFloat alpha = [statusMessageExpiry timeIntervalSinceNow];
    if (alpha>1) alpha=1.0;
    [[UIColor colorWithWhite:.7 alpha:alpha] set];
    CGPoint point = CGPointMake(20,0);
    [statusMessage drawAtPoint:point withFont:[UIFont systemFontOfSize:fontSize]];
}
- (void)drawSpeedingDot:(CGRect)rect {
    NSTimeInterval secs = [NSDate timeIntervalSinceReferenceDate];
    // if speed has changed, calculate new offset so that there is no skip.
    
    // make zeroOne seamless on value change by calculating new offset
    CGFloat OLDzeroOne = 0;
    if (desiredSpeed!=currentSpeed) {
        CGFloat OLDperiodInSecs=1.0/currentSpeed;
        OLDzeroOne = fmod(fmod(secs,OLDperiodInSecs)/OLDperiodInSecs+zeroOneOffset,1);
        currentSpeed = desiredSpeed;
    }
    
    if (currentSpeed<=0) return;
    
    CGFloat periodInSecs=1.0/currentSpeed;
    CGFloat zeroOne;
    zeroOne = fmod(fmod(secs,periodInSecs)/periodInSecs+zeroOneOffset,1);
    if (OLDzeroOne) {
        // make zeroOne seamless on value change by calculating new offset
        //NSLog(@"-OLD,NEW %f -> %f",OLDzeroOne,zeroOne);
        zeroOneOffset += (OLDzeroOne-zeroOne) + 1; // 1 to preserve sign after mod
    }
    zeroOne = fmod(fmod(secs,periodInSecs)/periodInSecs+zeroOneOffset,1);
    //if (OLDzeroOne) NSLog(@"+OLD,NEW %f -> %f",OLDzeroOne,zeroOne);
    
    
    //NSLog(@"calculated Period:%f speed=%f",periodInSecs,desiredSpeed);

    // Drawing code
    CGContextRef context = UIGraphicsGetCurrentContext();

    //NSLog(@"z01: %f",zeroOne);
    CGFloat dotWidth=6;
    CGFloat xPos = zeroOne * self.bounds.size.width;
	//CGFloat w = radius*cos(angle);

    CGSize          myShadowOffset = CGSizeMake (0,0);
    float           myColorValues[] = {1, 1, 0, 2};
    CGColorSpaceRef myColorSpace = CGColorSpaceCreateDeviceRGB ();
    CGColorRef      myColor  = CGColorCreate (myColorSpace, myColorValues);//
    CGFloat blur = 4;

    CGFloat r=1,g=1,b=0;
    //CGFloat h=zeroOne/3,s=1,v=1;
    CGFloat h=(1-desiredSpeed)/3,s=1,v=1;
    
    HSVtoRGB(h, s, v, &r, &g, &b);
    CGContextSetRGBStrokeColor(context, r,g,b,1);
	CGContextSetLineWidth(context, 6.0);
    CGContextSetLineCap(context,kCGLineCapRound);
    CGContextSetShadowWithColor (context, myShadowOffset, blur, myColor);//

    CGContextSaveGState(context);
    CGFloat stretchFactor = 1.5; // >1 is invisible portion
    // center the visible part
    CGContextTranslateCTM(context, (1-stretchFactor)/2.0 * self.bounds.size.width,0);
    CGContextScaleCTM(context,stretchFactor,1);
    CGContextMoveToPoint(context, xPos, self.bounds.size.height/2);
    CGContextAddLineToPoint(context, xPos+dotWidth, self.bounds.size.height/2);
    CGContextStrokePath(context);
    CGContextRestoreGState(context);
    
}

- (void)drawRect:(CGRect)rect {
    //NSLog(@"-StatusView drawRect MainThread=%d",[NSThread isMainThread]);

    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSaveGState(context);
    [self drawSpeedingDot:rect];
    CGContextRestoreGState(context);
    CGContextSaveGState(context);
    [self drawFadingMessage:rect];
    CGContextSaveGState(context);

    //NSLog(@"+StatusView drawRect MainThread=%d",[NSThread isMainThread]);
}


- (void)dealloc {
    [super dealloc];
    [statusMessageExpiry release];
    [statusMessage release];
}


@end
