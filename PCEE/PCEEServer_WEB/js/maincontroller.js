var serverIP;
var serverPort;

app
		.controller(
				'serverForm',
				function($scope, $http) {
					$scope.serverIP = '127.0.0.1';
					$scope.serverPort = '8080';

					$scope.launchServer = function() {
						(function getStatus() {
							$http
									.get(
											'http://' + $scope.serverIP + ':'
													+ $scope.serverPort
													+ '/ctrl/server/status')
									.success(function() {
										alert('Server is already started');
									})
									.error(
											function(data) {
//												alert('Starting the server'
//														+ data);
												runServer(false);
												$http
														.get(
																'http://'
																		+ $scope.serverIP
																		+ ':'
																		+ $scope.serverPort
																		+ '/ctrl/server/start')
														.success(
																function() {
//																	alert('The server is running');
																	serverIP = $scope.serverIP;
																	serverPort = $scope.serverPort;
																})
														.error(
																function() {
																	alert('Error launching the server');
																});
											});

						})();

					}

					$scope.stopServer = function() {
						(function getStatus() {
							$http
									.get(
											'http://' + $scope.serverIP + ':'
													+ $scope.serverPort
													+ '/ctrl/server/status')
									.success(
											function() {
												alert('Stoping the server');
												runServer(true);
												$http
														.get(
																'http://'
																		+ $scope.serverIP
																		+ ':'
																		+ $scope.serverPort
																		+ '/ctrl/server/stop')
														.success(
																function() {
																	alert('The server is stopped');
																})
														.error(
																function(data) {
																	alert('Error: It is not possible stop the server'
																			+ data);
																});
											}).error(function() {
										alert('The server is already stopped');
									});

						})();

					}

				});

window.oldLogs = [];

app
		.controller(
				'logCtrl',
				[
						'$scope',
						'$http',
						'$timeout',
						function logCtrl($scope, $http, $timeout) {
							$scope.log = [];

							(function getLogs() {
								if (isStarted) {
									$http
											.get(
													'http://' + serverIP
															+ ':'
															+ serverPort
															+ '/ctrl/logs')
											.success(
													function(data) {

														for ( var i = 0; i < data.length; i++) {
															var index = oldLogs.length;
															oldLogs[index] = {};
															oldLogs[index] = data[i];
														}
														var out = [];
														var logCount = 50;
														this
																.clearFrame('serverIframe');
														for ( var i = 0; i < oldLogs.length; i++) {
															this
																	.write(
																			"serverIframe",
																			'['
																					+ oldLogs[i].level
																					+ '] '
																					+ oldLogs[i].message
																					+ '<br/>');
															logCount--;
															if (logCount == 0)
																break;
														}
														$scope.log = out;

													});
								}
								$timeout(getLogs, 2000);
							})();

						} ]);
