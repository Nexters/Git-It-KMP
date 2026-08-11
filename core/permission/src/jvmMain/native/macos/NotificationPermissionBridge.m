#import <Foundation/Foundation.h>
#import <UserNotifications/UserNotifications.h>

#include <stdint.h>

typedef void (*GitItNotificationPermissionCallback)(int32_t status);

typedef NS_ENUM(int32_t, GitItNotificationPermissionStatus) {
    GitItNotificationPermissionStatusNotDetermined = 0,
    GitItNotificationPermissionStatusDenied = 1,
    GitItNotificationPermissionStatusAuthorized = 2,
    GitItNotificationPermissionStatusProvisional = 3,
    GitItNotificationPermissionStatusUnavailable = -1,
};

static int32_t GitItMapAuthorizationStatus(UNAuthorizationStatus status) {
    switch (status) {
        case UNAuthorizationStatusNotDetermined:
            return GitItNotificationPermissionStatusNotDetermined;
        case UNAuthorizationStatusDenied:
            return GitItNotificationPermissionStatusDenied;
        case UNAuthorizationStatusAuthorized:
            return GitItNotificationPermissionStatusAuthorized;
        case UNAuthorizationStatusProvisional:
            return GitItNotificationPermissionStatusProvisional;
        default:
            return GitItNotificationPermissionStatusUnavailable;
    }
}

static void GitItPublishCurrentAuthorizationStatus(GitItNotificationPermissionCallback callback) {
    UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
    [center getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings *settings) {
        callback(GitItMapAuthorizationStatus(settings.authorizationStatus));
    }];
}

__attribute__((visibility("default")))
void gititNotificationPermissionStatus(GitItNotificationPermissionCallback callback) {
    if (callback == NULL) {
        return;
    }

    @autoreleasepool {
        GitItPublishCurrentAuthorizationStatus(callback);
    }
}

__attribute__((visibility("default")))
void gititRequestNotificationPermission(GitItNotificationPermissionCallback callback) {
    if (callback == NULL) {
        return;
    }

    @autoreleasepool {
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        UNAuthorizationOptions options =
            UNAuthorizationOptionAlert | UNAuthorizationOptionSound | UNAuthorizationOptionBadge;
        [center requestAuthorizationWithOptions:options
                              completionHandler:^(BOOL granted, NSError *error) {
            if (error != nil) {
                callback(GitItNotificationPermissionStatusUnavailable);
                return;
            }
            GitItPublishCurrentAuthorizationStatus(callback);
        }];
    }
}
