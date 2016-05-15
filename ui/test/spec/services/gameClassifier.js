'use strict';

describe('Service: gameClassifier', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var playerMD5 = 'anmd5!';
    var mockPlayerService = {
        currentPlayer: function () {
            return {md5: playerMD5};
        }
    };

    var mockCanPlay = false, mockSetupNeeded = false, mockRematchPossible = false, mockResponseNeeded = false;
    var mockGameDetailsService = {
        playerCanPlay: function (game, md5) {
            expect(md5).to.equal(playerMD5);
            return mockCanPlay;
        },
        playerChallengeResponseNeeded: function (game, md5) {
            expect(md5).to.equal(playerMD5);
            return mockResponseNeeded;
        },
        playerRematchPossible: function (game, md5) {
            expect(md5).to.equal(playerMD5);
            return mockRematchPossible;
        },
        playerSetupEntryRequired: function (game, md5) {
            expect(md5).to.equal(playerMD5);
            return mockSetupNeeded;
        }

    };

    beforeEach(module(function ($provide) {
        $provide.factory('jtbPlayerService', [function () {
            return mockPlayerService;
        }]);
        $provide.factory('tbsGameDetails', [function () {
            return mockGameDetailsService;
        }])
    }));

    var service;
    beforeEach(inject(function ($injector) {
        mockCanPlay = false;
        mockSetupNeeded = false;
        mockRematchPossible = false;
        mockResponseNeeded = false;
        service = $injector.get('jtbGameClassifier');
    }));

    var expectedYourTurnClassification = 'Your move.';
    var expectedTheirTurnClassification = 'Their move.';
    var expectedOlderGameClassification = 'Older games.';
    var expectedIconMap = {};
    expectedIconMap[expectedYourTurnClassification] = 'play';
    expectedIconMap[expectedTheirTurnClassification] = 'pause';
    expectedIconMap[expectedOlderGameClassification] = 'stop';

    it('get classifications', function () {
        expect(service.getClassifications()).to.deep.equal([expectedYourTurnClassification, expectedTheirTurnClassification, expectedOlderGameClassification]);
    });

    it('get icon map', function () {
        expect(service.getIcons()).to.deep.equal(expectedIconMap);
    });

    it('classification for no player action needed', function () {
        var game = {gamePhase: 'TBD'};
        expect(service.getClassification(game)).to.equal(expectedTheirTurnClassification);
    });

    it('classification for player setup needed', function () {
        var game = {gamePhase: 'TBD'};
        mockSetupNeeded = true;
        expect(service.getClassification(game)).to.equal(expectedYourTurnClassification);
    });

    it('classification for player challenge response needed', function () {
        var game = {gamePhase: 'TBD'};
        mockResponseNeeded = true;
        expect(service.getClassification(game)).to.equal(expectedYourTurnClassification);
    });

    it('classification for player rematch possible', function () {
        var game = {gamePhase: 'TBD'};
        mockRematchPossible = true;
        expect(service.getClassification(game)).to.equal(expectedYourTurnClassification);
    });

    it('classification for player turn', function () {
        var game = {gamePhase: 'TBD'};
        mockCanPlay = true;
        expect(service.getClassification(game)).to.equal(expectedYourTurnClassification);
    });

    angular.forEach(['Declined', 'Quit', 'NextRoundStarted'], function (phase) {
        it('classification for phase ' + phase, function () {
            var game = {gamePhase: phase};
            expect(service.getClassification(game)).to.equal(expectedOlderGameClassification);
        });
    });
});