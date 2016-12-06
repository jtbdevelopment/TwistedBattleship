'use strict';

angular.module('tbs.controllers').controller('CoreIonicAdminCtrl',
    [
        function () {
            var controller = this;
            controller.switchToStats = function () {
                controller.showStats = true;
                controller.showSwitch = false;
            };
            controller.switchToSwitchPlayer = function () {
                controller.showStats = false;
                controller.showSwitch = true;
            };

            controller.switchToStats();
        }
    ]
);
