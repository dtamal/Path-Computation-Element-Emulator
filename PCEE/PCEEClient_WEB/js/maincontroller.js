window.oldLogs = [];

app.controller('logCtrl', [
		'$scope',
		'$http',
		'$timeout',
		function logCtrl($scope, $http, $timeout) {
			$scope.log = [];

			(function getLogs() {
				if (isConnected) {
					$http.get('http://localhost:8081/ctrl/logs').success(
							function(data) {

								for ( var i = 0; i < data.length; i++) {
									var index = oldLogs.length;
									oldLogs[index] = {};
									oldLogs[index] = data[i];
								}
								var out = [];
								var logCount = 50;
								this.clearFrame('clientIframe');
								for ( var i = 0; i < oldLogs.length; i++) {
									this.write("clientIframe", '['
											+ oldLogs[i].level + '] '
											+ oldLogs[i].message + '<br/>');
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

app
		.controller(
				'serverConnection',
				function($scope, $http) {
					$scope.connectionIP = '127.0.0.1';
					$scope.connectionPort = '8080';

					$scope.connectToServer = function() {
						if (!isConnected) {
							(function getStatus() {
								$http
										.get(
												'http://' + $scope.connectionIP
														+ ':'
														+ $scope.connectionPort
														+ '/ctrl/server/status')
										.success(
												function(data) {
													alert('Connection established: '
															+ data);

													this.connectClient();
													// /////////Temporal test
													// method to draw the
													// network
													// $.get("SNDlib/15nodes.txt",
													// function(data) {
													// loadNetworkGraph(data,
													// 1.1, 20, -20);
													// }, 'text');
													// ///////

													$http
															.get(
																	'http://'
																			+ $scope.connectionIP
																			+ ':'
																			+ $scope.connectionPort
																			+ '/ctrl/server/nodes')
															.success(
																	function(
																			nodes) {
																		$http
																				.get(
																						'http://'
																								+ $scope.connectionIP
																								+ ':'
																								+ $scope.connectionPort
																								+ '/ctrl/server/links')
																				.success(
																						function(
																								links) {
																							// loadNetworkGraph(data,
																							// 1.1,
																							// 20,
																							// -20);
																							drawNetworkgraph(
																									nodes,
																									links);
																						});

																	})
															.error(
																	function(
																			data) {
																		alert('Error: '
																				+ data);

																	});

												})
										.error(
												function(data) {
													alert('Error: server not running'
															+ data);

												});

							})();
						} else {
							(function setStatus() {
								alert('Disconnected from the server');
								this.connectClient();

							})();

						}

					}

				});
