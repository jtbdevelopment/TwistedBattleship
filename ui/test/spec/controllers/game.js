'use strict';

describe('Controller: GameCtrl', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    ['tbsActions'];

    var shipInfo = {};  //  TODO
    var expectedId = 'tada!';
    var expectedPhase = 'APhase';
    var currentPlayer = {source: 'MANUAL', md5: 'my md 5', theme: 'theme!'};
    var selectedOpponent = 'md5number2';
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
        mockShipService.stop = sinon.spy();
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

    it('shows action log', function () {
        scope.showActionLog();
        assert(stateSpy.go.calledWithMatch('app.actionLog', {gameID: expectedId}));
    });

    it('shows game details', function () {
        scope.showDetails();
        assert(stateSpy.go.calledWithMatch('app.gameDetails', {gameID: expectedId}));
    });

    it('shows help', function () {
        scope.showHelp();
        assert(stateSpy.go.calledWithMatch('app.playhelp'));
    });

    it('declines rematch', function () {
        scope.declineRematch();
        assert(actionsSpy.declineRematch.calledWithMatch(expectedGame));
    });

    it('start rematch', function () {
        scope.rematch();
        assert(actionsSpy.rematch.calledWithMatch(expectedGame));
    });


    it('quit game', function () {
        scope.quit();
        assert(actionsSpy.quit.calledWithMatch(expectedGame));
    });

    describe('tests with cell selected', function () {
        angular.forEach([true, false], function (self) {
            beforeEach(function () {
                mockSelectedCell = {x: 1, y: 2};
                if (self) {
                    scope.showing = selectedOpponent;
                }
            });
            it('ecm on self=' + self, function () {
                scope.showingSelf = self;
                scope.ecm();
                console.log(scope.showingSelf);
                assert(actionsSpy.ecm.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('repair on self=' + self, function () {
                scope.showingSelf = self;
                scope.repair();
                console.log(scope.showingSelf);
                assert(actionsSpy.repair.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('spy on self=' + self, function () {
                scope.showingSelf = self;
                scope.spy();
                console.log(scope.showingSelf);
                assert(actionsSpy.spy.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('missile on self=' + self, function () {
                scope.showingSelf = self;
                scope.missile();
                console.log(scope.showingSelf);
                assert(actionsSpy.missile.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('fire on self=' + self, function () {
                scope.showingSelf = self;
                scope.fire();
                console.log(scope.showingSelf);
                assert(actionsSpy.fire.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('move on self=' + self, function () {
                scope.showingSelf = self;
                scope.move();
                console.log(scope.showingSelf);
                assert(actionsSpy.move.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });
        });
    });

    it('shuts down ship grid on view exit', function () {
        rootScope.$broadcast('$ionicView.leave');
        assert(mockShipService.stop.calledWithMatch());
    });
});
