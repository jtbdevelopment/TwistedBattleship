'use strict';

describe('Controller: SetupGameV2Ctrl', function () {
    beforeEach(module('tbs.controllers'));

    var expectedId = 'tada!';
    var expectedPhase = 'APhase';
    var selectedOpponent = 'md3';
    var mockGameCache = {
        getGameForID: function (id) {
            expect(id).to.equal(expectedId);
            return expectedGame;
        }
    };

    var shipsOnGrid;
    var mockShipService = {
        overlapCB: undefined,
        currentShipsOnGrid: function () {
            return shipsOnGrid;
        },
        initialize: function (game, ships, markers, cb) {
            expect(game).to.equal(expectedGame);
            expect(ships).to.equal(game.maskedPlayersState.shipStates);
            angular.forEach(ships, function (ship, index) {
                expect(ship).to.equal(game.maskedPlayersState.shipStates[index]);
            });
            expect([]).to.deep.equal(markers);
            cb();
        },
        enableShipMovement: function (cb) {
            expect(cb).to.be.defined;
            this.overlapCB = cb;
        }
    };
    var $rootScope, scope, ctrl, stateSpy, $q, phasePromise, ionicLoadingSpy, $timeout, ionicModal,
        actionsSpy, expectedGame, ionicSideMenuDelegate, jtbIonicGameActions;
    var modalHelpPromise, modalHelp, $http;

    beforeEach(inject(function ($httpBackend, _$rootScope_, $controller, _$q_, _$timeout_) {
        $rootScope = _$rootScope_;
        $http = $httpBackend;
        $q = _$q_;
        $timeout = _$timeout_;
        scope = $rootScope.$new();
        modalHelpPromise = $q.defer();
        modalHelp = {show: sinon.spy(), hide: sinon.spy(), remove: sinon.spy()};
        ionicModal = {fromTemplateUrl: sinon.stub()};
        ionicSideMenuDelegate = {canDragContent: sinon.spy()};
        shipsOnGrid = [];
        jtbIonicGameActions = {quit: sinon.spy(), wrapActionOnGame: sinon.stub(), getGameURL: sinon.stub()};
        expectedGame = {
            id: expectedId,
            features: [],
            gamePhase: expectedPhase,
            players: {
                md3: {},
                md1: {},
                md2: {}
            },
            maskedPlayersState: {
                shipStates: [
                    {
                        ship: 'Type1',
                        healthRemaining: 3,
                        shipGridCells: [{row: 0, column: 0}, {row: 0, column: 1}, {row: 0, column: 2}],
                        shipSegmentHit: [false, false, false]
                    },
                    {
                        ship: 'Type2',
                        healthRemaining: 2,
                        shipGridCells: [{row: 5, column: 5}, {row: 6, column: 5}],
                        shipSegmentHit: [false, false]
                    }
                ],
                consolidatedOpponentView: {
                    table: {a: 'X', b: 2}
                },
                opponentGrids: {
                    md3: {
                        table: {x: 1, y: 2, s: 'X'}
                    }
                },
                opponentViews: {
                    md3: {
                        table: {x: 5, y: 1, s: 'XXX'}
                    }
                }
            }
        };

        expectedGame.maskedPlayersState.opponentGrids[selectedOpponent] = {table: {x: 1, y: 2, s: 'X'}};
        stateSpy = {go: sinon.spy(), params: {gameID: expectedId}};
        ionicLoadingSpy = {show: sinon.spy(), hide: sinon.spy()};
        phasePromise = _$q_.defer();
        mockShipService.placeShips = sinon.spy();
        mockShipService.placeCellMarkers = sinon.spy();
        mockShipService.stop = sinon.spy();
        actionsSpy = {
            quit: sinon.spy(),
            setup: sinon.spy(),
            submit: sinon.spy(),
            updateCurrentView: sinon.spy()
        };

        ionicModal.fromTemplateUrl.withArgs(
            sinon.match('templates/help/help-setup.html'),
            sinon.match(function (val) {
                return scope == val.scope && val.animation === 'slide-in-up'
            })
        ).returns(modalHelpPromise.promise);

        ctrl = $controller('SetupGameV2Ctrl', {
            $scope: scope,
            $state: stateSpy,
            jtbIonicGameActions: jtbIonicGameActions,
            tbsActions: actionsSpy,
            jtbGameCache: mockGameCache,
            $timeout: $timeout,
            $ionicLoading: ionicLoadingSpy,
            $ionicModal: ionicModal,
            $ionicSideMenuDelegate: ionicSideMenuDelegate,
            tbsShipGridV2: mockShipService
        });
    }));

    it('initializes', function () {
        expect(ctrl.gameID).to.equal(expectedId);
        expect(ctrl.game).to.equal(expectedGame);
        assert(ionicSideMenuDelegate.canDragContent.calledWithMatch(false));
        expect(ctrl.movingShip).to.equal(null);
        expect(ctrl.submitDisabled).to.equal(true);
        expect(ctrl.movingPointerRelativeToShip).to.deep.equal({x: 0, y: 0});
        expect(ctrl.helpModal).to.be.undefined;
        modalHelpPromise.resolve(modalHelp);
        $rootScope.$apply();
        expect(ctrl.helpModal).to.equal(modalHelp);
        expect(ctrl.actions).to.equal(jtbIonicGameActions);
    });

    describe('tests involving help', function () {
        beforeEach(function () {
            modalHelpPromise.resolve(modalHelp);
            $rootScope.$apply();
        });

        it('shows help', function () {
            ctrl.showHelp();
            expect(modalHelp.show.calledWithMatch());
        });

        it('close help', function () {
            ctrl.closeHelp();
            expect(modalHelp.hide.calledWithMatch());
        });

        it('removes help on $destroy', function () {
            $rootScope.$broadcast('$destroy');
            expect(modalHelp.remove.calledWithMatch());
        });
    });

    describe('other tests', function () {
        beforeEach(function () {
            modalHelpPromise.resolve(modalHelp);
        });

        it('on enter, enables ship movement and registers callback', function () {
            $rootScope.$broadcast('$ionicView.enter');
            $timeout.flush();
            expect(mockShipService.overlapCB).to.be.defined;
        });

        it('submit disabled driven by enable ship movement callback', function () {
            $rootScope.$broadcast('$ionicView.enter');
            $timeout.flush();
            mockShipService.overlapCB(false);
            $timeout.flush();
            expect(ctrl.submitDisabled).to.be.false;
            mockShipService.overlapCB(true);
            $timeout.flush();
            expect(ctrl.submitDisabled).to.be.true;
        });

        it('navigate to game details', function () {
            ctrl.showDetails();
            assert(stateSpy.go.calledWithMatch('app.gameDetails', {gameID: expectedId}));
        });

        it('shuts down ship grid on view exit', function () {
            $rootScope.$broadcast('$ionicView.leave');
            assert(mockShipService.stop.calledWithMatch());
        });

        it('submit sends in ships from grid', function () {
            shipsOnGrid.push({ship: 'ship1', shipGridCells: [{}, {field: 'value'}], other: 'value'});
            shipsOnGrid.push({ship: 'ship2', shipGridCells: [{a: 1}, {b: 'value'}], other: 'v'});
            shipsOnGrid.push({ship: 'ship3', shipGridCells: [{a: 2}, {b: 3}]});
            var expectedPayload = [];
            angular.forEach(shipsOnGrid, function (shipOnGrid) {
                expectedPayload.push({ship: shipOnGrid.ship, coordinates: shipOnGrid.shipGridCells});
            });
            var baseUrl = 'http:/www/woo/';
            var newGame = {id: '1445'};
            jtbIonicGameActions.getGameURL.withArgs(expectedGame).returns(baseUrl);
            jtbIonicGameActions.wrapActionOnGame.withArgs(sinon.match(sinon.match.any));
            $http.expectPUT(baseUrl + 'setup', expectedPayload).respond(newGame);
            ctrl.submitSetup();
            $http.flush();// flush verifies call
            expect(jtbIonicGameActions.wrapActionOnGame.callCount).to.equal(1);
        });
    });
});
