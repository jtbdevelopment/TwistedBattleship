'use strict';

describe('Controller: GameCtrl', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    ['tbsActions'];

    var shipInfo = {};  //  TODO
    var expectedId = 'tada!';
    var expectedPhase = 'APhase';
    var currentPlayer = {source: 'MANUAL', md5: 'my md 5', theme: 'theme!'};
    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };
    var expectedGame = {
        id: expectedId,
        features: [],
        gamePhase: expectedPhase,
        players: {
            md1: {},
            md2: {},
            md3: {}
        }
    };
    var mockGameCache = {
        getGameForID: function (id) {
            expect(id).to.equal(expectedId);
            return expectedGame;
        }
    };

    var mockGameDetails = {
        x: function () {
        }
    };

    var mockSelectedCell, mockSelectedShip;
    var mockShipService = {
        selectedCell: function () {
            return mockSelectedCell;
        },
        selectedShip: function () {
            return mockSelectedShip;
        }
    };
    var rootScope, scope, ctrl, stateSpy, q, phasePromise, ionicLoadingSpy, ionicPopupSpy, timeout, ads, actionsSpy;

    beforeEach(inject(function ($rootScope, $controller, $q, $timeout) {
        stateSpy = {go: sinon.spy(), params: {gameID: expectedId}};
        ionicLoadingSpy = {show: sinon.spy(), hide: sinon.spy()};
        ionicPopupSpy = {alert: sinon.spy()};
        ads = {showInterstitial: sinon.spy()};
        rootScope = $rootScope;
        q = $q;
        timeout = $timeout;
        phasePromise = $q.defer();
        scope = rootScope.$new();
        mockShipService.placeShips = sinon.spy();
        mockShipService.placeCellMarkers = sinon.spy();
        actionsSpy = {
            repair: sinon.spy(),
            missile: sinon.spy(),
            ecm: sinon.spy(),
            spy: sinon.spy(),
            fire: sinon.spy(),
            declineRematch: sinon.spy(),
            rematch: sinon.spy(),
            move: sinon.spy(),
            quit: sinon.spy(),
            updateCurrentView: sinon.spy()
        };

        ctrl = $controller('GameCtrl', {
            $scope: scope,
            $state: stateSpy,
            tbsActions: actionsSpy,
            $rootScope: rootScope,
            jtbGameCache: mockGameCache,
            tbsGameDetails: mockGameDetails,
            $ionicLoading: ionicLoadingSpy,
            $ionicPopup: ionicPopupSpy,
            jtbPlayerService: mockPlayerService,
            $timeout: timeout,
            tbsAds: ads,
            shipInfo: shipInfo,
            tbsShipGrid: mockShipService
        });
    }));

    it('initializes', function () {
        expect(scope.gameID).to.equal(expectedId);
        expect(scope.game).to.equal(expectedGame);
        expect(scope.gameDetails).to.equal(mockGameDetails);
        expect(scope.playerKeys).to.deep.equal(['md1', 'md2', 'md3']);
        expect(scope.player).to.equal(currentPlayer);
        expect(scope.showing).to.equal('ALL');
        expect(scope.showingSelf).to.be.false;
        expect(scope.shipHighlighted).to.be.false;
    });

});
