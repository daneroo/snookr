//
//  Mirawatt_ted5kAppDelegate.h
//  Mirawatt-ted5k
//
//  Created by Daniel Lauzon on 22/12/09.
//  Copyright __MyCompanyName__ 2009. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Mirawatt_ted5kViewController;

@interface Mirawatt_ted5kAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    Mirawatt_ted5kViewController *viewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet Mirawatt_ted5kViewController *viewController;

@end

