//
//  EZMasterViewController.m
//  Timer
//
//  Created by msavula on 4/15/14.
//  Copyright (c) 2014 Eleks. All rights reserved.
//

#import <PebbleKit/PebbleKit.h>
#import <CoreLocation/CoreLocation.h>
#import <AddressBookUI/AddressBookUI.h>

#import "EZMasterViewController.h"

#import "EZDetailViewController.h"

#import "EZTableViewCell.h"


enum {
    EZTimerStart = 0x5,
    EZTimerStop  = 0xb
};


#warning simple stab to hold data, replace it with database for persistancy
@interface EZTime : NSObject

@property (nonatomic, strong) NSDate *startDate;
@property (nonatomic, assign) NSTimeInterval timeSpent;
@property (nonatomic, strong) CLLocation *location;
@property (nonatomic, strong) NSString *locationAddress;

@end

@implementation EZTime

@end


@interface EZMasterViewController () <PBPebbleCentralDelegate, PBDataLoggingServiceDelegate, CLLocationManagerDelegate>

@property (nonatomic, strong) PBWatch *targetWatch;
@property (nonatomic, strong) CLGeocoder *geocoder;
@property (nonatomic, strong) CLLocationManager *locationManager;

@property (nonatomic, strong) NSDateFormatter *dateFormatter;

@property (nonatomic, strong) EZTime *currentTime;
@property (nonatomic, strong) NSMutableArray *times;

@property (nonatomic, strong) CLLocation *currentLocation;

// prototype to get rid of warning later
- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation;

@end

@implementation EZMasterViewController

@synthesize targetWatch = targetWatch_;

- (void)awakeFromNib
{
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad)
    {
        self.clearsSelectionOnViewWillAppear = NO;
        self.preferredContentSize = CGSizeMake(320.0, 600.0);
    }
    
    [super awakeFromNib];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    self.navigationItem.leftBarButtonItem = self.editButtonItem;

    UIBarButtonItem *addButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(insertNewObject:)];
    self.navigationItem.rightBarButtonItem = addButton;
    self.detailViewController = (EZDetailViewController *)[[self.splitViewController.viewControllers lastObject] topViewController];
    
    self.times = [NSMutableArray array];
    
    // We'd like to get called when Pebbles connect and disconnect, so become the delegate of PBPebbleCentral:
    [[PBPebbleCentral defaultCentral] setDelegate:self];
    [[[PBPebbleCentral defaultCentral] dataLoggingService] setDelegate:self];
    
    // Initialize with the last connected watch:
    [self setTargetWatch:[[PBPebbleCentral defaultCentral] lastConnectedWatch]];
    
    // location manager
    self.locationManager = [[CLLocationManager alloc] init];
    self.locationManager.distanceFilter = 10.0; // Move at least 10m until next location event is generated
    self.locationManager.desiredAccuracy = kCLLocationAccuracyThreeKilometers;
    self.locationManager.delegate = self;
    [self.locationManager startUpdatingLocation];
    
    self.dateFormatter = [[NSDateFormatter alloc] init];
    self.dateFormatter.dateFormat = @"HH:MM:ss";
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table View

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.times.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    EZTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    
#warning better would be to display dates as headers?
    EZTime *time = self.times[indexPath.row];
    cell.titleLabel.text = [self.dateFormatter stringFromDate:time.startDate];
    
    NSUInteger seconds = (NSUInteger)round(time.timeSpent);
    cell.valueLabel.text = [NSString stringWithFormat:@"Duration: %02lu:%02lu:%02lu", seconds / 3600, (seconds / 60) % 60, seconds % 60];
    cell.subtitleLabel.text = time.locationAddress; //[NSString stringWithFormat:@"%02lu:%02lu:%02lu", seconds / 3600, (seconds / 60) % 60, seconds % 60];
    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [self.times removeObjectAtIndex:indexPath.row];
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view.
    }
}

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        NSDate *object = [(EZTime *)self.times[indexPath.row] startDate];
        self.detailViewController.detailItem = object;
    }
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([[segue identifier] isEqualToString:@"showDetail"])
    {
        NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
        NSDate *object = [(EZTime *)self.times[indexPath.row] startDate];
        
        [[segue destinationViewController] setDetailItem:object];
    }
}

- (void)insertNewObject:(id)sender
{
    [self performSegueWithIdentifier:@"EZShowSpritzSegue" sender:self];
}

#pragma mark - pebble utility

