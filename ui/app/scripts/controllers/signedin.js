'use strict';

angular.module('tbs.controllers')
    //  TODO - finish and move to core?
    .controller('CoreMobileSignedInCtrl',
    ['$scope', '$state', '$ionicHistory', '$rootScope', '$cacheFactory',
        function ($scope, $state, $ionicHistory, $rootScope, $cacheFactory) {

            function clearHttpCache() {
                $cacheFactory.get('$http').removeAll();
            }

            function onSuccessfulLogin() {
                console.log('Logged in');
                clearHttpCache();
                $rootScope.$broadcast('login');
                $ionicHistory.nextViewOptions({
                    disableBack: true
                });
                $state.go('app.games');
            }

            $scope.$on('$ionicView.enter', function () {
                onSuccessfulLogin();
            });

        }
    ]
);