/*global AdMob:false */
'use strict';

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

    describe('for ios platform', function () {
        beforeEach(function () {
            platformUnderTest = 'iOS';
        });

        it('initializes correctly', function () {
            service.initialize();
            expect(googleAdSpy.prepareInterstitial.callCount).to.equal(1);
            assert(googleAdSpy.prepareInterstitial.calledWithMatch({
                adId: 'ca-app-pub-8812482609918940/9839986116',
                autoShow: false
            }));
            assert(googleAdSpy.createBanner.calledWithMatch({
                adId: 'ca-app-pub-8812482609918940/3876007710',
                position: AdMob.AD_POSITION.BOTTOM_CENTER,
                autoShow: true
            }));
        });

        describe('calling ads', function () {
            beforeEach(function () {
                service.initialize();
            });

            /*
             it('show interstitial first time', function() {
             window.invokeApplixirVideoUnitExtended = sinon.spy();
             service.showInterstitial();
             // can't test callback function
             assert(window.invokeApplixirVideoUnitExtended.calledWith(false, 'middle'));
             expect(window.invokeApplixirVideoUnitExtended.callCount).to.equal(1);
             });

             it('show interstitial rapidly after first time does nothing second time', function() {
             window.invokeApplixirVideoUnitExtended = sinon.spy();
             service.showInterstitial();
             // can't test callback function
             assert(window.invokeApplixirVideoUnitExtended.calledWith(false, 'middle'));
             expect(window.invokeApplixirVideoUnitExtended.callCount).to.equal(1);
             window.invokeApplixirVideoUnitExtended.getCall(0).args[2]();
             service.showInterstitial();
             expect(window.invokeApplixirVideoUnitExtended.callCount).to.equal(1);
             });
             */
        });
    });
});