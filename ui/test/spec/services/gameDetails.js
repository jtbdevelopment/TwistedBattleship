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
            md2: 'anotherlink'
        },
        playerProfiles: {
            md1: 'someprofile',
            md2: 'anotherprofile'
        },
        featureData: {},
        features: ['Grid10x10', 'ECMEnabled', 'SpyingEnabled', 'EREnabled', 'EMEnabled', 'SharedIntel', 'Single', 'CriticalEnabled'],
        playersScore: {'md1': 1, 'md2': 0, 'md3': -1, 'md4': 3, 'md5': 2},
        playersSetup: {'md1': true, 'md2': false, 'md3': false, 'md4': true, 'md5': false},
        playersAlive: {'md1': false, 'md2': false, 'md3': true, 'md4': true, 'md5': false},
        currentPlayer: 'md2'
    };

    var service;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($injector) {
        game = angular.copy(gameBase);
        service = $injector.get('tbsGameDetails');
    }));

    it('player icon based on state', function () {
        expect(service.stateIconForPlayer(game, 'md1')).to.equal('speakerphone');
        expect(service.stateIconForPlayer(game, 'md2')).to.equal('thumbsup');
        expect(service.stateIconForPlayer(game, 'md3')).to.equal('help');
        expect(service.stateIconForPlayer(game, 'md4')).to.equal('flag');
        expect(service.stateIconForPlayer(game, 'md5')).to.equal('thumbsup');
        expect(service.stateIconForPlayer(game, 'md6')).to.equal('thumbsdown');
    });

    it('test state icon - bad parameters', function () {
        expect(service.stateIconForPlayer()).to.equal('help');
        expect(service.stateIconForPlayer({})).to.equal('help');
        expect(service.stateIconForPlayer({}, '  ')).to.equal('help');
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

    it('test player end for non end phases', function () {
        angular.forEach(phases, function (phase) {
            if (phase !== 'Playing' && phase !== 'RoundOver' && phase !== 'NextRoundStarted') {
                game.gamePhase = phase;
                angular.forEach(players, function (player) {
                    expect(service.gameEndForPlayer(game, player)).to.equal('');
                });
            }
        });
    });

    it('test player end needed for end phases', function () {
        angular.forEach(phases, function (phase) {
            if (phase === 'Playing' || phase === 'RoundOver' || phase === 'NextRoundStarted') {
                game.gamePhase = phase;
                var alive = (phase === 'Playing') ? 'Still Playing.' : 'Winner!';
                angular.forEach(players, function (player) {
                    expect(service.gameEndForPlayer(game, player)).to.equal((player === 'md4' || player === 'md3') ? alive : 'Defeated!');
                });
            }
        });
    });

    it('test player end - bad parameters', function () {
        expect(service.gameEndForPlayer()).to.equal('');
        expect(service.gameEndForPlayer({})).to.equal('');
        expect(service.gameEndForPlayer({}, '  ')).to.equal('');
    });

    it('test player end icon for non end phases', function () {
        angular.forEach(phases, function (phase) {
            if (phase !== 'Playing' && phase !== 'RoundOver' && phase !== 'NextRoundStarted') {
                game.gamePhase = phase;
                angular.forEach(players, function (player) {
                    expect(service.gameEndIconForPlayer(game, player)).to.equal('help');
                });
            }
        });
    });

    it('test player end needed for end phases', function () {
        angular.forEach(phases, function (phase) {
            if (phase === 'Playing' || phase === 'RoundOver' || phase === 'NextRoundStarted') {
                game.gamePhase = phase;
                var alive = (phase === 'Playing') ? 'load-a' : 'ribbon-a';
                angular.forEach(players, function (player) {
                    expect(service.gameEndIconForPlayer(game, player)).to.equal((player === 'md4' || player === 'md3') ? alive : 'sad-outline');
                });
            }
        });
    });

    it('test player end icon - bad parameters', function () {
        expect(service.gameEndIconForPlayer()).to.equal('help');
        expect(service.gameEndIconForPlayer({})).to.equal('help');
        expect(service.gameEndIconForPlayer({}, '  ')).to.equal('help');
    });

    it('test player score', function () {
        expect(service.gameScoreForPlayer(game, 'md1')).to.equal(1);
        expect(service.gameScoreForPlayer(game, 'md2')).to.equal(0);
        expect(service.gameScoreForPlayer(game, 'md3')).to.equal(-1);
        expect(service.gameScoreForPlayer(game, 'md4')).to.equal(3);
        expect(service.gameScoreForPlayer(game, 'md5')).to.equal(2);
    });

    it('test player score - bad parameters', function () {
        expect(service.gameScoreForPlayer()).to.equal('');
        expect(service.gameScoreForPlayer({})).to.equal('');
        expect(service.gameScoreForPlayer({}, '  ')).to.equal('');
    });

    it('test player profile', function () {
        expect(service.profileForPlayer(game, 'md1')).to.equal('someprofile');
        expect(service.profileForPlayer(game, 'md2')).to.equal('anotherprofile');
        expect(service.profileForPlayer(game, 'md3')).to.equal('');
        expect(service.profileForPlayer(game, 'md4')).to.equal('');
        expect(service.profileForPlayer(game, 'md5')).to.equal('');
    });

    it('test player profile - bad parameters', function () {
        expect(service.profileForPlayer()).to.equal('');
        expect(service.profileForPlayer({})).to.equal('');
        expect(service.profileForPlayer({}, '  ')).to.equal('');
    });

    it('test player image', function () {
        expect(service.imageForPlayer(game, 'md1')).to.equal('someimagelink');
        expect(service.imageForPlayer(game, 'md2')).to.equal('anotherlink');
        expect(service.imageForPlayer(game, 'md3')).to.equal(null);
        expect(service.imageForPlayer(game, 'md4')).to.equal(null);
        expect(service.imageForPlayer(game, 'md5')).to.equal(null);
    });

    it('test player image - bad parameters', function () {
        expect(service.imageForPlayer()).to.equal(null);
        expect(service.imageForPlayer({})).to.equal(null);
        expect(service.imageForPlayer({}, '  ')).to.equal(null);
    });

    it('test short game description - general', function () {
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "wrench", "shuffle", "images", "alert"],
            playerAction: false
        });

        game.features = ['Grid15x15', 'ECMDisabled', 'SpyingEnabled', 'EREnabled', 'EMEnabled', 'SharedIntel', 'Single', 'CriticalDisabled'];
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '15x15',
            actionsText: 'Single',
            icons: ["wrench", "shuffle", "images"],
            playerAction: false
        });


        game.features = ['Grid20x20', 'ECMEnabled', 'IsolatedIntel', 'PerShip'];
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '20x20',
            actionsText: 'Multiple',
            icons: ["eye-disabled", "image"],
            playerAction: false
        });

    });

    it('test short game description - playerAction', function () {
        game.gamePhase = 'Setup';
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "wrench", "shuffle", "images", "alert"],
            playerAction: false
        });
        expect(service.shortGameDescription(game, 'md2')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "wrench", "shuffle", "images", "alert"],
            playerAction: true
        });

        game.gamePhase = 'Challenged';
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "wrench", "shuffle", "images", "alert"],
            playerAction: true
        });
        expect(service.shortGameDescription(game, 'md2')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "wrench", "shuffle", "images", "alert"],
            playerAction: false
        });

        game.gamePhase = 'Playing';
        expect(service.shortGameDescription(game, 'md1')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "wrench", "shuffle", "images", "alert"],
            playerAction: false
        });
        expect(service.shortGameDescription(game, 'md2')).to.deep.equal({
            sizeText: '10x10',
            actionsText: 'Single',
            icons: ["eye-disabled", "wrench", "shuffle", "images", "alert"],
            playerAction: true
        });
    });

    it('test short game description bad game', function () {
        expect(service.shortGameDescription()).to.equal('Game details missing!');
    });
});


