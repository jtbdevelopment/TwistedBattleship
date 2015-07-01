'use strict';

angular.module('tbs.services').factory('tbsShips', ['$http', function ($http) {
    return {
        ships: function () {
            return $http.get('/api/ships', {cache: true}).then(function (response) {
                return response.data;
            });
        }
    };
}]);

