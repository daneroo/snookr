//
//  Converter.m
//  CocoaOne
//
//  Created by Daniel Lauzon on 09/06/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "Converter.h"


@implementation Converter
@synthesize sourceCurrencyAmount, rate;
- (float)convertCurrency {
    
    return self.sourceCurrencyAmount * self.rate;
    
}


@end