- (void)setTargetWatch:(PBWatch*)watch
{
    targetWatch_ = watch;
    
    // NOTE:
    // For demonstration purposes, we start communicating with the watch immediately upon connection,
    // because we are calling -appMessagesGetIsSupported: here, which implicitely opens the communication session.
    // Real world apps should communicate only if the user is actively using the app, because there
    // is one communication session that is shared between all 3rd party iOS apps.
    
    // Test if the Pebble's firmware supports AppMessages / Weather:
    [watch appMessagesGetIsSupported:^(PBWatch *watch, BOOL isAppMessagesSupported)
     {
        if (isAppMessagesSupported)
        {
            // Configure our communications channel to target the stopwatch app:
            // See pebble-stopwatch-master/appinfo.json in the native watch app SDK for the same definition on the watch's end:
            // "uuid": "11a1cb08-065f-491e-82c4-83b9f9cb042c",
            uint8_t bytes[] = {0x11, 0xa1, 0xcb, 0x08, 0x06, 0x5f, 0x49, 0x1e, 0x82, 0xc4, 0x83, 0xb9, 0xf9, 0xcb, 0x04, 0x2c};
            NSData *uuid = [NSData dataWithBytes:bytes length:sizeof(bytes)];
            [[PBPebbleCentral defaultCentral] setAppUUID:uuid];
            
            [targetWatch_ appMessagesAddReceiveUpdateHandler:^BOOL(PBWatch *watch, NSDictionary *update) {
                NSLog(@"Received message: %@", update);
                
                int tag = [[update.allKeys objectAtIndex:0] intValue];
                
                if (tag == EZTimerStart)
                {
                    // store start time
                    self.currentTime = [[EZTime alloc] init];
                    self.currentTime.location = self.locationManager.location;
                    
                    if (self.currentTime.location == nil && self.currentLocation != nil)
                    {
                        self.currentTime.location = self.currentLocation;
                    }
                    
                    [self.times addObject:self.currentTime];
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.tableView reloadData];
                    });
                    
                    // started timer, save timestamp and get location
                    // let's get going then, grab current location and send it over to our awesome watch
                    if (self.geocoder == nil)
                    {
                        self.geocoder = [[CLGeocoder alloc] init];
                    }
                    
                    [self.geocoder reverseGeocodeLocation:self.currentTime.location completionHandler:^(NSArray* placemarks, NSError* error)
                     {
                         NSLog(@"%@", error.localizedDescription);
                         
                         if ([placemarks count] > 0)
                         {
                             CLPlacemark *placemark = [placemarks objectAtIndex:0];
                             
                             // format address in a standard addressbook style
                             NSString *address = ABCreateStringWithAddressDictionary(placemark.addressDictionary, NO);
                             
                             address = [[address componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]] componentsJoinedByString:@" "];
                             
                             self.currentTime.locationAddress = [address copy];
                             
                             dispatch_async(dispatch_get_main_queue(), ^{
                                 [self.tableView reloadData];
                             });
                             
                             
                             if (address.length > 20)
                             {
                                 address = [address substringToIndex:20];
                             }
                             
                             // now send it over to watch
                             // Send data to watch:
                             // See demos/feature_app_messages/weather.c in the native watch app SDK for the same definitions on the watch's end:
                             NSNumber *locationKey = @(0); // This is our custom-defined key for the icon ID, which is of type uint8_t.
                             NSDictionary *update = @{ locationKey:address };
                             
                             [self.targetWatch appMessagesPushUpdate:update onSent:^(PBWatch *watch, NSDictionary *update, NSError *error)
                              {
                                  NSLog(@"%@", update);
//                                  NSString *message = error ? [error localizedDescription] : @"Update sent!";
//                                  dispatch_async(dispatch_get_main_queue(), ^{
//                                      [[[UIAlertView alloc] initWithTitle:nil message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
//                                  });
                              }];
                         }
                     }];
                    
                    const UInt32 loggedTime = [[update objectForKey:[update.allKeys objectAtIndex:0]] intValue];
                    
                    /* Add seconds to GMT, since the watch only keeps local time. */
                    NSTimeZone *timeZone = [NSTimeZone localTimeZone];
                    NSInteger secondsOffset = [timeZone secondsFromGMT];
                    NSTimeInterval secondsSince1970 = loggedTime - secondsOffset;
                    NSDate *date = [NSDate dateWithTimeIntervalSince1970:secondsSince1970];
                    
                    self.currentTime.startDate = date;
                    
                    NSLog(@"%@", date);
                }
                else if (tag == EZTimerStop)
                {
                    const UInt32 loggedTime = [[update objectForKey:[update.allKeys objectAtIndex:0]] intValue];
                    self.currentTime.timeSpent = loggedTime;
                    
                    NSLog(@"%d", (unsigned int)loggedTime);
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.tableView reloadData];
                    });
                }
                
                return YES;
            }];
            
