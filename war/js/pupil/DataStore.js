// This class is used to store the informaiton downloaded from
// the server for use on the client. This is being done centrally
// as the same information is required on a number of different
// pages.
var dataStore = new function () {
		
	// Private data variables used to store downloaded
	// information that may be requested.
	// Use to hold a list of all the subjects available.
	var subjects = {};
	
	// Used to hold the list of subjects which have a
	// target attached to them in a particular key stage.
	var subjectsWithTargets = {};
	
	// Used to hold a list of all the targets downloaded
	// for the server by keystage.
	var targetsForSubjects = {};
	
	// Holds the details of the progress records downloaded
	// from the server by year and type.
	var progressToTargets = {};
	
	// Pupil Members meant for outside use.
	// Stores the original information on the pupil
	this.pupil = { };
	
	var formData = { };
	
	this.initialise = function () {
		// Premptively loads and caches the list of available subjects
		dataStore.subjects.get();
	}
	
	this.updatePupil = function (newData, callback) {
		callback = typeof callback !== 'undefined' ? callback : function() {};
		
		loading("show");
		$.ajax('/api/pupil/'+ dataStore.pupil.key, {type: "PUT", data: newData, dataType: "json"}).done(function (data) {
			
			dataStore.pupil.displayName = data.displayName;
			dataStore.pupil.form = data.form.formCode;
			dataStore.pupil.formKey = data.form.key;
			dataStore.pupil.year = convertIntakeToYear(data.form.intakeYear);
			dataStore.pupil.stage = convertIntakeToStage(data.form.intakeYear);
			
			callback(data);
			loading("hide");
		}).fail (function(jqXHR, textStatus, errorThrown) {
			loading("hide");
			alert("Unable to save the record!");
			logger.logAjax(logger.LOG_ERROR, '/api/pupil/'+ dataStore.pupil.key, "PUT",
					newData, textStatus, errorThrown, jqXHR);
		});
	};
		
	this.progress = new function () {
		this.get = function (year, type, callback, forceReload) {
			forceReload = typeof forceReload !== 'undefined' ? forceReload : false;
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			if ((year in progressToTargets) && (type in progressToTargets[year]) 
					&& (!forceReload))
			{
				callback(progressToTargets[year][type]);
				return;
			}
			
			loading("show");
			$.getJSON('/api/pupil/' + dataStore.pupil.key + '/progress', {Year: year, Type: type }, 
				function (data) {
					if(!(year in progressToTargets))
						progressToTargets[year] = {};
					
					progressToTargets[year][type] = {};
					for (var index in data) {
						progressToTargets[year][type][data[index].progress.key] = data[index];
					}
					
					callback(progressToTargets[year][type]);
					loading("hide");
				}).fail(function (jqXHR, textStatus, errorThrown) {
					loading("hide"); alert("Unable to load progress records from server");
					logger.logAjax(logger.LOG_ERROR, '/api/pupil/' + dataStore.pupil.key + '/progress', "GET",
							{Year: year, Type: type }, textStatus, errorThrown, jqXHR);
				});
		};
		
		this.create = function (newProgressData, callback) {
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			loading("show");
			$.post ('/api/progress', newProgressData,  function(data) {
				progressToTargets[data.progress.yearGroup][data.progress.recordType][data.progress.key] = data;
				callback(data);
				loading("hide");
			}, "json").fail(function (jqXHR, textStatus, errorThrown)	{
				loading("hide"); alert("Unable to save the record!");
				logger.logAjax(logger.LOG_ERROR, '/api/progress', "POST",
						newProgressData, textStatus, errorThrown, jqXHR);
			});
			
		};
		
		this.update = function (progressKey, newProgressData, callback) {
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			loading("show");
			$.ajax('/api/progress/'+ progressKey, {type: "PUT", data: newProgressData, dataType: "json"}).done(function (data) {
				progressToTargets[data.progress.yearGroup][data.progress.recordType][data.progress.key] = data;
				callback(data);
				loading("hide");
			}).fail (function(jqXHR, textStatus, errorThrown) {
				loading("hide");
				alert("Unable to save the record!");
				logger.logAjax(logger.LOG_ERROR, '/api/progress/'+ progressKey, "PUT",
						newProgressData, textStatus, errorThrown, jqXHR);
			});
		}
		
		this.remove = function (progressKey, year, type, callback) {
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			loading("show");
			$.ajax('/api/progress/'+ progressKey, {type: "DELETE"}).done(function () {
				delete progressToTargets[year][type][progressKey];
				callback(progressToTargets[year][type]);
				loading("hide");
			}).fail(function (jqXHR, textStatus, errorThrown) {
				loading("hide");
				alert("Unable to delete the record specified!");
				logger.logAjax(logger.LOG_ERROR, '/api/progress/'+ progressKey, "DELETE",
						"", textStatus, errorThrown, jqXHR);
			});
		};
		
	};

	// Holds all the functions related to subjects.
	this.subjects = new function () {
		// Downloads the current list of subjects.
		this.get = function (callback, forceReload) {
			forceReload = typeof forceReload !== 'undefined' ? forceReload : false;
			callback = typeof callback !== 'undefined' ? callback : function() {};
			
			if ((!($.isEmptyObject(subjects))) && (!forceReload))
			{
				callback(subjects);
				return;
			}
			
			loading("show");
			$.get("/api/subject", function (jsonData) {
				subjects = {};
				for (var index in jsonData)
				{
					subjects[jsonData[index].key] = jsonData[index];
				}
				callback(subjects);
				loading("hide");
			}, "json").fail(function (jqXHR, textStatus, errorThrown) {
				loading("hide");
				alert("Unable to load subjects from server");
				logger.logAjax(logger.LOG_ERROR, '/api/subject/', "GET",
						"", textStatus, errorThrown, jqXHR);
			});
		};
		
		// Gets a list of the subjects that have targets associated with them. Does this
		// by chaining off the getTargetsForSubjects method and just creating a
		// callback to call the other callback.
		this.getWithTargets = function (stage, callback, forceReload)
		{
			callback = typeof callback !== 'undefined' ? callback : function(data) {};
			dataStore.targets.get(stage, function() {
				callback(subjectsWithTargets[stage]);
			}, forceReload);
		};
		
		// WARNING: As implemented this method will only work if the information
		// has already been fetched from the servers!
		this.getWithoutTarget = function (stage)
		{
			var result = {};
			
			for (var subjectKey in subjects)
			{
				if ((subjectsWithTargets === undefined) ||
						(stage === undefined) ||
						(subjectsWithTargets[stage] === undefined))
				{
					logger.logUser(logger.LOG_ERROR, "Getting Subjects Without Targets (site of undefined error)",
						{ STAGEKEY: stage, SUBJECTOBJ: subjectsWithTargets});
				}
				else if (!(subjectKey in subjectsWithTargets[stage]))
				{
					result[subjectKey] = subjects[subjectKey];
				}

			}
			return result;
		};
	};

	this.targets = new function () {
		
		// Downloads all the target information for the current pupil in a particular
		// key stage. All create the subjects with targets list at the same time.
		this.get = function (stage, callback, forceReload)
		{
			forceReload = typeof forceReload !== 'undefined' ? forceReload : false;
			callback = typeof callback !== 'undefined' ? callback : function() { };
	
			if ((stage in targetsForSubjects) && (!forceReload))
			{
				callback(targetsForSubjects[stage]);
				return;
			}
			
			var data = { Stage: stage }
			loading("show");
			$.get("/api/pupil/" + dataStore.pupil.key + "/target", data, function(jsonData) {
				subjectsWithTargets[stage] = {};
				targetsForSubjects[stage] = {};
				
				for (var index in jsonData)
				{
					var target = jsonData[index];
					subjectsWithTargets[stage][target.subject.key] = target.subject;
					targetsForSubjects[stage][target.key] = target;
				}
	
				callback(targetsForSubjects[stage]);
				loading("hide");
			}, "json").fail(function (jqXHR, textStatus, errorThrown) {
				loading("hide");
				alert("Unable to load targets from server");
				logger.logAjax(logger.LOG_ERROR, "/api/pupil/" + dataStore.pupil.key + "/target", "GET",
						"", textStatus, errorThrown, jqXHR);
			});
		};
		
		this.remove = function (targetKey, stage, callback)
		{
			loading("show");
			$.ajax('/api/target/'+ targetKey,
				{ type: "DELETE" }).done(function ()
					{ 
						delete subjectsWithTargets[stage][targetsForSubjects[stage][targetKey].subject.key];
						delete targetsForSubjects[stage][targetKey];
						callback(targetsForSubjects);
						loading("hide");
					})
				.fail(function (jqXHR, textStatus, errorThrown) {
					loading("hide");
					alert("Unable to delete the record specified!");
					logger.logAjax(logger.LOG_ERROR, '/api/target/'+ targetKey, "DELETE",
							"", textStatus, errorThrown, jqXHR);
				});
		};
		
		this.update = function (targetKey, stage, targetData, callback)
		{
			loading("show");
			$.ajax('/api/target/'+ targetKey, { type: "PUT", data: targetData })
				.done(function () {
					var target = targetsForSubjects[stage][targetKey];
					target.threeLevelsTargetGrade = targetData.ThreeLevelsTarget;
					target.fourLevelsTargetGrade = targetData.FourLevelsTarget;
					target.fiveLevelsTargetGrade = targetData.FiveLevelsTarget;
					callback(targetsForSubjects);
					loading("hide");
				}).fail(function (jqXHR, textStatus, errorThrown) {
					loading("hide");
					alert("Unable to update the record specified!");
					logger.logAjax(logger.LOG_ERROR, '/api/target/'+ targetKey, "PUT",
							targetData, textStatus, errorThrown, jqXHR);
				});
		};
		
		this.create = function (subjectKey, stage, callback) {
			var grades = getGrades(stage, subjects[subjectKey].vocational);
			var newData = { TargetPupilKey: dataStore.pupil.key,
			             TargetSubjectKey: subjectKey,
			             TargetStage: stage,
			             ThreeLevelsTarget: grades[0],
			             FourLevelsTarget: grades[0],
			             FiveLevelsTarget: grades[0] };
			
			loading("show");
			$.post ('/api/target', newData,  function(newTarget) {
					if (targetsForSubjects[stage] === undefined)
						targetsForSubjects[stage] = {};
					
					targetsForSubjects[stage][newTarget.key] = newTarget;
					
					if (subjectsWithTargets[stage] === undefined)
						subjectsWithTargets[stage] = {};
					
					subjectsWithTargets[stage][newTarget.subject.key] = newTarget.subject;
					callback(newTarget);
					 loading("hide");
				}, "json").fail(function (jqXHR, textStatus, errorThrown) {
					loading("hide");
					alert("Unable to save the record!");
					logger.logAjax(logger.LOG_ERROR, '/api/target/', "POST",
							newData, textStatus, errorThrown, jqXHR);
				});
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
			
			loading("show");
			$.getJSON("/api/form", function (data) {
				for (var index in data)
				{
					formData[data[index].key] = data[index];
				}
				loading("hide");
				callback(formData);
			}).fail(function (jqXHR, textStatus, errorThrown) {
				loading("hide");
				alert("Unable to save the record!");
				logger.logAjax(logger.LOG_ERROR, '/api/form/', "GET",
						"", textStatus, errorThrown, jqXHR);
			});
		};
	};
			
};
