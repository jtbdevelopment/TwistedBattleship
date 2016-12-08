'use strict';

angular.module('tbs.controllers').controller('PlayerDetailsCtrl',
    ['$scope', 'jtbPlayerService', '$http', '$rootScope',
        function ($scope, jtbPlayerService, $http, $rootScope) {
            var controller = this;
            controller.data = {theme: 'default-theme'};

            controller.changeTheme = function () {
                $http.put(jtbPlayerService.currentPlayerBaseURL() + '/changeTheme/' + controller.data.theme)
                    .success(function (data) {
                        console.log(JSON.stringify(data));
                        $rootScope.$broadcast('playerUpdate', data.id, data);
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