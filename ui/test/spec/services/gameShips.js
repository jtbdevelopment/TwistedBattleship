'use strict';

describe('Service: gameShips', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var service, httpBackend;
    var result = [{ship: 'AShip', description: 'a ship', gridSize: 3}, {
        ship: 'Another',
        description: 'another ship',
        gridSize: 4
    }];
    var url = '/api/ships';

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($httpBackend, $injector) {
        httpBackend = $httpBackend;
        service = $injector.get('tbsShips');
    }));

    it('sets ships to http results', function () {
        var ships = null;
        httpBackend.expectGET(url).respond(result);
        service.ships().then(function (data) {
            ships = data;
        }, function (error) {
            ships = error;
        });
        httpBackend.flush();

        expect(ships).to.deep.equal(result);
    });

    it('sets ships to error results', function () {
        var ships = undefined;
        httpBackend.expectGET(url).respond(500);
        var errorCalled = false;
        service.ships().then(function (data) {
            ships = data;
        }, function (error) {
            expect(error).to.not.be.an('undefined');
            errorCalled = true;
        });
        httpBackend.flush();

        expect(errorCalled).to.equal(true);
        expect(ships).to.be.an('undefined');
    });

    it('multiple calls only one http result', function () {
        var ships = null;
        httpBackend.expectGET(url).respond(result);
        service.ships().then(function (data) {
            ships = data;
        }, function (error) {
            ships = error;
        });
        httpBackend.flush();

        expect(ships).to.deep.equal(result);

        service.ships().then(function (data) {
            ships = data;
        }, function (error) {
            ships = error;
        });

        expect(ships).to.deep.equal(result);
    });
});
