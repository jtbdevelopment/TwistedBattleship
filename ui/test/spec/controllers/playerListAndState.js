'use strict';

describe('Controller: playerListAndState', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var rootScope, scope, http, ctrl;
    var game;
    var gameBase = {features: []};
    var gameID = 'X1244dd';
    var shortGrid = '1x8';
    var player = {md5: 'md5ForMe'};
    var state = {
        params: {
            gameId: gameID
        }
    };
    var playerService = {
        currentPlayer: function () {
            return player;
        }
    };
    var gameCache = {
        getGameForID: function (id) {
            if (id === gameID) {
                return game;
            }
            return null;
        }
    };
    var gameDetails = {
        shortenGridSize: function (g) {
            if (g === game) {
                return shortGrid;
            }
            return '';
        }
    };
    beforeEach(inject(function ($rootScope, $httpBackend, $controller) {
        rootScope = $rootScope;
        http = $httpBackend;
        scope = rootScope.$new();
        game = {};
        angular.copy(gameBase, game);
    }));

    describe('for games in challenge phase', function () {
        beforeEach(inject(function ($controller) {
            game.gamePhase = 'Challenged';
            ctrl = $controller('PlayerListAndStateCtrl', {
                $scope: scope,
                tbsGameDetails: gameDetails,
                jtbGameCache: gameCache,
                jtbPlayerService: playerService,
                $state: state
            });
        }));

        it('test initializes for challenged', function () {
            expect(scope.showActions).to.equal(true);
            expect(scope.game).to.deep.equal(game);
            expect(scope.player).to.deep.equal(player);
            expect(scope.intel).to.equal('Shared');
            expect(scope.moves).to.equal('Per Ship');
            expect(scope.ecmEnabled).to.equal('close');
            expect(scope.spyingEnabled).to.equal('close');
            expect(scope.repairsEnabled).to.equal('close');
            expect(scope.moveEnabled).to.equal('close');
            expect(scope.criticalsEnabled).to.equal('close');
            expect(scope.gridSize).to.equal(shortGrid);
        });
    });

    describe('for games in challenge phase with options', function () {
        beforeEach(inject(function ($controller) {
            game.gamePhase = 'Challenged';
            game.features = ['ECMEnabled', 'SpyEnabled', 'EREnabled', 'EMEnabled', 'CriticalEnabled', 'IsolatedIntel', 'Single'];
            ctrl = $controller('PlayerListAndStateCtrl', {
                $scope: scope,
                tbsGameDetails: gameDetails,
                jtbGameCache: gameCache,
                jtbPlayerService: playerService,
                $state: state
            });
        }));

        it('test initializes for challenged', function () {
            expect(scope.showActions).to.equal(true);
            expect(scope.game).to.deep.equal(game);
            expect(scope.player).to.deep.equal(player);
            expect(scope.intel).to.equal('Isolated');
            expect(scope.moves).to.equal('1');
            expect(scope.ecmEnabled).to.equal('checkmark');
            expect(scope.spyingEnabled).to.equal('checkmark');
            expect(scope.repairsEnabled).to.equal('checkmark');
            expect(scope.moveEnabled).to.equal('checkmark');
            expect(scope.criticalsEnabled).to.equal('checkmark');
            expect(scope.gridSize).to.equal(shortGrid);
        });
    });
});

