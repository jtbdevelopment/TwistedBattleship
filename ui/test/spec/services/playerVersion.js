'use strict';

describe('Service: playerVersion', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var service, httpBackend;
    var result = {'10': [{row: 1, column: 0}, {row: -1, column: 0}], '20': [{row: -2, column: -5}]};
    var url = '/api/player/lastVersionNotes/';

    var popupCalled;

    var player = {
        lastVersionNotes: '3.0'
    };
    beforeEach(module(function ($provide) {
        $provide.factory('$ionicPopup', function () {
            return {
                alert: function (params) {
                    popupCalled = params;
                }
            };
        });
        $provide.factory('jtbPlayerService', function () {
            return {
                currentPlayer: function () {
                    return player;
                }
            };
        });
    }));


    // Initialize the controller and a mock scope
    beforeEach(inject(function ($httpBackend, $injector) {
        httpBackend = $httpBackend;
        popupCalled = undefined;
        service = $injector.get('tbsVersionNotes');
    }));

    it('no alert if player beats current version', function () {
        player.lastVersionNotes = '9999999.00'; // big version
        service.showReleaseNotes();
        expect(popupCalled).to.equal(undefined);
    });

    it('no alert if player equals current version', function () {
        player.lastVersionNotes = CURRENT_VERSION; // big version
        service.showReleaseNotes();
        expect(popupCalled).to.equal(undefined);
    });

    it('generates alert if player version is less than current', function () {
        player.lastVersionNotes = '0.1'; // low version
        httpBackend.expectPOST(url + CURRENT_VERSION).respond(200);
        service.showReleaseNotes();
    });
});
