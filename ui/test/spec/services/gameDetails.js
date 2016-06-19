'use strict';

describe('Service: gameDetails', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));


    var phases = ['Challenged', 'Declined', 'Quit', 'Setup', 'Playing', 'RoundOver', 'NextRoundStarted'];
    var players = ['md1', 'md2', 'md3', 'md4', 'md5'];

    var game;
    var gameBase = {
        id: 'id',
        players: {'md1': 'P1', 'md2': 'P2', 'md3': 'P3', 'md4': 'P4', 'md5': 'P5'},
        playerStates: {
            md1: 'Pending',
            md2: 'Accepted',
            md3: 'Declined',
            md4: 'Quit',
            md5: 'Accepted',
            md6: 'Rejected'
        },
        playerImages: {
            md1: 'someimagelink',
            md2: 'anotherlink',
            md3: null
        },
        playerProfiles: {
            md1: 'someprofile',
            md2: 'anotherprofile'
        },
        featureData: {},
        features: ['Grid10x10', 'ECMEnabled', 'SpyingEnabled', 'EREnabled', 'EMEnabled', 'SharedIntel', 'Single'],
        gridSize: 10,
        playersScore: {'md1': 1, 'md2': 0, 'md3': -1, 'md4': 3, 'md5': 2},
        playersSetup: {'md1': true, 'md2': false, 'md3': false, 'md4': true, 'md5': false},
        playersAlive: {'md1': false, 'md2': false, 'md3': true, 'md4': true, 'md5': false},
        currentPlayer: 'md2'
    };

    var service;

    var phaseDeferred;
    beforeEach(module(function ($provide) {
        $provide.factory('jtbGamePhaseService', ['$q', function ($q) {
            return {
                phases: function () {
                    phaseDeferred = $q.defer();
                    return phaseDeferred.promise;
                }
            };
        }]);
    }));

    var serverPhases = {
        Phase1: ['', 'Phase 1'],
        Phase2: ['x', 'A 2nd Phase'],
        Phase3: ['hh', '']
    };

    var rootScope;
    // Initialize the controller and a mock scope
    beforeEach(inject(function ($injector, $rootScope) {
        game = angular.copy(gameBase);
        rootScope = $rootScope;
        service = $injector.get('tbsGameDetails');
    }));

    it('phase descriptions', function () {
        phaseDeferred.resolve(serverPhases);
        rootScope.$apply();
        expect(service.descriptionForPhase('Phase1')).to.equal(serverPhases.Phase1[1]);
        expect(service.descriptionForPhase('Phase2')).to.equal(serverPhases.Phase2[1]);
        expect(service.descriptionForPhase('Phase3')).to.equal(serverPhases.Phase3[1]);
    });

    it('icons for features', function () {
        expect(service.iconForFeature('SpyEnabled')).to.equal('eye');
        expect(service.iconForFeature('CruiseMissileDisabled')).to.equal('disc');
        expect(service.iconForFeature('ECMEnabled')).to.equal('eye-disabled');
        expect(service.iconForFeature('SharedIntel')).to.equal('images');
        expect(service.iconForFeature('IsolatedIntel')).to.equal('image');
        expect(service.iconForFeature('EREnabled')).to.equal('wrench');
        expect(service.iconForFeature('EMEnabled')).to.equal('shuffle');
        expect(service.iconForFeature('Single')).to.equal('chatbox');
        expect(service.iconForFeature('PerShip')).to.equal('chatboxes');
        expect(service.iconForFeature('Grid10x10')).to.equal('crop');
        expect(service.iconForFeature('Grid15x15')).to.equal('crop');
        expect(service.iconForFeature('Grid20x20')).to.equal('crop');
        expect(service.iconForFeature('StandardShips')).to.equal('help-buoy');
        expect(service.iconForFeature('AllCarriers')).to.equal('help-buoy');
        expect(service.iconForFeature('AllCruisers')).to.equal('help-buoy');
        expect(service.iconForFeature('AllBattleships')).to.equal('help-buoy');
        expect(service.iconForFeature('AllDestroyers')).to.equal('help-buoy');
        expect(service.iconForFeature('AllDestroyers')).to.equal('help-buoy');
    });

    describe('testing enabled flags for player not current turn', function () {
        beforeEach(function () {
            game.currentPlayer = 'md2';
            game.gamePhase = 'Playing';
        });

        it('ecm', function () {
            expect(service.ecmPossible(game, 'md1')).to.equal(false);
        });
        it('spy', function () {
            expect(service.spyPossible(game, 'md1')).to.equal(false);
        });
        it('cruise missle', function () {
            expect(service.cruiseMissilePossible(game, 'md1')).to.equal(false);
        });
        it('repair', function () {
            expect(service.repairPossible(game, 'md1')).to.equal(false);
        });
        it('move', function () {
            expect(service.evasiveMovePossible(game, 'md1')).to.equal(false);
        });
    });

    describe('testing enabled flags for player current turn', function () {
        beforeEach(function () {
            game.currentPlayer = 'md1';
            game.gamePhase = 'Playing';
            game.maskedPlayersState = {};
        });

        describe('with no specials remaining', function () {
            beforeEach(function () {
                game.maskedPlayersState.ecmsRemaining = 0;
                game.maskedPlayersState.spysRemaining = 0;
                game.maskedPlayersState.cruiseMissilesRemaining = 0;
                game.maskedPlayersState.emergencyRepairsRemaining = 0;
                game.maskedPlayersState.evasiveManeuversRemaining = 0;
            });
            it('ecm', function () {
                expect(service.ecmPossible(game, 'md1')).to.equal(false);
            });
            it('spy', function () {
                expect(service.spyPossible(game, 'md1')).to.equal(false);
            });
            it('cruise missile', function () {
                expect(service.cruiseMissilePossible(game, 'md1')).to.equal(false);
            });
            it('repair', function () {
                expect(service.repairPossible(game, 'md1')).to.equal(false);
            });
            it('move', function () {
                expect(service.evasiveMovePossible(game, 'md1')).to.equal(false);
            });
        });

        describe('with specials remaining, but insufficient remaining moves', function () {
            beforeEach(function () {
                game.maskedPlayersState.ecmsRemaining = 1;
                game.maskedPlayersState.spysRemaining = 1;
                game.maskedPlayersState.emergencyRepairsRemaining = 1;
                game.maskedPlayersState.evasiveManeuversRemaining = 1;
                game.maskedPlayersState.cruiseMissilesRemaining = 1;
                game.remainingMoves = 1;
                game.movesForSpecials = 2;
            });
            it('ecm', function () {
                expect(service.ecmPossible(game, 'md1')).to.equal(false);
            });
            it('spy', function () {
                expect(service.spyPossible(game, 'md1')).to.equal(false);
            });
            it('cruise missile', function () {
                expect(service.cruiseMissilePossible(game, 'md1')).to.equal(false);
            });
            it('repair', function () {
                expect(service.repairPossible(game, 'md1')).to.equal(false);
            });
            it('move', function () {
                expect(service.evasiveMovePossible(game, 'md1')).to.equal(false);
            });
        });

        describe('with specials remaining and sufficient remaining moves', function () {
            beforeEach(function () {
                game.maskedPlayersState.ecmsRemaining = 1;
                game.maskedPlayersState.spysRemaining = 1;
                game.maskedPlayersState.emergencyRepairsRemaining = 1;
                game.maskedPlayersState.evasiveManeuversRemaining = 1;
                game.maskedPlayersState.cruiseMissilesRemaining = 1;
                game.remainingMoves = 2;
                game.movesForSpecials = 2;
            });
            it('ecm', function () {
                expect(service.ecmPossible(game, 'md1')).to.equal(true);
            });
            it('spy', function () {
                expect(service.spyPossible(game, 'md1')).to.equal(true);
            });
            it('cruise missile', function () {
                expect(service.cruiseMissilePossible(game, 'md1')).to.equal(true);
            });
            it('repair', function () {
                expect(service.repairPossible(game, 'md1')).to.equal(true);
            });
            it('move', function () {
                expect(service.evasiveMovePossible(game, 'md1')).to.equal(true);
            });
        });
    });

    it('test player response needed for non challenged phases', function () {
        angular.forEach(phases, function (phase) {
            if (phase !== 'Challenged') {
                game.gamePhase = phase;
                angular.forEach(players, function (player) {
                    expect(service.playerChallengeResponseNeeded(game, player)).to.equal(false);
                });
            }
        });
    });

    it('test player response needed for challenged phases', function () {
        game.gamePhase = 'Challenged';
        angular.forEach(players, function (player) {
            expect(service.playerChallengeResponseNeeded(game, player)).to.equal(player === 'md1');
        });
    });

    it('test player response needed - bad parameters', function () {
        expect(service.playerChallengeResponseNeeded()).to.equal(false);
        expect(service.playerChallengeResponseNeeded({})).to.equal(false);
        expect(service.playerChallengeResponseNeeded({}, '  ')).to.equal(false);
    });

    it('test player response needed for non play phases', function () {
        angular.forEach(phases, function (phase) {
            if (phase !== 'Playing') {
                game.gamePhase = phase;
                angular.forEach(players, function (player) {
                    expect(service.playerCanPlay(game, player)).to.equal(false);
                });
            }
        });
    });

    it('test player response needed for playing phases', function () {
        game.gamePhase = 'Playing';
        angular.forEach(players, function (player) {
            expect(service.playerCanPlay(game, player)).to.equal(player === 'md2');
        });
    });

    it('test player can play - bad parameters', function () {
        expect(service.playerCanPlay()).to.equal(false);
        expect(service.playerCanPlay({})).to.equal(false);
        expect(service.playerCanPlay({}, '  ')).to.equal(false);
    });

    it('test player setup needed for non setup phases', function () {
        angular.forEach(phases, function (phase) {
            if (phase !== 'Setup') {
                game.gamePhase = phase;
                angular.forEach(players, function (player) {
                    expect(service.playerSetupEntryRequired(game, player)).to.equal(false);
                });
            }
        });
    });

    it('test player response needed for setup phases', function () {
        game.gamePhase = 'Setup';
        angular.forEach(players, function (player) {
            expect(service.playerSetupEntryRequired(game, player)).to.equal(!(player === 'md1' || player === 'md4'));
        });
    });

    it('test player setup required - bad parameters', function () {
        expect(service.playerSetupEntryRequired()).to.equal(false);
        expect(service.playerSetupEntryRequired({})).to.equal(false);
        expect(service.playerSetupEntryRequired({}, '  ')).to.equal(false);
    });

    it('test player rematch not possible - bad parameters', function () {
        expect(service.playerRematchPossible()).to.equal(false);
        expect(service.playerRematchPossible({})).to.equal(false);
        expect(service.playerRematchPossible({}, '  ')).to.equal(false);
    });

    it('test player rematch possible for RoundOver', function () {
        game.gamePhase = 'RoundOver';
        expect(service.playerRematchPossible(game, 'md1')).to.equal(true);
    });

    it('test player rematch not possible for other phases', function () {
        angular.forEach(['Playing', 'Setup', 'Challenged', 'NextRoundStarted', 'Quit', 'Declined'], function (phase) {
            game.gamePhase = phase;
            expect(service.playerRematchPossible(game, 'md1')).to.equal(false);
        });
    });

    it('test player image', function () {
        expect(service.imageForPlayer(game, 'md1')).to.equal('someimagelink');
        expect(service.imageForPlayer(game, 'md2')).to.equal('anotherlink');
        expect(service.imageForPlayer(game, 'md3')).to.equal('images/ionic.png');
        expect(service.imageForPlayer(game, 'md4')).to.equal('images/ionic.png');
        expect(service.imageForPlayer(game, 'md5')).to.equal('images/ionic.png');
    });

    it('test player image - bad parameters', function () {
        expect(service.imageForPlayer()).to.equal('images/ionic.png');
        expect(service.imageForPlayer({})).to.equal('images/ionic.png');
        expect(service.imageForPlayer({}, '  ')).to.equal('images/ionic.png');
    });

    it('test short game description - general', function () {
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "shuffle", "wrench", "crop", "images"],
            playerAction: false
        });

        game.features = ['Grid15x15', 'ECMDisabled', 'SpyingEnabled', 'EREnabled', 'EMEnabled', 'SharedIntel', 'Single'];
        game.gridSize = 15;
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '15x15',
            actionsText: 'Single',
            icons: ["shuffle", "wrench", "crop", "images"],
            playerAction: false
        });


        game.features = ['Grid20x20', 'ECMEnabled', 'IsolatedIntel', 'PerShip'];
        game.gridSize = 20;
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '20x20',
            actionsText: 'Multiple',
            icons: ["eye-disabled", "crop", "image"],
            playerAction: false
        });

    });

    it('test short game description - playerAction', function () {
        game.gamePhase = 'Setup';
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "shuffle", "wrench", "crop", "images"],
            playerAction: false
        });
        expect(service.shortGameDescription(game, 'md2')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "shuffle", "wrench", "crop", "images"],
            playerAction: true
        });

        game.gamePhase = 'Challenged';
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "shuffle", "wrench", "crop", "images"],
            playerAction: true
        });
        expect(service.shortGameDescription(game, 'md2')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "shuffle", "wrench", "crop", "images"],
            playerAction: false
        });

        game.gamePhase = 'Playing';
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "shuffle", "wrench", "crop", "images"],
            playerAction: false
        });
        expect(service.shortGameDescription(game, 'md2')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "shuffle", "wrench", "crop", "images"],
            playerAction: true
        });
    });

    it('test short game description bad game', function () {
        expect(service.shortGameDescription()).to.equal('Game details missing!');
    });

    it('test shorten grid description bad game', function () {
        expect(service.shortenGridSize()).to.equal('');
    });

    it('test last action log for bad game', function () {
        expect(service.lastActionLog()).to.equal('');
    });

    it('test last action log for game', function () {
        game.maskedPlayersState = {};
        game.maskedPlayersState.actionLog = [];
        game.maskedPlayersState.actionLog.push({description: 'Line1'});
        game.maskedPlayersState.actionLog.push({description: 'Line2'});
        game.maskedPlayersState.actionLog.push({description: 'Line3'});
        expect(service.lastActionLog(game)).to.equal('Line3');
    });

    it('test last action time for bad game', function () {
        expect(service.lastActionTime()).to.equal('');
    });

    it('test last action time for game', function () {
        game.maskedPlayersState = {};
        game.maskedPlayersState.actionLog = [];
        game.maskedPlayersState.actionLog.push({timestamp: 12878});
        game.maskedPlayersState.actionLog.push({timestamp: 38989});
        game.maskedPlayersState.actionLog.push({timestamp: 918311109983});
        expect(service.lastActionTime(game)).to.equal(new Date(918311109983).toLocaleString());
    });


    it('test format action time', function () {
        expect(service.formatActionTime(1023149344)).to.equal(new Date(1023149344).toLocaleString());
    });
});


