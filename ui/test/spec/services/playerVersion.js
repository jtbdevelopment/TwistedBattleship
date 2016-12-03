'use strict';

describe('Service: playerVersion', function () {
    beforeEach(module('tbs.services'));

    var service;
    var popupCalled;

    var player = {
        lastVersionNotes: '3.0'
    };
    var playerService = {
        currentPlayer: function () {
            return player;
        }
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
            return playerService;
        });
    }));


    // Initialize the controller and a mock scope
    beforeEach(inject(function ($injector) {
        popupCalled = undefined;
        playerService.updateLastVersionNotes = sinon.spy();
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
        service.showReleaseNotes();
        assert(playerService.updateLastVersionNotes.calledWithMatch(CURRENT_VERSION));
    });

    it('generates alert if player version is less than current but update server fails', function () {
        player.lastVersionNotes = '0.1'; // low version
        service.showReleaseNotes();
        assert(playerService.updateLastVersionNotes.calledWithMatch(CURRENT_VERSION));
    });
});
