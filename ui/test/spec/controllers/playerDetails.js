'use strict';

describe('Controller: playerDetails', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var manualPlayer = {source: 'MANUAL', gameSpecificPlayerAttributes: {theme: 'theTheme'}};
    var currentPlayer = manualPlayer;


    var expectedURL = 'http://xyz/123';
    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        },
        currentPlayerBaseURL: function () {
            return expectedURL;
        }
    };

    var rootScope, scope, ctrl, timeout, http;
    var ionicPopup;
    beforeEach(inject(function ($rootScope, $controller, $timeout, $httpBackend) {
        rootScope = $rootScope;
        timeout = $timeout;
        ionicPopup = {alert: sinon.spy()};
        http = $httpBackend;
        scope = rootScope.$new();
        ctrl = $controller('PlayerDetailsCtrl', {
            $scope: scope,
            $ionicPopup: ionicPopup,
            jtbPlayerService: mockPlayerService
        });
    }));

    it('on intialization, scope.player is undefined', function () {
        expect(scope.player).to.equal(undefined);
        expect(scope.data.theme).to.equal('default-theme');
    });

    it('on ionic enter view, set player', function () {
        expect(scope.player).to.equal(undefined);
        rootScope.$broadcast('$ionicView.enter');
        expect(scope.player).to.equal(manualPlayer);
        expect(scope.data.theme).to.equal(manualPlayer.gameSpecificPlayerAttributes.theme);
    });

    it('on changing theme successfully, broadcast update', function () {
        scope.data.theme = 'new-theme';
        var newPlayer = {id: 'newid'};
        var oldBC = rootScope.$broadcast;
        rootScope.$broadcast = sinon.spy();
        http.expectPUT(expectedURL + '/changeTheme/' + scope.data.theme).respond(200, newPlayer);
        scope.changeTheme();
        http.flush();
        timeout.flush();
        assert(rootScope.$broadcast.calledWithMatch('playerUpdate', newPlayer.id, newPlayer));
        rootScope.$broadcast = oldBC;
    });

    it('on changing theme fails, show error', function () {
        scope.data.theme = 'new-theme';
        http.expectPUT(expectedURL + '/changeTheme/' + scope.data.theme).respond(400, 'something bad');
        scope.changeTheme();
        http.flush();
        assert(ionicPopup.alert.calledWithMatch({
            title: 'Error updating theme!',
            template: 'something bad'
        }));
    });
});
