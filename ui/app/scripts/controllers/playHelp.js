'use strict';

angular.module('tbs.controllers').controller('PlayHelpCtrl',
    ['jtbPlayerService',
        function (jtbPlayerService) {
            var controller = this;
            controller.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;

            controller.switchToGeneral = function () {
                controller.showGeneral = true;
                controller.showAttackDefend = false;
                controller.showGrid = false;
            };

            controller.switchToGrid = function () {
                controller.showGeneral = false;
                controller.showAttackDefend = false;
                controller.showGrid = true;
            };

            controller.switchToAttackDefend = function () {
                controller.showGeneral = false;
                controller.showAttackDefend = true;
                controller.showGrid = false;
            };

            controller.switchToGeneral();
        }
    ]
);