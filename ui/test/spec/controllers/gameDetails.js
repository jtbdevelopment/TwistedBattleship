'use strict';

describe('Controller: GameDetailsCtrl', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var expectedId = 'tada!';
    var expectedPhase = 'APhase';
    var expectedPhaseLabel = 'A Phase Label!';
    var expectedGame = {
        id: expectedId,
        features: [],
        gamePhase: expectedPhase
    };
    var mockGameCache = {
        initialized: function () {
            return expectedInitialized;
        },
        getGameForID: function (id) {
            expect(id).to.equal(expectedId);
            return expectedGame;
        }
    };

    var expectedGameSize = 'A Size!';
    var mockGameDetails = {
        shortenGridSize: function (game) {
            expect(game).to.equal(expectedGame);
            return expectedGameSize;
        }
    };
    var rootScope, scope, ctrl, stateSpy, q, phasePromise;

    beforeEach(inject(function ($rootScope, $controller, $q) {
        stateSpy = {go: sinon.spy(), params: {gameID: expectedId}};
        rootScope = $rootScope;
        q = $q;
        phasePromise = $q.defer();
        scope = rootScope.$new();

        var mockGamePhaseService = {
            phases: function () {
                return phasePromise.promise;
            }
        };

        ctrl = $controller('GameDetailsCtrl', {
            $scope: scope,
            $state: stateSpy,
            $rootScope: rootScope,
            jtbGameCache: mockGameCache,
            tbsGameDetails: mockGameDetails,
            jtbGamePhaseService: mockGamePhaseService
        });
    }));

    it('initializes', function () {
        expect(scope.gameID).to.equal(expectedId);
        expect(scope.game).to.equal(expectedGame);
        expect(scope.gameDetails).to.equal(mockGameDetails);
    });

    it('on ionic view enter, sets rest of details, with some features enabled', function () {
        expectedGame.features = ['ECMEnabled', 'SpyEnabled', 'EMEnabled', 'IsolatedIntel', 'Single'];
        rootScope.$broadcast('$ionicView.enter');
        expect(scope.game).to.equal(expectedGame);
        expect(scope.ecmEnabled).to.equal('checkmark');
        expect(scope.spyingEnabled).to.equal('checkmark');
        expect(scope.moveEnabled).to.equal('checkmark');
        expect(scope.cruiseMissileEnabled).to.equal('close');
        expect(scope.repairsEnabled).to.equal('close');
        expect(scope.gridSize).to.equal(expectedGameSize);
        expect(scope.intel).to.equal('Isolated');
        expect(scope.moves).to.equal('1');
        var serverPhases = {
            a: ['b', 'x'],
            c: ['d', 'y'],
        };
        serverPhases[expectedPhase] = ['d', expectedPhaseLabel];
        phasePromise.resolve(
            serverPhases);
        rootScope.$apply();
        expect(scope.phase).to.equal(expectedPhaseLabel);
    });

    it('on ionic view enter, sets rest of details, with other features enabled', function () {
        expectedGame.features = ['CruiseMissileEnabled', 'EREnabled'];
        rootScope.$broadcast('$ionicView.enter');
        expect(scope.game).to.equal(expectedGame);
        expect(scope.ecmEnabled).to.equal('close');
        expect(scope.spyingEnabled).to.equal('close');
        expect(scope.moveEnabled).to.equal('close');
        expect(scope.cruiseMissileEnabled).to.equal('checkmark');
        expect(scope.repairsEnabled).to.equal('checkmark');
        expect(scope.gridSize).to.equal(expectedGameSize);
        expect(scope.intel).to.equal('Shared');
        expect(scope.moves).to.equal('Per Ship');
    });
});
