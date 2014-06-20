//
//  EZDetailViewController.h
//  Timer
//
//  Created by msavula on 4/15/14.
//  Copyright (c) 2014 Eleks. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface EZDetailViewController : UIViewController <UISplitViewControllerDelegate>

@property (strong, nonatomic) id detailItem;

@property (weak, nonatomic) IBOutlet UILabel *detailDescriptionLabel;
@end
