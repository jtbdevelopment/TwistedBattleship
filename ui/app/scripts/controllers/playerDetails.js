'use strict';

angular.module('tbs.controllers').controller('PlayerDetailsCtrl',
    ['$scope', 'jtbPlayerService', '$http', '$rootScope',
        function ($scope, jtbPlayerService, $http, $rootScope) {
            var controller = this;
            controller.data = {theme: 'default-theme'};

            controller.changeTheme = function () {
                $http.put(jtbPlayerService.currentPlayerBaseURL() + '/changeTheme/' + controller.data.theme)
                    .then(function (response) {
                        $rootScope.$broadcast('playerUpdate', response.data.id, response.data);
                    });
            };

            $scope.$on('$ionicView.enter', function () {
                controller.player = jtbPlayerService.currentPlayer();
                console.log(JSON.stringify(controller.player));
                controller.data.theme = controller.player.gameSpecificPlayerAttributes.theme;
            });
        }
    ]
);