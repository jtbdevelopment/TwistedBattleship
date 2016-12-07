'use strict';

describe('Controller: CoreIonicAdminCtrl', function () {
    beforeEach(module('tbs.controllers'));

    var rootScope, ctrl;

    beforeEach(inject(function ($rootScope, $controller) {
        rootScope = $rootScope;

        ctrl = $controller('CoreIonicAdminCtrl', {});
    }));

    it('initializes', function () {
        expect(ctrl.showStats).to.be.true;
        expect(ctrl.showSwitch).to.be.false;
    });

    it('switching to switch player', function () {
        ctrl.switchToSwitchPlayer();
        expect(ctrl.showStats).to.be.false;
        expect(ctrl.showSwitch).to.be.true;
    });

    it('switching to stats', function () {
        ctrl.switchToSwitchPlayer();
        ctrl.switchToStats();
        expect(ctrl.showStats).to.be.true;
        expect(ctrl.showSwitch).to.be.false;
    });
});
