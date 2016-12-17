'use strict';

describe('Background Events', function () {

    describe('module definition tests', function () {

        var moduleUnderTest;
        var dependencies = [];

        var hasModule = function (module) {
            return dependencies.indexOf(module) >= 0;
        };

        beforeEach(function () {
            moduleUnderTest = angular.module('tbsBackground');
            dependencies = moduleUnderTest.requires;
        });

        it('should load outside dependencies', function () {
            expect(hasModule('ionic')).to.be.true;
        });
    });

    describe('functional tests', function () {

        angular.module('ionic', []);
        beforeEach(module('tbsBackground'));

        var $state;
        beforeEach(module(function ($provide) {
            $state = {go: sinon.spy()};
            $provide.factory('$state', [function () {
                return $state;
            }]);
        }));

        var $rootScope;
        beforeEach(inject(function (_$rootScope_) {
            $rootScope = _$rootScope_;
        }));

        it('changes phases when game phase updated is current game', function () {
            var id = 'agameid';
            $state.params = {gameID: id};
            $rootScope.$broadcast('gameUpdated', {id: id, gamePhase: 'P21'}, {id: id, gamePhase: 'NewPhase'});
            $rootScope.$apply();
            assert($state.go.calledWithMatch('app.newphase', {gameID: id}));
        });

        it('ignores game non phase changing game updated is current game', function () {
            var id = 'agameid';
            $state.params = {gameID: id};
            $rootScope.$broadcast('gameUpdated', {id: id, gamePhase: 'P21'}, {id: id, gamePhase: 'P21'});
            $rootScope.$apply();
            expect($state.go.callCount).to.equal(0);
        });

        it('ignores other game  updated is not current game', function () {
            var id = 'agameid';
            $state.params = {gameID: id};
            $rootScope.$broadcast('gameUpdated', {id: id + 'X', gamePhase: 'P21'}, {id: id + 'X', gamePhase: 'P21'});
            $rootScope.$apply();
            expect($state.go.callCount).to.equal(0);
        });
    });
});
