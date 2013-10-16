// This class is used to store the informaiton downloaded from
// the server for use on the client. This is being done centrally
// as the same information is required on a number of different
// pages.
var dataStore = new function () {
	
	var formData = { };
	var subjectData = { };
	
	this.teacher = { };
	
	this.initialise = function () {
	};
	
	this.subjects = new function () {
		this.get = function (callback, forceReload) {
			forceReload = typeof forceReload !== 'undefined' ? forceReload : false;
			callback = typeof callback !== 'undefined' ? callback : function() {};
		
			if ((!$.isEmptyObject(subjectData)) && (!forceReload))
			{
				callback(subjectData);
				return;
			}
			
			$.getJSON("/api/subject", function (data) {
				for (var index in data)
				{
					subjectData[data[index].key] = data[index];
				}
				callback(subjectData);
			});
		};
		
		this.create = function (newSubjectData, callback) {
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			$.post ('/api/subject', newSubjectData,  function(data) {
				subjectData[data.key] = data;
				callback(data);
			}, "json").fail(function ()	{ alert("Unable to save the record!"); });
		};
		
		this.bulkCreate = function (bulkCSVData, callback) {
			$.post ('/api/subject', bulkCSVData,  function(data) {
				for (var index in data) {
					subjectData[data[index].key] = data[index];
				}
				callback(data);
			}, "json").fail(function ()	{ alert("Unable to save the record!"); });
		};
		
		this.update = function (subjectKey, newSubjectData, callback) {
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			$.ajax('/api/subject/'+ subjectKey, {type: "PUT", data: newSubjectData, dataType: "json"}).done(function (data) {
				subjectData[data.key] = data;
				callback(data);
			});
		}
		
		this.remove = function (subjectKey, callback) {
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			$.ajax('/api/subject/'+ subjectKey, {type: "DELETE"}).done(function () {
				delete subjectData[subjectKey];
				callback(subjectData);
			}).fail(function () { alert("Unable to delete the record specified!"); });
		};
	};
	
	this.forms = new function () {
		this.get = function (callback, forceReload) {
			forceReload = typeof forceReload !== 'undefined' ? forceReload : false;
			callback = typeof callback !== 'undefined' ? callback : function() {};

			if ((!$.isEmptyObject(formData)) && (!forceReload))
			{
				callback(formData);
				return;
			}
			
			$.getJSON("/api/form", function (data) {
				for (var index in data)
				{
					formData[data[index].key] = data[index];
				}
				callback(formData);
			});
		};
			
		this.create = function (newFormData, callback) {
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			$.post ('/api/form', newFormData,  function(data) {
				formData[data.key] = data;
				callback(data);
			}, "json").fail(function ()	{ alert("Unable to save the record!"); });
		};
		
		this.bulkCreate = function (bulkCSVData, callback) {
			$.post ('/api/form', bulkCSVData,  function(data) {
				for (var index in data) {
					formData[data[index].key] = data[index];
				}
				callback(data);
			}, "json").fail(function ()	{ alert("Unable to save the record!"); });
		};
		
		this.update = function (formKey, newFormData, callback) {
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			$.ajax('/api/form/'+ formKey, {type: "PUT", data: newFormData, dataType: "json"}).done(function (data) {
				formData[data.key] = data;
				callback(data);
			});
		}
		
		this.remove = function (formKey, callback) {
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			$.ajax('/api/form/'+ formKey, {type: "DELETE"}).done(function () {
				delete formData[formKey];
				callback(formData);
			}).fail(function () { alert("Unable to delete the record specified!"); });
		};

	};
		
};
