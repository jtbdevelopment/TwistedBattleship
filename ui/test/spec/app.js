'use strict';

describe('', function () {

    var moduleUnderTest;
    var dependencies = [];

    var hasModule = function (module) {
        return dependencies.indexOf(module) >= 0;
    };

    angular.module('ionic', []);
    angular.module('ngCordova', []);
    angular.module('angular-multi-select', []);
    angular.module('coreGamesIonicUi', []);

    beforeEach(function () {
        // Get module
        moduleUnderTest = angular.module('tbs');
        dependencies = moduleUnderTest.requires;
    });

    it('should load outside dependencies', function () {
        expect(hasModule('coreGamesIonicUi')).to.be.true;
        expect(hasModule('ngCordova')).to.be.true;
        expect(hasModule('ionic')).to.be.true;
        expect(hasModule('angular-multi-select')).to.be.true;
    });


    it('should load config module', function () {
        expect(hasModule('config')).to.be.true;
    });

    it('should load controllers module', function () {
        expect(hasModule('tbs.controllers')).to.be.true;
    });


});