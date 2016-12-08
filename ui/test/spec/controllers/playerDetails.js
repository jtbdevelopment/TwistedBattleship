'use strict';

describe('Controller: playerDetails', function () {
    beforeEach(module('tbs.controllers'));

    var manualPlayer = {source: 'MANUAL', gameSpecificPlayerAttributes: {theme: 'theTheme'}};
    var currentPlayer = manualPlayer;


    var expectedURL = '$http://xyz/123';
    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        },
        currentPlayerBaseURL: function () {
            return expectedURL;
        }
    };

    var $rootScope, $scope, ctrl, $http;
    beforeEach(inject(function (_$rootScope_, $controller, $httpBackend) {
        $rootScope = _$rootScope_;
        $http = $httpBackend;
        $scope = $rootScope.$new();
        ctrl = $controller('PlayerDetailsCtrl', {
            $scope: $scope,
            jtbPlayerService: mockPlayerService
        });
    }));

    it('on init, ctrl.player is undefined', function () {
        expect(ctrl.player).to.equal(undefined);
        expect(ctrl.data.theme).to.equal('default-theme');
    });

    it('on ionic enter view, set player', function () {
        expect(ctrl.player).to.equal(undefined);
        $rootScope.$broadcast('$ionicView.enter');
        expect(ctrl.player).to.equal(manualPlayer);
        expect(ctrl.data.theme).to.equal(manualPlayer.gameSpecificPlayerAttributes.theme);
    });

    it('on changing theme successfully, broadcast update', function () {
        ctrl.data.theme = 'new-theme';
        var newPlayer = {id: 'newid'};
        var oldBC = $rootScope.$broadcast;
        $rootScope.$broadcast = sinon.spy();
        $http.expectPUT(expectedURL + '/changeTheme/' + ctrl.data.theme).respond(200, newPlayer);
        ctrl.changeTheme();
        $http.flush();
        assert($rootScope.$broadcast.calledWithMatch('playerUpdate', newPlayer.id, newPlayer));
        $rootScope.$broadcast = oldBC;
    });

});
