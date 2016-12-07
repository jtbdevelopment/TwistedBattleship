'use strict';

describe('Controller: MobileGameListCtrl', function () {
    beforeEach(module('tbs.controllers'));

    var currentPlayer = {source: 'MANUAL', md5: 'my md 5'};
    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };

    var expectedClassifications = ['Class2', 'Class1', 'Class3', 'Class4'];
    var expectedIcons = {Class1: 'Icon1', Class2: 'IconX', Class3: 'hey', Class4: 'spiral'};
    var expectedGames = {
        Class1: [{id: '1'}, {id: '10'}],
        Class2: [],
        Class3: [{id: '3'}, {id: '7'}],
        Class4: [{id: '17'}, {id: '2'}]
    };
    var mockClassifier = {
        getIcons: function () {
            return expectedIcons;
        },
        getClassifications: function () {
            return expectedClassifications;
        }
    };

    var expectedInitialized = false;
    var mockGameCache = {
        initialized: function () {
            return expectedInitialized;
        },
        getGamesForPhase: function (phase) {
            return expectedGames[phase];
        }
    };

    var mockGameDetails = {
        afunction: function () {
        }
    };
    var rootScope, scope, ctrl, stateSpy;

    beforeEach(inject(function ($rootScope, $controller) {
        stateSpy = {go: sinon.spy()};
        rootScope = $rootScope;
        scope = rootScope.$new();
        ctrl = $controller('MobileGameListCtrl', {
            $scope: scope,
            $state: stateSpy,
            $rootScope: rootScope,
            jtbGameClassifier: mockClassifier,
            jtbGameCache: mockGameCache,
            jtbPlayerService: mockPlayerService,
            tbsGameDetails: mockGameDetails
        });
    }));

    it('initializes when cache not ready yet', function () {
        angular.forEach(expectedClassifications, function (classification) {
            expect(scope.games[classification].games).to.deep.equal([]);
            expect(scope.games[classification].icon).to.equal(expectedIcons[classification]);
            expect(scope.games[classification].hideGames).to.be.false;
            expect(scope.games[classification].label).to.equal(classification);
        });
        expect(scope.md5).to.equal('');
        expect(scope.phasesInOrder).to.deep.equal(expectedClassifications);
        expect(scope.gameDetails).to.equal(mockGameDetails);
    });

    it('after game caches loaded event', function () {
        rootScope.$broadcast('gameCachesLoaded');
        angular.forEach(expectedClassifications, function (classification) {
            expect(scope.games[classification].games).to.deep.equal(expectedGames[classification]);
        });
        expect(scope.md5).to.equal(currentPlayer.md5);
    });

    it('switch flag for hide games', function () {
        expect(scope.games[expectedClassifications[2]].hideGames).to.be.false;
        scope.switchHideGames(expectedClassifications[2]);
        expect(scope.games[expectedClassifications[2]].hideGames).to.be.true;
        scope.switchHideGames(expectedClassifications[2]);
        expect(scope.games[expectedClassifications[2]].hideGames).to.be.false;
    });

    it('new game action goes to setup', function () {
        scope.createNew();
        assert(stateSpy.go.calledWithMatch('app.create'));
    });

    describe('controller loaded after cache loaded', function () {
        beforeEach(inject(function ($rootScope, $controller) {
            expectedInitialized = true;
            scope = rootScope.$new();
            ctrl = $controller('MobileGameListCtrl', {
                $scope: scope,
                $state: stateSpy,
                $rootScope: rootScope,
                jtbGameClassifier: mockClassifier,
                jtbGameCache: mockGameCache,
                jtbPlayerService: mockPlayerService,
                tbsGameDetails: mockGameDetails
            });
        }));

        it('initializes games immediately', function () {
            angular.forEach(expectedClassifications, function (classification) {
                expect(scope.games[classification].games).to.deep.equal(expectedGames[classification]);
                expect(scope.games[classification].icon).to.equal(expectedIcons[classification]);
                expect(scope.games[classification].hideGames).to.be.false;
                expect(scope.games[classification].label).to.equal(classification);
            });
            expect(scope.md5).to.equal(currentPlayer.md5);
            expect(scope.phasesInOrder).to.deep.equal(expectedClassifications);
            expect(scope.gameDetails).to.equal(mockGameDetails);
        });
    });

});
