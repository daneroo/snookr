//
//  LogoPainter.m
//  Wattrical
//
//  Created by Daniel on 2008-12-02.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "LogoPainter.h"

@implementation LogoPainter

@synthesize animateUntil;

// period is in secs
// map to 0..1 -> 0..2PI offset by -PI/2
- (CGFloat)angleForSecs:(NSTimeInterval)secs period:(CGFloat)period {
	CGFloat angle = remainder(secs/period,1.0)*2*M_PI- M_PI/2;
	return angle;
}

// http://www.tecgraf.puc-rio.br/~mgattass/color/HSVtoRGB.htm
void ZZHSVtoRGB(CGFloat h, CGFloat s, CGFloat v, CGFloat* r, CGFloat* g, CGFloat* b) {
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

- (void)drawMovingDot:(CGRect)bounds angle:(CGFloat)angle radius:(CGFloat)radius hue:(CGFloat)hue {
	CGContextRef context = UIGraphicsGetCurrentContext();
	CGPoint center = CGPointMake(CGRectGetMidX(bounds), CGRectGetMidY(bounds));
	// greentored 0-.5
	CGFloat r=1,g=1,b=0;
	CGFloat sat=1,val=1;
	ZZHSVtoRGB(hue, sat, val, &r, &g, &b);

	CGSize          shadowOffset = CGSizeMake (0,0);
    float           shadowColorVals[] = {r, g, 0, 2};
    CGColorRef      shadowColor  = CGColorCreate (CGColorSpaceCreateDeviceRGB(), shadowColorVals);
    CGFloat blur = 4;
	
	CGFloat alpha=fabs(remainder(angle/M_PI*2,.6))+.4;
	CGContextSetRGBStrokeColor(context,r,g,0,alpha);
	CGContextSetShadowWithColor (context, shadowOffset, blur, shadowColor);//
	CGContextSetLineCap(context,kCGLineCapRound);
	CGContextAddArc(context, center.x, center.y, radius, angle-.1, angle+.1, 0);
	CGContextSetLineWidth(context, 5);
	CGContextStrokePath(context);
}


- (void)drawDial:(CGRect)bounds rInner:(CGFloat)rInner rOutter:(CGFloat)rOutter angleOffset:(CGFloat)aOffset{
	CGContextRef context = UIGraphicsGetCurrentContext();
	CGPoint center = CGPointMake(CGRectGetMidX(bounds), CGRectGetMidY(bounds));

	// Colors and widths
	CGContextSetLineWidth(context, .5);
	CGContextSetRGBStrokeColor(context, .5,.5,.5,1);
	CGContextSetRGBFillColor(context, .75,.75,1,.1);
	
	
	//draw and fill two circles in opposite directions
	CGContextAddArc(context, center.x, center.y, rOutter, 0, 2*M_PI, 1);
	CGContextStrokePath(context);
	CGContextAddArc(context, center.x, center.y, rInner, 0, 2*M_PI, 0);
	CGContextStrokePath(context);
	
	CGContextAddArc(context, center.x, center.y, rOutter, 0, 2*M_PI, 1);
	CGContextAddArc(context, center.x, center.y, rInner, 0, 2*M_PI, 0);
	CGContextFillPath(context);

	for (CGFloat angle=0; angle<M_PI*2; angle+=M_PI/8) {
		CGFloat a = angle+aOffset;
		CGFloat radius = rOutter;
		CGContextMoveToPoint(context,center.x+radius*cos(a), center.y+radius*sin(a));
		radius = (rOutter+rInner)/2;
		CGContextAddLineToPoint(context,center.x+radius*cos(a), center.y+radius*sin(a));
	}
	CGContextStrokePath(context);
	
}

- (void)drawBandsAndDots:(CGRect)bounds {
	// seconds
	NSTimeInterval secs = [animateUntil timeIntervalSinceNow]-3;
	if (secs<0) secs=0;
	
	CGFloat angle;

	CGFloat rOutter = bounds.size.height/2*.98;
	angle = [self angleForSecs:secs period:40]; //5s for 1/8th
	[self drawDial:bounds rInner:rOutter-10 rOutter:rOutter angleOffset:-angle];
	[self drawDial:bounds rInner:rOutter-25 rOutter:rOutter-15 angleOffset:angle];
	[self drawDial:bounds rInner:rOutter-40 rOutter:rOutter-30 angleOffset:-angle];
	angle = [self angleForSecs:secs period:10];
	[self drawMovingDot:bounds angle:angle radius:rOutter-5 hue:1.0/3.0];
	angle = [self angleForSecs:secs period:-7.5];
	[self drawMovingDot:bounds angle:angle radius:rOutter-20 hue:1.0/6.0];
	angle = [self angleForSecs:secs period:5];
	[self drawMovingDot:bounds angle:angle radius:rOutter-35 hue:0.0/3.0];
}

- (void) centerText:(NSString *)message withFont:(UIFont *)font atPoint:(CGPoint)center {
	CGSize sz=[message sizeWithFont:font];
	CGPoint point = CGPointMake(center.x-sz.width/2, center.y-sz.height/2);
    [message drawAtPoint:point withFont:font];	
}
- (void)drawText:(CGRect)bounds {
	CGPoint center = CGPointMake(CGRectGetMidX(bounds), CGRectGetMidY(bounds));

    CGFloat alpha = [animateUntil timeIntervalSinceNow];
    if (alpha>1) alpha=1.0;

    [[UIColor colorWithWhite:1 alpha:alpha] set];
	[self centerText:@"iMetrical" withFont:[UIFont systemFontOfSize:40] 
			 atPoint:CGPointMake(center.x,center.y+10)];
    [[UIColor colorWithWhite:.5 alpha:alpha] set];
	[self centerText:@"supported by" withFont:[UIFont italicSystemFontOfSize:15] 
			 atPoint:CGPointMake(center.x,center.y-25)];
}

- (void)paint:(CGRect)bounds {
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSaveGState(context);
	[self drawBandsAndDots:bounds];
    CGContextRestoreGState(context);

    CGContextSaveGState(context);
	[self drawText:bounds];
    CGContextRestoreGState(context);
}

- (void)dealloc {
    [animateUntil release];
    [super dealloc];
}


@end
