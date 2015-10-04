'use strict';

angular.module('tbs.controllers')
    //  TODO - finish and move to core?
    .controller('CoreIonicSignedInCtrl',
    ['$scope', '$state', '$rootScope', '$cacheFactory',
        function ($scope, $state, $rootScope, $cacheFactory) {

            function clearHttpCache() {
                $cacheFactory.get('$http').removeAll();
            }

            function onSuccessfulLogin() {
                console.log('Logged in');
                clearHttpCache();
                $rootScope.$broadcast('login');
                $state.go('app.games');
            }

            $scope.$on('$ionicView.enter', function () {
                onSuccessfulLogin();
            });

        }
    ]
);