//            NSString *message = [NSString stringWithFormat:@"Yay! %@ supports AppMessages :D", [watch name]];
//            [[[UIAlertView alloc] initWithTitle:@"Connected!" message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
        }
        else
        {
            
            NSString *message = [NSString stringWithFormat:@"Blegh... %@ does NOT support AppMessages :'(", [watch name]];
            [[[UIAlertView alloc] initWithTitle:@"Connected..." message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
        }
    }];
}

#pragma mark - pebble central delegate

- (void)pebbleCentral:(PBPebbleCentral*)central watchDidConnect:(PBWatch*)watch isNew:(BOOL)isNew
{
    [self setTargetWatch:watch];
}

- (void)pebbleCentral:(PBPebbleCentral*)central watchDidDisconnect:(PBWatch*)watch
{
    [[[UIAlertView alloc] initWithTitle:@"Disconnected!" message:[watch name] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    
    if (self.targetWatch == watch || [watch isEqual:self.targetWatch])
    {
        [self setTargetWatch:nil];
    }
}

#pragma mark - pebble data logging delegate

- (BOOL)dataLoggingService:(PBDataLoggingService *)service hasUInt32s:(const UInt32 [])data numberOfItems:(UInt16)numberOfItems forDataLoggingSession:(PBDataLoggingSessionMetadata *)sessionMetadata
{
    
        // we'll get some tag for starting
        switch (sessionMetadata.tag) {
            case EZTimerStart:
            {
                if (numberOfItems > 0)
                {
                    // will contain time
                    const UInt32 loggedTime = data[0];
                    
                    /* Add seconds to GMT, since the watch only keeps local time. */
                    NSTimeZone *timeZone = [NSTimeZone localTimeZone];
                    NSInteger secondsOffset = [timeZone secondsFromGMT];
                    NSTimeInterval secondsSince1970 = loggedTime - secondsOffset;
                    NSDate *date = [NSDate dateWithTimeIntervalSince1970:secondsSince1970];
                    
                    self.currentTime.startDate = date;
                }
                else
                {
                    self.currentTime.startDate = [NSDate date];
                }
                
                break;
            }
            case EZTimerStop:
            {
                // spent time
                NSTimeInterval spentTime;
                
                // stopped timer, calculate spent time (or get it from data) and save record
                if (numberOfItems > 0)
                {
                    // will contain spent time
                    const UInt32 loggedTime = data[0];
                    
                    /* Add seconds to GMT, since the watch only keeps local time. */
                    spentTime = loggedTime;
                }
                else
                {
                   spentTime = [[NSDate date] timeIntervalSinceDate:self.currentTime.startDate];
                }
                
                // add timer object, reload data, cleanup
                self.currentTime.timeSpent = spentTime;
                
                [self.times addObject:self.currentTime];
                [self.tableView reloadData];
                
                self.currentTime = nil;
                
                break;
            }
                
            default:
                break;
        }
    
    // We consumed the data, let the data logging service know:
    return YES;
}

- (void)dataLoggingService:(PBDataLoggingService *)service sessionDidFinish:(PBDataLoggingSessionMetadata *)sessionMetadata
{
    // session finished
    if (self.currentTime != nil)
    {
        // spent time
        NSTimeInterval spentTime = [[NSDate date] timeIntervalSinceDate:self.currentTime.startDate];
        
        // add timer object, reload data, cleanup
        self.currentTime.timeSpent = spentTime;
        
        [self.times addObject:self.currentTime];
        [self.tableView reloadData];
        
        self.currentTime = nil;
    }
}

#pragma mark - location manager delegate

// iOS 5 and earlier:
- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation
{
//    NSLog(@"New Location: %@", newLocation);
    self.currentLocation = newLocation;
}

// iOS 6 and later:
- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    CLLocation *lastLocation = [locations lastObject];
    [self locationManager:manager didUpdateToLocation:lastLocation fromLocation:nil];
}

@end
