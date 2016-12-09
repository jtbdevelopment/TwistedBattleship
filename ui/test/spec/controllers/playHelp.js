'use strict';

describe('Controller: playHelp', function () {
    beforeEach(module('tbs.controllers'));

    var ctrl;
    beforeEach(inject(function ($controller) {
        ctrl = $controller('PlayHelpCtrl');
    }));

    describe('switching tabs', function () {

        it('initializes theme and defaults to general help', function () {
            expect(ctrl.showGeneral).to.be.true;
            expect(ctrl.showAttackDefend).to.be.false;
            expect(ctrl.showGrid).to.be.false;
        });

        it('switching back to general', function () {
            ctrl.switchToAttackDefend();
            ctrl.switchToGeneral();
            expect(ctrl.showGeneral).to.be.true;
            expect(ctrl.showAttackDefend).to.be.false;
            expect(ctrl.showGrid).to.be.false;
        });

        it('switching to attack defend', function () {
            ctrl.switchToAttackDefend();
            expect(ctrl.showGeneral).to.be.false;
            expect(ctrl.showAttackDefend).to.be.true;
            expect(ctrl.showGrid).to.be.false;
        });

        it('switching to grid', function () {
            ctrl.switchToGrid();
            expect(ctrl.showGeneral).to.be.false;
            expect(ctrl.showAttackDefend).to.be.false;
            expect(ctrl.showGrid).to.be.true;
        });

    });
});