//
//  DateUtil.h
//  TBVAdd
//
//  Created by Daniel Lauzon on 06/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

/* 
   For NSDateFormatter class convinience method
     we use a thread local instance stored in the threadDictionary
     [[NSThread currentThread] threadDictionary];
   see
     +(DateUtil) threadInstance;
     +(DateUtil) getInstance;

   We adopted this setup for a) convienience, and from the following timing tests showing
   That the expensive part of setting up a dateformatter is actually
   the [[.. alloc] init] part!
     we compared tight iterations loops with: 
    NSDateFormatter allocation only, alloc-setFormat-stringFromDate, setFormat-stringFromDate,
    stringFromDate only, same combinations through instance variable and method invocation,
    and finally a class method using a thread local instance of DateUtil.
 
     1-alloc      :       4239.6 i/s 2.359 s
     2-format     :     116964.5 i/s 0.085 s
     3-use        :     117387.4 i/s 0.085 s
     4-meth  alloc:       4264.9 i/s 2.345 s
     5-meth format:     116922.1 i/s 0.086 s
     6+meth format:     114958.4 i/s 0.087 s
    or on the iPhone:
     0-alloc only :        180.0 i/s 5.555 s
     1-alloc      :        162.2 i/s 6.164 s
     2-format     :       1948.8 i/s 0.513 s
     3-use        :       1953.9 i/s 0.512 s
     4-meth  alloc:        162.0 i/s 6.172 s
     5-meth format:       1893.0 i/s 0.528 s
     6+meth format:       1885.3 i/s 0.530 s
 */
@interface DateUtil : NSObject {
    NSDateFormatter *sharedDateFormatter;
}

+ (DateUtil *)getInstance;
+ (NSString *) formatDate:(NSDate *)d withFormat:(NSString *)fmt;

- (NSString *) formatDate:(NSDate *)d withFormat:(NSString *)fmt;

@end
