'use strict';

describe('Controller: playerListAndState', function () {
    beforeEach(module('tbs.controllers'));

    var stateSpy;

    var currentPlayer = {source: 'MANUAL', md5: 'mymd5'};
    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };
    var mockGameActions;
    var mockGameDetails = {
        something: function () {
        }
    };
    var gameId = 'AN ID!';
    var expectedGame = {
        id: gameId,
        gamePhase: 'Quit',
        playerStates: {md1: 'Pending', md2: 'Accepted', md3: 'Quit', md4: 'Other'}
    };
    var mockGameCache = {
        getGameForID: function (id) {
            expect(id).to.equal(gameId);
            return expectedGame;
        }
    };

    var $rootScope, scope, ctrl;
    beforeEach(inject(function (_$rootScope_, $controller) {
        stateSpy = {go: sinon.spy(), params: {gameID: gameId}};
        mockGameActions = {
            reject: sinon.spy(),
            accept: sinon.spy(),
            updateCurrentView: sinon.spy()
        };
        $rootScope = _$rootScope_;
        scope = $rootScope.$new();
        ctrl = $controller('PlayerListAndStateCtrl', {
            $scope: scope,
            jtbPlayerService: mockPlayerService,
            $state: stateSpy,
            tbsGameDetails: mockGameDetails,
            tbsActions: mockGameActions,
            jtbGameCache: mockGameCache
        });
    }));

    it('initializes as expected', function () {
        expect(ctrl.gameID).to.equal(gameId);
        expect(ctrl.game).to.equal(expectedGame);
        expect(ctrl.gameDetails).to.equal(mockGameDetails);
        expect(ctrl.player).to.equal(currentPlayer);
        expect(ctrl.showActions).to.be.false;
    });

    it('re-initializes as enter', function () {
        ctrl.gameID = undefined;
        ctrl.game = undefined;
        ctrl.gameDetails = undefined;
        ctrl.player = undefined;
        ctrl.showActions = undefined;
        scope.$broadcast('$ionicView.enter');
        scope.$apply();

        expect(ctrl.gameID).to.equal(gameId);
        expect(ctrl.game).to.equal(expectedGame);
        expect(ctrl.gameDetails).to.equal(mockGameDetails);
        expect(ctrl.player).to.equal(currentPlayer);
        expect(ctrl.showActions).to.be.false;
    });

    it('calls action accept on accept', function () {
        expect(mockGameActions.accept.callCount).to.equal(0);
        ctrl.accept();
        expect(mockGameActions.accept.callCount).to.equal(1);
        assert(mockGameActions.accept.calledWithMatch(expectedGame));
    });

    it('calls action reject on reject', function () {
        expect(mockGameActions.reject.callCount).to.equal(0);
        ctrl.reject();
        expect(mockGameActions.reject.callCount).to.equal(1);
        assert(mockGameActions.reject.calledWithMatch(expectedGame));
    });

    it('test status color style', function () {
        expect(ctrl.statusColor('md1')).to.equal('pending');
        expect(ctrl.statusColor('md2')).to.equal('accepted');
        expect(ctrl.statusColor('md3')).to.equal('quit');
        expect(ctrl.statusColor('md4')).to.equal('other');
        expect(ctrl.statusColor()).to.equal('');
    });

    it('test status icon style', function () {
        expect(ctrl.statusIcon('md1')).to.equal('help-circled');
        expect(ctrl.statusIcon('md2')).to.equal('checkmark-circled');
        expect(ctrl.statusIcon('md3')).to.equal('flag');
        expect(ctrl.statusIcon('md4')).to.equal('close-circled');
        expect(ctrl.statusIcon()).to.equal('');
    });

    it('test show details goes to game details', function () {
        ctrl.showDetails();
        expect(stateSpy.go.callCount).to.equal(1);
        assert(stateSpy.go.calledWithMatch('app.gameDetails', {gameID: gameId}));
    });

    it('ignores update on different game', function () {
        expect(mockGameActions.updateCurrentView.callCount).to.equal(0);
        var newGame = {id: gameId + 'X'};
        $rootScope.$broadcast('gameUpdated', newGame, newGame);
        expect(mockGameActions.updateCurrentView.callCount).to.equal(0);
    });

    it('processes update on same game', function () {
        expect(mockGameActions.updateCurrentView.callCount).to.equal(0);
        var newGame = {id: gameId};
        $rootScope.$broadcast('gameUpdated', expectedGame, newGame);
        expect(mockGameActions.updateCurrentView.callCount).to.equal(1);
        assert(mockGameActions.updateCurrentView.calledWithMatch(expectedGame, newGame));
        expect(ctrl.game).to.equal(newGame);
    });

    describe('with a game in challenged state', function () {
        beforeEach(inject(function ($controller) {
            expectedGame.gamePhase = 'Challenged';
            ctrl = $controller('PlayerListAndStateCtrl', {
                $scope: scope,
                jtbPlayerService: mockPlayerService,
                $state: stateSpy,
                tbsGameDetails: mockGameDetails,
                tbsActions: mockGameActions,
                jtbGameCache: mockGameCache
            });
        }));

        it('initializes as expected', function () {
            expect(ctrl.gameID).to.equal(gameId);
            expect(ctrl.game).to.equal(expectedGame);
            expect(ctrl.gameDetails).to.equal(mockGameDetails);
            expect(ctrl.player).to.equal(currentPlayer);
            expect(ctrl.showActions).to.be.true;
        });
    })
});
