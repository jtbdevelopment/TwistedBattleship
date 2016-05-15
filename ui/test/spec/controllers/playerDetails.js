'use strict';

describe('Controller: playerDetails', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var manualPlayer = {source: 'MANUAL'};
    var currentPlayer = manualPlayer;


    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };

    var rootScope, scope, ctrl;
    beforeEach(inject(function ($rootScope, $controller) {
        rootScope = $rootScope;
        scope = rootScope.$new();
        ctrl = $controller('PlayerDetailsCtrl', {
            $scope: scope,
            jtbPlayerService: mockPlayerService
        });
    }));

    it('on intialization, scope.player is undefined', function () {
        expect(scope.player).to.equal(undefined);
    });

    it('on ionic enter view, set player', function () {
        expect(scope.player).to.equal(undefined);
        rootScope.$broadcast('$ionicView.enter');
        expect(scope.player).to.equal(manualPlayer);
    });
});
