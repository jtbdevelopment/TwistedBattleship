'use strict';

describe('Controller: playHelp', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var expectedTheme = 'A THEME!';
    var currentPlayer = {source: 'MANUAL', gameSpecificPlayerAttributes: {theme: expectedTheme}};

    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };

    var rootScope, scope, ctrl;
    beforeEach(inject(function ($rootScope, $controller) {
        rootScope = $rootScope;
        scope = rootScope.$new();
        ctrl = $controller('PlayHelpCtrl', {
            $scope: scope,
            jtbPlayerService: mockPlayerService
        });
    }));

    describe('switching tabs', function () {

        it('initializes theme and defaults to general help', function () {
            expect(scope.theme).to.equal(expectedTheme);
            expect(scope.showGeneral).to.be.true;
            expect(scope.showAttackDefend).to.be.false;
            expect(scope.showGrid).to.be.false;
        });

        it('switching back to general', function () {
            scope.switchToAttackDefend();
            scope.switchToGeneral();
            expect(scope.theme).to.equal(expectedTheme);
            expect(scope.showGeneral).to.be.true;
            expect(scope.showAttackDefend).to.be.false;
            expect(scope.showGrid).to.be.false;
        });

        it('switching to attack defend', function () {
            scope.switchToAttackDefend();
            expect(scope.theme).to.equal(expectedTheme);
            expect(scope.showGeneral).to.be.false;
            expect(scope.showAttackDefend).to.be.true;
            expect(scope.showGrid).to.be.false;
        });

        it('switching to grid', function () {
            scope.switchToGrid();
            expect(scope.theme).to.equal(expectedTheme);
            expect(scope.showGeneral).to.be.false;
            expect(scope.showAttackDefend).to.be.false;
            expect(scope.showGrid).to.be.true;
        });

    });
});