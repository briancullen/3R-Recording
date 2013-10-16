var logger = new function () {
	var baseUrl = '/logging/';
	
	var windowUrl = baseUrl + 'window';
	var userUrl = baseUrl + 'user';
	var eventUrl = baseUrl + 'event';
	var ajaxUrl = baseUrl + 'ajax';
	
	this.tracking = true;
	
	this.LOG_INFO = 0;
	this.LOG_WARN = 1;
	this.LOG_ERROR = 2;
	this.LOG_CRITICAL = 3;
	
	function sendData (url, data) {
		if (logger.tracking)
			$.post (url, data);
	}
	
	this.logWindow = function (message, url, line) {
		sendData (windowUrl, {LOG_MESSAGE: message, LOG_SOURCEURL: url, LOG_LINENUM: line});
	};
	
	this.logEvent = function(message) {
		sendData (eventUrl, {LOG_MESSAGE: message});
	};
	
	this.logUser = function(level, message, params) {
		if (typeof params === 'object')
			params = JSON.stringify(params);
		
		sendData (userUrl, {LOG_LEVEL: level, LOG_MESSAGE: message, LOG_PARAMS: params});
	};
	
	this.logAjax = function(level, requestUrl, requestType, requestParams, responseCode, responseText, xhrObj) {
		if (typeof requestParams === 'object')
			requestParams = JSON.stringify(requestParams);
		
		if (typeof xhrObj === 'object')
			xhrObj = JSON.stringify(xhrObj);
		
		sendData (ajaxUrl, {LOG_LEVEL: level, LOG_REQUESTURL: requestUrl, LOG_TYPE: requestType,
			LOG_PARAMS: requestParams, LOG_CODE: responseCode, LOG_TEXT: responseText, LOG_OBJ: xhrObj});
	};

};

// This should register for the javascript errors
// caught by the browser.
window.onerror = logger.logWindow;
