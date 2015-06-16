'use strict';

describe('Service: gameFeatures', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var service, httpBackend;
    var result = {AFeature: 'That does X', AnotherFeature: ['An Array', 'Of Strings']};

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($httpBackend, $injector) {
        httpBackend = $httpBackend;
        service = $injector.get('jtbGameFeatureService');
    }));

    it('sets features to http results', function () {
        var features = null;
        httpBackend.expectGET('/api/features').respond(result);
        service.features().then(function (data) {
            features = data;
        }, function (error) {
            features = error;
        });
        httpBackend.flush();

        expect(features).to.deep.equal(result);
    });

    it('sets features to error results', function () {
        var features;
        httpBackend.expectGET('/api/features').respond(500);
        var errorCalled = false;
        service.features().then(function (data) {
            features = data;
        }, function (error) {
            expect(error).to.not.be.an('undefined');
            errorCalled = true;
        });
        httpBackend.flush();

        expect(errorCalled).to.equal(true);
        expect(features).to.be.an('undefined');
    });

    it('multiple calls only one http result', function () {
        var features = null;
        httpBackend.expectGET('/api/features').respond(result);
        service.features().then(function (data) {
            features = data;
        }, function (error) {
            features = error;
        });
        httpBackend.flush();

        expect(features).to.deep.equal(result);

        service.features().then(function (data) {
            features = data;
        }, function (error) {
            features = error;
        });

        expect(features).to.deep.equal(result);
    });
});
