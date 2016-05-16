/*global AdMob:false */
'use strict';

window.AdMob = {
    AD_POSITION: {
        NO_CHANGE: 0,
        TOP_LEFT: 1,
        TOP_CENTER: 2,
        TOP_RIGHT: 3,
        LEFT: 4,
        CENTER: 5,
        RIGHT: 6,
        BOTTOM_LEFT: 7,
        BOTTOM_CENTER: 8,
        BOTTOM_RIGHT: 9,
        POS_XY: 10
    }
};

describe('Service: ads', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var googleAdSpy = {};
    var platformUnderTest = 'browser';
    var mockCordovaDevice = {
        getPlatform: function () {
            return platformUnderTest
        }
    };
    beforeEach(module(function ($provide) {
        $provide.factory('$cordovaDevice', [function () {
            return mockCordovaDevice;
        }]);
        $provide.factory('$cordovaGoogleAds', [function () {
            return googleAdSpy;
        }]);
    }));

    var service;
    beforeEach(inject(function ($injector) {
        googleAdSpy.prepareInterstitial = sinon.spy();
        googleAdSpy.createBanner = sinon.spy();
        googleAdSpy.showInterstitial = sinon.spy();
        service = $injector.get('tbsAds');
    }));

    describe('for browser platform', function () {
        beforeEach(function () {
            platformUnderTest = 'browser';
        });

        it('initializes correctly', function () {
            service.initialize();
            //  tough to test beyond this since it is applixir provided logic applied to html
        });

        describe('calling ads', function () {
            beforeEach(function () {
                service.initialize();
            });

            it('show interstitial first time', function () {
                window.invokeApplixirVideoUnitExtended = sinon.spy();
                service.showInterstitial();
                // can't test callback function
                assert(window.invokeApplixirVideoUnitExtended.calledWith(false, 'middle'));
                expect(window.invokeApplixirVideoUnitExtended.callCount).to.equal(1);
            });

            it('show interstitial rapidly after first time does nothing second time', function () {
                window.invokeApplixirVideoUnitExtended = sinon.spy();
                service.showInterstitial();
                // can't test callback function
                assert(window.invokeApplixirVideoUnitExtended.calledWith(false, 'middle'));
                expect(window.invokeApplixirVideoUnitExtended.callCount).to.equal(1);
                window.invokeApplixirVideoUnitExtended.getCall(0).args[2]();
                service.showInterstitial();
                expect(window.invokeApplixirVideoUnitExtended.callCount).to.equal(1);
            });
        });
    });

    var platformsToTest = {
        iOS: ['ca-app-pub-8812482609918940/9839986116', 'ca-app-pub-8812482609918940/2316719315'],
        Android: ['ca-app-pub-8812482609918940/5352740910', 'ca-app-pub-8812482609918940/3876007710']
    };
    angular.forEach(platformsToTest, function (keys, platform) {
        describe('for ' + platform + ' platform', function () {
            beforeEach(function () {
                platformUnderTest = platform;
            });

            it('initializes correctly', function () {
                service.initialize();
                expect(googleAdSpy.prepareInterstitial.callCount).to.equal(1);
                assert(googleAdSpy.prepareInterstitial.calledWithMatch({
                    adId: keys[0],
                    autoShow: false
                }));
                expect(googleAdSpy.createBanner.callCount).to.equal(1);
                assert(googleAdSpy.createBanner.calledWithMatch({
                    adId: keys[1],
                    position: AdMob.AD_POSITION.BOTTOM_CENTER,
                    autoShow: true
                }));
            });

            describe('calling ads', function () {
                beforeEach(function () {
                    service.initialize();
                });

                it('show interstitial first time', function () {
                    window.invokeApplixirVideoUnitExtended = sinon.spy();
                    service.showInterstitial();
                    expect(googleAdSpy.showInterstitial.callCount).to.equal(1);
                    assert(googleAdSpy.showInterstitial.calledWithMatch());
                });

                it('show interstitial rapidly after first time does nothing second time', function () {
                    window.invokeApplixirVideoUnitExtended = sinon.spy();
                    service.showInterstitial();

                    //  TODO - ugly
                    var event = document.createEvent('CustomEvent');
                    console.log(JSON.stringify(event));
                    event.initEvent('onAdDismiss', true, false);
                    event.adType = 'interstitial';
                    console.log(JSON.stringify(event));
                    document.dispatchEvent(event);

                    expect(googleAdSpy.showInterstitial.callCount).to.equal(1);
                    assert(googleAdSpy.showInterstitial.calledWithMatch());
                    service.showInterstitial();
                    expect(googleAdSpy.showInterstitial.callCount).to.equal(1);
                });
            });
        });
    });
});