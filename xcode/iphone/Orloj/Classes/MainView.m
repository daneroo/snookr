//
//  MainView.m
//  Orloj
//
//  Created by Daniel on 2008-11-05.
//  Copyright __MyCompanyName__ 2008. All rights reserved.
//

#import "MainView.h"

@implementation MainView

static int lightningEnabled = 0;

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
		// NOT BEING CALLED ???
    }
    return self;
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
	NSLog(@"was: %d",lightningEnabled);
	lightningEnabled = 30; !lightningEnabled;
	NSLog(@"setting: %d",lightningEnabled);
}

- (void) drawLightning {
	if (lightningEnabled<=0){
		//NSLog(@"shoud be NO: %d",lightningEnabled);
		return;
	} else {
		NSLog(@"shoud be YES: %d",lightningEnabled);
	}
	lightningEnabled--;
	
	if (!l1Image) {
		l1Image = [[UIImage imageNamed:@"lightning01.png"] retain];
	}
	if (!l2Image) {
		l2Image = [[UIImage imageNamed:@"lightning02.png"] retain];
	}

	NSTimeInterval secs = [NSDate timeIntervalSinceReferenceDate];
	//secs/=2;
	CGFloat t = remainder(secs,2.0);
	// angle stays constant for 1 second (round)
	// and is randomized by *17
	CGFloat angle = round(remainder(secs,60.0))*17*M_PI/30.0 - M_PI/2;
	CGFloat alpha = sin(fabs(t/1.0)*M_PI*3); // actually goes negative !! cool
	CGFloat scale = (6+fabs(sin(fabs(t/1.0)*M_PI)))/6.0; // actually goes negative !! cool
	//NSLog(@"drawLightning image: t=%f angle=%f scale:%f",t,angle,scale);
	UIImage *img = (t<0)?l1Image:l2Image;
	//[img drawAtPoint:imgPoint];
	CGContextRef context = UIGraphicsGetCurrentContext();
	CGContextSaveGState(context);
	CGContextTranslateCTM(context, self.center.x, self.center.y);
    CGContextRotateCTM(context, angle);
	CGContextScaleCTM(context,scale,scale);
	CGPoint imgPoint = CGPointMake(-img.size.width/2.0,-img.size.height/2.0);
	[img drawAtPoint:imgPoint blendMode:kCGBlendModeNormal alpha:alpha];
	CGContextRestoreGState(context);
}

- (void) drawMoonPhase {
	CGFloat radius = 40;
	CGPoint c = CGPointMake(self.center.x-85, self.center.y-100);
	
	//draw two circles in opposite directions
	CGContextRef context = UIGraphicsGetCurrentContext();

	if (NO) { // black backing
		CGContextSetRGBFillColor(context, 0,0,0,1);
		CGContextAddArc(context, c.x,c.y, radius+5, 0, 2*M_PI, 1);
		CGContextFillPath(context);
	}
	
	CGContextSetRGBFillColor(context, 1,1,1,1);
	CGContextSetRGBStrokeColor(context, 1,1,1,1);
	CGContextSetLineWidth(context, 2);
	
	CGContextSaveGState(context);
	NSTimeInterval secs = [NSDate timeIntervalSinceReferenceDate];
	CGFloat angle = remainder(secs,60.0)*M_PI/30.0 - M_PI/2;
	CGFloat w = radius*cos(angle);
	
	CGRect rect = CGRectMake(c.x-w, c.y-radius, 2*w, 2*radius);
	CGFloat w2 = radius*sin(angle);
	//CGRect rect2 = CGRectMake(c.x-w2, c.y-radius, 2*w2, 2*radius);
	CGRect full = CGRectMake(c.x-radius, c.y-radius, 2*radius, 2*radius);
	CGRect clip = CGRectMake(c.x, c.y-radius, ((w>0)?1:-1)*radius, 2*radius);

	NSString *text=[NSString stringWithFormat:@"%.1f",angle*180/M_PI];
	[text drawAtPoint:CGPointMake(c.x+3*radius, c.y) withFont:[UIFont systemFontOfSize:20]];
	
	CGContextSaveGState(context);
	CGContextClipToRect(context,clip);
	CGContextAddEllipseInRect(context, full);
	CGContextAddEllipseInRect(context, rect);
	//CGContextAddEllipseInRect(context, rect2);
	CGContextDrawPath(context, kCGPathEOFill);
	CGContextRestoreGState(context);
}

- (void) drawDial {
		//draw two circles in opposite directions
		CGFloat rOutter = self.bounds.size.width/2*.98;
		CGFloat rInner  = rOutter*.9;
		CGContextRef context = UIGraphicsGetCurrentContext();
		
	CGContextSetLineWidth(context, .5);
	CGContextSetRGBStrokeColor(context, .5,.5,.5,1);
	CGContextSetRGBFillColor(context, .75,.75,1,.1);
	CGContextAddArc(context, self.center.x, self.center.y, rOutter, 0, 2*M_PI, 1);
	CGContextStrokePath(context);
	CGContextAddArc(context, self.center.x, self.center.y, rInner, 0, 2*M_PI, 0);
	CGContextStrokePath(context);

	CGContextAddArc(context, self.center.x, self.center.y, rOutter, 0, 2*M_PI, 1);
	CGContextAddArc(context, self.center.x, self.center.y, rInner, 0, 2*M_PI, 0);
	CGContextFillPath(context);

	NSTimeInterval secs = [NSDate timeIntervalSinceReferenceDate];
	CGFloat angle;
	angle = remainder(secs,60.0)*M_PI/30.0 - M_PI/2;

	CGContextMoveToPoint(context, self.center.x, self.center.y);
	CGContextAddLineToPoint(context,self.center.x+rOutter*cos(angle), self.center.y+rOutter*sin(angle));
	CGContextSetLineWidth(context, .5);
	CGContextSetRGBStrokeColor(context, .7,.7,.7,1);
	CGContextStrokePath(context);

	angle = remainder(secs/60,60.0)*M_PI/30.0 - M_PI/2;
	CGContextMoveToPoint(context, self.center.x, self.center.y);
	CGContextAddLineToPoint(context,self.center.x+rInner*cos(angle), self.center.y+rInner*sin(angle));
	CGContextSetLineWidth(context, 2);
	CGContextSetRGBStrokeColor(context, .5,.5,.7,.8);
	CGContextStrokePath(context);

	angle = remainder(secs/3600,24.0)*M_PI/30.0 - M_PI/2;
	CGContextMoveToPoint(context, self.center.x, self.center.y);
	CGContextAddLineToPoint(context,self.center.x+rInner/2*cos(angle), self.center.y+rInner/2*sin(angle));
	CGContextSetLineWidth(context, 3);
	CGContextSetRGBStrokeColor(context, .5,.5,.9,.6);
	CGContextStrokePath(context);
	
	CGContextSetLineWidth(context, .5);
}

- (void)drawRect:(CGRect)rect {
    //NSLog(@"drawRect");

	[self drawLightning];

	[self drawDial];
	[self drawMoonPhase];
	
    // Text part
	[[UIColor lightTextColor] set];
	NSString *text=[NSString stringWithFormat:@"%@",[NSDate date]];
	CGFloat fontSize = 20;//[UIFont systemFontSize];
	UIFont *font = [UIFont systemFontOfSize:fontSize];
	CGSize tsz = [text sizeWithFont:font];
	CGPoint point = CGPointMake(self.center.x - tsz.width/2,10);
	[text drawAtPoint:point withFont:font];
	
}


- (void)dealloc {
    [super dealloc];
	[l1Image release];
	[l2Image release];
}


@end
