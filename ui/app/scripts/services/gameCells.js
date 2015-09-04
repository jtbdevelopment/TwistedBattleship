'use strict';

angular.module('tbs.services').factory('tbsCellStates', ['$http', function ($http) {
    return {
        cellStates: function () {
            return $http.get('/api/states', {cache: true}).then(function (response) {
                return response.data;
            });
        }
    };
}]);

