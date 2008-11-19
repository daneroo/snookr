//
//  StatusSectionHeaderView.m
//  Wattrical
//
//  Created by Daniel Lauzon on 19/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "StatusSectionHeaderView.h"


@implementation StatusSectionHeaderView

- (void) updateIfNeeded {
    //NSLog(@"updateroo");
    [self setNeedsDisplay];
}

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
        self.opaque = NO;
        //self.backgroundColor = [UIColor yellowColor];
        self.backgroundColor = [UIColor clearColor];
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

- (void)drawRect:(CGRect)rect {
    // Drawing code
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    NSTimeInterval secs = [NSDate timeIntervalSinceReferenceDate];
    CGFloat periodInSecs=5;
	CGFloat zero0ne = fmod(secs,periodInSecs)/periodInSecs;
    NSLog(@"z01: %f",zero0ne);
    CGFloat fakeWidth = rect.size.width*1;
    CGFloat dotWidth=10;
    CGFloat xPos = zero0ne * fakeWidth;
	//CGFloat w = radius*cos(angle);

    CGSize          myShadowOffset = CGSizeMake (0,0);
    float           myColorValues[] = {1, 1, 0, 2};
    CGColorSpaceRef myColorSpace = CGColorSpaceCreateDeviceRGB ();
    CGColorRef      myColor  = CGColorCreate (myColorSpace, myColorValues);//
    CGFloat blur = 8;

    CGFloat r=1,g=1,b=0;
    CGFloat h=zero0ne/3,s=1,v=1;
    HSVtoRGB(h, s, v, &r, &g, &b);
    CGContextSetRGBStrokeColor(context, r,g,b,1);
	CGContextSetLineWidth(context, 6.0);
    CGContextSetLineCap(context,kCGLineCapRound);
    CGContextSetShadowWithColor (context, myShadowOffset, blur, myColor);//
    
    CGContextMoveToPoint(context, xPos, rect.size.height/2);
    CGContextAddLineToPoint(context, xPos+dotWidth, rect.size.height/2);
    CGContextStrokePath(context);
    
}


- (void)dealloc {
    [super dealloc];
}


@end
