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
	
	// Pupil Memebers means for outside use.
	// Stores the original information on the pupil
	this.pupil = { };
	
	this.initialise = function () {
		// Premptively loads and caches the list of available subjects
		dataStore.subjects.get();
	}
		
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
					console.error(textStatus + ": " + errorThrown);
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
				console.error(textStatus + ": " + errorThrown);
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
				console.error(textStatus + ": " + errorThrown);
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
				console.error(textStatus + ": " + errorThrown);
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
				console.error(textStatus + ": " + errorThrown);
			});
		};
		
		// Gets a list of the subjects that have targets associated with them. Does this
		// by chaining off the getTargetsForSubjects method and just creating a
		// callback to call the other callback.
		this.getWithTargets = function (keyStage, callback, forceReload)
		{
			callback = typeof callback !== 'undefined' ? callback : function(data) {};
			dataStore.targets.get(keyStage, function() {
				callback(subjectsWithTargets[keyStage]);
			}, forceReload);
		};
		
		// WARNING: As implemented this method will only work if the information
		// has already been fetched from the servers!
		this.getWithoutTarget = function (keyStage)
		{
			var result = {};
			
			for (var subjectKey in subjects)
			{
				if (!(subjectKey in subjectsWithTargets[keyStage]))
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
		this.get = function (keyStage, callback, forceReload)
		{
			forceReload = typeof forceReload !== 'undefined' ? forceReload : false;
			callback = typeof callback !== 'undefined' ? callback : function() { };
	
			if ((keyStage in targetsForSubjects) && (!forceReload))
			{
				callback(targetsForSubjects[keyStage]);
				return;
			}
			
			var data = { KeyStage: keyStage }
			loading("show");
			$.get("/api/pupil/" + dataStore.pupil.key + "/target", data, function(jsonData) {
				subjectsWithTargets[keyStage] = {};
				targetsForSubjects[keyStage] = {};
				
				for (var index in jsonData)
				{
					var target = jsonData[index];
					subjectsWithTargets[keyStage][target.subject.key] = target.subject;
					targetsForSubjects[keyStage][target.key] = target;
				}
	
				callback(targetsForSubjects[keyStage]);
				loading("hide");
			}, "json").fail(function (jqXHR, textStatus, errorThrown) {
				loading("hide");
				alert("Unable to load targets from server");
				console.error(textStatus + ": " + errorThrown);
			});
		};
		
		this.remove = function (targetKey, keyStage, callback)
		{
			loading("show");
			$.ajax('/api/target/'+ targetKey,
				{ type: "DELETE" }).done(function ()
					{ 
						delete subjectsWithTargets[keyStage][targetsForSubjects[keyStage][targetKey].subject.key];
						delete targetsForSubjects[keyStage][targetKey];
						callback(targetsForSubjects);
						loading("hide");
					})
				.fail(function (jqXHR, textStatus, errorThrown) {
					loading("hide");
					alert("Unable to delete the record specified!");
					console.error(textStatus + ": " + errorThrown);
				});
		};
		
		this.update = function (targetKey, keyStage, targetData, callback)
		{
			loading("show");
			$.ajax('/api/target/'+ targetKey, { type: "PUT", data: targetData })
				.done(function () {
					var target = targetsForSubjects[keyStage][targetKey];
					target.threeLevelsTargetGrade = targetData.ThreeLevelsTarget;
					target.fourLevelsTargetGrade = targetData.FourLevelsTarget;
					target.fiveLevelsTargetGrade = targetData.FiveLevelsTarget;
					callback(targetsForSubjects);
					loading("hide");
				}).fail(function (jqXHR, textStatus, errorThrown) {
					loading("hide");
					alert("Unable to update the record specified!");
					console.error(textStatus + ": " + errorThrown);
				});
		};
		
		this.create = function (subjectKey, keyStage, callback) {
			var grades = getGrades(keyStage, subjects[subjectKey].vocational);
			var newData = { TargetPupilKey: dataStore.pupil.key,
			             TargetSubjectKey: subjectKey,
			             TargetKeyStage: keyStage,
			             ThreeLevelsTarget: grades[0],
			             FourLevelsTarget: grades[0],
			             FiveLevelsTarget: grades[0] };
			
			loading("show");
			$.post ('/api/target', newData,  function(newTarget) {
					targetsForSubjects[keyStage][newTarget.key] = newTarget;
					subjectsWithTargets[keyStage][newTarget.subject.key] = newTarget.subject;
					callback(newTarget);
					 loading("hide");
				}, "json").fail(function (jqXHR, textStatus, errorThrown) {
					loading("hide");
					alert("Unable to save the record!");
					console.error(textStatus + ": " + errorThrown);
				});
		};
	};
			
};
