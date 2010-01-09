//
//  PowerMetricalAppDelegate.h
//  PowerMetrical
//
//  Created by Daniel Lauzon on 08/01/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import <UIKit/UIKit.h>

@class PowerMetricalViewController;

@interface PowerMetricalAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    PowerMetricalViewController *viewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet PowerMetricalViewController *viewController;

@end

