#import "MobileSmartWatchPlugin.h"
#if __has_include(<mobile_smart_watch/mobile_smart_watch-Swift.h>)
#import <mobile_smart_watch/mobile_smart_watch-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "mobile_smart_watch-Swift.h"
#endif

//#import <UTESmartBandApi/UTESmartBandApi.h>
//#endif

@implementation MobileSmartWatchPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMobileSmartWatchPlugin registerWithRegistrar:registrar];
}
@end
