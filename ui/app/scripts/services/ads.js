'use strict';

angular.module('tbs.services').factory('tbsAds',
    ['$cordovaGoogleAds', '$cordovaDevice',
        function ($cordovaGoogleAds, $cordovaDevice) {
            var IOS = 'iOS';
            var BROWSER = 'browser';

            var initialized = false;
            var platform = '';

            return {
                initialize: function () {
                    if (!initialized) {
                        try {
                            platform = $cordovaDevice.getPlatform();
                        } catch (ex) {
                            platform = BROWSER;
                        }
                        console.log('Platform: ' + platform);
                        switch (platform) {
                            case BROWSER:
                                break;
                            case IOS:
                                break;
                        }
                        initialized = true;
                    }
                },

                showInterstitial: function () {

                },

                showBanner: function () {

                }
            };
        }
    ]
);
