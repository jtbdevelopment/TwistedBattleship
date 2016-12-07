'use strict';

describe('Service: gameCircles', function () {
    beforeEach(module('tbs.services'));

    var service, httpBackend;
    var result = {'10': [{row: 1, column: 0}, {row: -1, column: 0}], '20': [{row: -2, column: -5}]};
    var url = '/api/circles';

    beforeEach(inject(function ($httpBackend, $injector) {
        httpBackend = $httpBackend;
        service = $injector.get('tbsCircles');
    }));

    it('sets circles to http results', function () {
        var circles = null;
        httpBackend.expectGET(url).respond(result);
        service.circles().then(function (data) {
            circles = data;
        }, function (error) {
            circles = error;
        });
        httpBackend.flush();

        expect(circles).to.deep.equal(result);
    });

    it('sets circles to error results', function () {
        var circles = undefined;
        httpBackend.expectGET(url).respond(500);
        var errorCalled = false;
        service.circles().then(function (data) {
            circles = data;
        }, function (error) {
            expect(error).to.not.be.an('undefined');
            errorCalled = true;
        });
        httpBackend.flush();

        expect(errorCalled).to.equal(true);
        expect(circles).to.be.an('undefined');
    });

    it('multiple calls only one http result', function () {
        var circles = null;
        httpBackend.expectGET(url).respond(result);
        service.circles().then(function (data) {
            circles = data;
        }, function (error) {
            circles = error;
        });
        httpBackend.flush();

        expect(circles).to.deep.equal(result);

        service.circles().then(function (data) {
            circles = data;
        }, function (error) {
            circles = error;
        });

        expect(circles).to.deep.equal(result);
    });
});
