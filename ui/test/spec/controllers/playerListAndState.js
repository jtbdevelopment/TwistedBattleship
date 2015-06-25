'use strict';

describe('Controller: playerListAndState', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var rootScope, scope, http, ctrl;
    var game;
    var gameBase = {features: [], playerStates: {md1: 'Pending', md2: 'Accepted', md3: 'Declined', md4: 'Quit'}};
    var gameID = 'X1244dd';
    var shortGrid = '1x8';
    var player = {md5: 'md5ForMe'};
    var playerURL = 'http://someurl';
    var state = {
        goTrack: {},
        params: {
            gameID: gameID
        },
        go: function (dest, params) {
            this.goTrack.dest = dest;
            this.goTrack.params = params;
        }
    };
    var playerService = {
        currentPlayer: function () {
            return player;
        },
        currentPlayerBaseURL: function () {
            return playerURL;
        }
    };
    var gameCache = {
        updatedGame: {},
        getGameForID: function (id) {
            if (id === gameID) {
                return game;
            }
            return null;
        },
        putUpdatedGame: function (update) {
            this.updatedGame = update;
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
    beforeEach(inject(function ($rootScope, $httpBackend) {
        rootScope = $rootScope;
        http = $httpBackend;
        scope = rootScope.$new();
        game = {};
        angular.copy(gameBase, game);
        state.goTrack = {};
        gameCache.updatedGame = {};
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
            expect(scope.gameID).to.equal(gameID);
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

        it('test status colors', function () {
            expect(scope.statusColor()).to.equal('');
            expect(scope.statusColor('md1')).to.equal('energized');
            expect(scope.statusColor('md2')).to.equal('balanced');
            expect(scope.statusColor('md3')).to.equal('assertive');
            expect(scope.statusColor('md4')).to.equal('assertive');
            expect(scope.statusColor('md5')).to.equal('assertive');
        });

        it('test status icons', function () {
            expect(scope.statusIcon()).to.equal('');
            expect(scope.statusIcon('md1')).to.equal('help-circled');
            expect(scope.statusIcon('md2')).to.equal('checkmark-circled');
            expect(scope.statusIcon('md3')).to.equal('close-circled');
            expect(scope.statusIcon('md4')).to.equal('flag');
            expect(scope.statusIcon('md5')).to.equal('close-circled');
        });

        it('accept game, but it stays challenged', function () {
            var newGame = {};
            angular.copy(game, newGame);
            http.expectPUT(playerURL + '/game/' + gameID + '/accept').respond(newGame);
            scope.accept();
            http.flush();
            expect(gameCache.updatedGame).to.deep.equal(newGame);
            expect(state.goTrack).to.deep.equal({dest: 'app.games', params: undefined});
        });

        it('accept game, and it goes to setup phase', function () {
            var newGame = {};
            angular.copy(game, newGame);
            newGame.gamePhase = 'Setup';
            http.expectPUT(playerURL + '/game/' + gameID + '/accept').respond(newGame);
            scope.accept();
            http.flush();
            expect(gameCache.updatedGame).to.deep.equal(newGame);
            expect(state.goTrack).to.deep.equal({dest: 'app.setup', params: {gameID: gameID}});
        });

        it('reject game, and it goes to declined phase', function () {
            var newGame = {};
            angular.copy(game, newGame);
            newGame.gamePhase = 'Declined';
            http.expectPUT(playerURL + '/game/' + gameID + '/reject').respond(newGame);
            scope.reject();
            http.flush();
            expect(gameCache.updatedGame).to.deep.equal(newGame);
            expect(state.goTrack).to.deep.equal({dest: 'app.declined', params: {gameID: gameID}});
        });

        it('accept game, but it errors', function () {
            var newGame = {};
            angular.copy(game, newGame);
            http.expectPUT(playerURL + '/game/' + gameID + '/accept').respond(404, 'error message');
            scope.accept();
            http.flush();
            expect(gameCache.updatedGame).to.deep.equal({});
            expect(state.goTrack).to.deep.equal({});
        });

        it('reject game, but it errors', function () {
            var newGame = {};
            angular.copy(game, newGame);
            http.expectPUT(playerURL + '/game/' + gameID + '/reject').respond(404, 'error message');
            scope.reject();
            http.flush();
            expect(gameCache.updatedGame).to.deep.equal({});
            expect(state.goTrack).to.deep.equal({});
        });
    });

    angular.forEach(['Declined', 'Quit'], function (phase) {
        describe('for games in ' + phase + ' phase with options', function () {
            beforeEach(inject(function ($controller) {
                game.gamePhase = phase;
                ctrl = $controller('PlayerListAndStateCtrl', {
                    $scope: scope,
                    tbsGameDetails: gameDetails,
                    jtbGameCache: gameCache,
                    jtbPlayerService: playerService,
                    $state: state
                });
            }));

            it('test initializes for challenged', function () {
                expect(scope.showActions).to.equal(false);
            });
        });
    });
});

