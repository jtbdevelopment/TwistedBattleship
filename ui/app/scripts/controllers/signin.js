'use strict';

angular.module('tbs.controllers')
    //  TODO - finish and move to core
    //  TODO - investigate cordova facebook plugin instead of custom
    //  TODO - move modals out of html?
    .controller('CoreMobileSignInCtrl',
    ['$scope', '$window', '$cookies', '$http', '$state', '$cacheFactory', '$ionicHistory', '$rootScope', 'jtbFacebook',
        function ($scope, $window, $cookies, $http, $state, $cacheFactory, $ionicHistory, $rootScope, jtbFacebook) {
            $scope.message = 'Initializing...';
            $scope.showFacebook = false;
            $scope.showManual = false;
            $scope.csrf = $cookies['XSRF-TOKEN'];
            $scope.facebookPermissions = '';

            $scope.username = '';
            $scope.password = '';
            $scope.rememberme = false;

            $scope.manualLogin = function () {
                clearHttpCache();
                $http({
                    transformRequest: function (obj) {
                        var str = [];
                        for (var p in obj) {
                            str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
                        }
                        return str.join('&');
                    },
                    url: '/signin/authenticate',
                    data: {
                        username: $scope.username,
                        password: $scope.password,
                        'remember-me': $scope.rememberme
                    },
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    method: 'POST'
                }).success(function () {
                    clearHttpCache();
                    $rootScope.$broadcast('refreshGames', '');
                    $ionicHistory.nextViewOptions({
                        disableBack: true
                    });
                    $state.go('app.games', {}, {reload: true});
                }).error(function () {
                    clearHttpCache();
                    $scope.message = 'Invalid username or password.';
                });
            };

            function showLoginOptions() {
                $scope.showFacebook = true;
                $scope.showManual =
                    $window.location.href.indexOf('localhost') > -1 ||
                    $window.location.href.indexOf('-dev') > -1;
                $scope.message = '';
            }

            function autoLogin() {
                $scope.showFacebook = false;
                $scope.showManual = false;
                $scope.message = 'Logging in via Facebook';
                clearHttpCache();
                //  TODO - make $http instead?
                //  TODO - forsce a refresh games in here
                $window.location = '/auth/facebook';
            }

            jtbFacebook.canAutoSignIn().then(function (details) {
                clearHttpCache();
                $scope.facebookPermissions = details.permissions;
                if (!details.auto) {
                    showLoginOptions();
                } else {
                    autoLogin();
                }
            }, function () {
                showLoginOptions();
            });

            function clearHttpCache() {
                $cacheFactory.get('$http').removeAll();
            }
        }
    ]
);
