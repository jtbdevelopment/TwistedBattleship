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
            accept: sinon.spy()
        };
        $rootScope = _$rootScope_;
        scope = $rootScope.$new();
        ctrl = $controller('PlayerListAndStateCtrl', {
            $scope: scope,
            jtbPlayerService: mockPlayerService,
            $state: stateSpy,
            jtbIonicGameActions: mockGameActions,
            jtbGameCache: mockGameCache
        });
    }));

    it('initializes as expected', function () {
        expect(ctrl.gameID).to.equal(gameId);
        expect(ctrl.game).to.equal(expectedGame);
        expect(ctrl.player).to.equal(currentPlayer);
        expect(ctrl.showActions).to.be.false;
        expect(ctrl.actions).to.equal(mockGameActions);
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
        expect(ctrl.player).to.equal(currentPlayer);
        expect(ctrl.showActions).to.be.false;
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

    describe('with a game in challenged state', function () {
        beforeEach(inject(function ($controller) {
            expectedGame.gamePhase = 'Challenged';
            ctrl = $controller('PlayerListAndStateCtrl', {
                $scope: scope,
                jtbPlayerService: mockPlayerService,
                $state: stateSpy,
                jtbIonicGameActions: mockGameActions,
                jtbGameCache: mockGameCache
            });
        }));

        it('initializes as expected', function () {
            expect(ctrl.gameID).to.equal(gameId);
            expect(ctrl.game).to.equal(expectedGame);
            expect(ctrl.player).to.equal(currentPlayer);
            expect(ctrl.showActions).to.be.true;
        });
    })
});
