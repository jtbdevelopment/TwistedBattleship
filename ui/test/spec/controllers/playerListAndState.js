'use strict';

describe('Controller: playerListAndState', function () {
    // load the controller's module
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

    var rootScope, scope, ctrl;
    beforeEach(inject(function ($rootScope, $controller) {
        stateSpy = {go: sinon.spy(), params: {gameID: gameId}};
        mockGameActions = {
            reject: sinon.spy(),
            accept: sinon.spy(),
            updateCurrentView: sinon.spy()
        };
        rootScope = $rootScope;
        scope = rootScope.$new();
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
        expect(scope.gameID).to.equal(gameId);
        expect(scope.game).to.equal(expectedGame);
        expect(scope.gameDetails).to.equal(mockGameDetails);
        expect(scope.player).to.equal(currentPlayer);
        expect(scope.showActions).to.be.false;
    });

    it('calls action accept on accept', function () {
        expect(mockGameActions.accept.callCount).to.equal(0);
        scope.accept();
        expect(mockGameActions.accept.callCount).to.equal(1);
        assert(mockGameActions.accept.calledWithMatch(expectedGame));
    });

    it('calls action reject on reject', function () {
        expect(mockGameActions.reject.callCount).to.equal(0);
        scope.reject();
        expect(mockGameActions.reject.callCount).to.equal(1);
        assert(mockGameActions.reject.calledWithMatch(expectedGame));
    });

    it('test status color style', function () {
        expect(scope.statusColor('md1')).to.equal('pending');
        expect(scope.statusColor('md2')).to.equal('accepted');
        expect(scope.statusColor('md3')).to.equal('quit');
        expect(scope.statusColor('md4')).to.equal('other');
        expect(scope.statusColor()).to.equal('');
    });

    it('test status icon style', function () {
        expect(scope.statusIcon('md1')).to.equal('help-circled');
        expect(scope.statusIcon('md2')).to.equal('checkmark-circled');
        expect(scope.statusIcon('md3')).to.equal('flag');
        expect(scope.statusIcon('md4')).to.equal('close-circled');
        expect(scope.statusIcon()).to.equal('');
    });

    it('test show details goes to game details', function () {
        scope.showDetails();
        expect(stateSpy.go.callCount).to.equal(1);
        assert(stateSpy.go.calledWithMatch('app.gameDetails', {gameID: gameId}));
    });

    it('ignores update on different game', function () {
        expect(mockGameActions.updateCurrentView.callCount).to.equal(0);
        var newGame = {id: gameId + 'X'};
        rootScope.$broadcast('gameUpdated', newGame, newGame);
        expect(mockGameActions.updateCurrentView.callCount).to.equal(0);
    });

    it('processes update on same game', function () {
        expect(mockGameActions.updateCurrentView.callCount).to.equal(0);
        var newGame = {id: gameId};
        rootScope.$broadcast('gameUpdated', expectedGame, newGame);
        expect(mockGameActions.updateCurrentView.callCount).to.equal(1);
        assert(mockGameActions.updateCurrentView.calledWithMatch(expectedGame, newGame));
        expect(scope.game).to.equal(newGame);
    });

    describe('with a game in challenged state', function () {
        beforeEach(inject(function ($rootScope, $controller) {
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
            expect(scope.gameID).to.equal(gameId);
            expect(scope.game).to.equal(expectedGame);
            expect(scope.gameDetails).to.equal(mockGameDetails);
            expect(scope.player).to.equal(currentPlayer);
            expect(scope.showActions).to.be.true;
        });
    })
});
