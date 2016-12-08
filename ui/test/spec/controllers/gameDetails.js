'use strict';

describe('Controller: GameDetailsCtrl', function () {
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
    var $rootScope, $scope, ctrl, stateSpy, $q, phasePromise;

    beforeEach(inject(function (_$rootScope_, $controller, _$q_) {
        stateSpy = {go: sinon.spy(), params: {gameID: expectedId}};
        $rootScope = _$rootScope_;
        $q = _$q_;
        phasePromise = _$q_.defer();
        $scope = $rootScope.$new();

        var mockGamePhaseService = {
            phases: function () {
                return phasePromise.promise;
            }
        };

        ctrl = $controller('GameDetailsCtrl', {
            $scope: $scope,
            $state: stateSpy,
            jtbGameCache: mockGameCache,
            tbsGameDetails: mockGameDetails,
            jtbGamePhaseService: mockGamePhaseService
        });
    }));

    it('initializes', function () {
        expect(ctrl.gameID).to.equal(expectedId);
        expect(ctrl.game).to.equal(expectedGame);
        expect(ctrl.gameDetails).to.equal(mockGameDetails);
    });

    it('on ionic view enter, sets rest of details, with some features enabled', function () {
        expectedGame.features = ['ECMEnabled', 'SpyEnabled', 'EMEnabled', 'IsolatedIntel', 'Single'];
        $rootScope.$broadcast('$ionicView.enter');
        expect(ctrl.game).to.equal(expectedGame);
        expect(ctrl.ecmEnabled).to.equal('checkmark');
        expect(ctrl.spyingEnabled).to.equal('checkmark');
        expect(ctrl.moveEnabled).to.equal('checkmark');
        expect(ctrl.cruiseMissileEnabled).to.equal('close');
        expect(ctrl.repairsEnabled).to.equal('close');
        expect(ctrl.gridSize).to.equal(expectedGameSize);
        expect(ctrl.intel).to.equal('Isolated');
        expect(ctrl.moves).to.equal('1');
        var serverPhases = {
            a: ['b', 'x'],
            c: ['d', 'y']
        };
        serverPhases[expectedPhase] = ['d', expectedPhaseLabel];
        phasePromise.resolve(
            serverPhases);
        $rootScope.$apply();
        expect(ctrl.phase).to.equal(expectedPhaseLabel);
    });

    it('on ionic view enter, sets rest of details, with other features enabled', function () {
        expectedGame.features = ['CruiseMissileEnabled', 'EREnabled'];
        $rootScope.$broadcast('$ionicView.enter');
        expect(ctrl.game).to.equal(expectedGame);
        expect(ctrl.ecmEnabled).to.equal('close');
        expect(ctrl.spyingEnabled).to.equal('close');
        expect(ctrl.moveEnabled).to.equal('close');
        expect(ctrl.cruiseMissileEnabled).to.equal('checkmark');
        expect(ctrl.repairsEnabled).to.equal('checkmark');
        expect(ctrl.gridSize).to.equal(expectedGameSize);
        expect(ctrl.intel).to.equal('Shared');
        expect(ctrl.moves).to.equal('Per Ship');
    });
});
