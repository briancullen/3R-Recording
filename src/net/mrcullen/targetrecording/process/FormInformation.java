package net.mrcullen.targetrecording.process;

import static net.mrcullen.targetrecording.OfyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.entities.FormEntity;

public class FormInformation {

	public static List<FormEntity> getForms ()
	{
		return ofy().load().type(FormEntity.class).list();
	}

	public static FormEntity getFormByStaffCode (String staffCode)
	{
		return ofy().load().type(FormEntity.class).filter("formCode", staffCode.toUpperCase()).first().now();
	}
	
	public static List<FormEntity> getFormByIntakeYear (int year)
	{
		return ofy().load().type(FormEntity.class).filter("intakeYear", year).list();
	}
	
	public static void removeForm (Key<FormEntity> key)
	{
		PupilInformation.removePupilsByForm(key);
		ofy().delete().entity(key);
	}
	
	public static void removeForm (long formId)
	{
		removeForm(Key.create(FormEntity.class, formId));
	}
	
	public static Key<FormEntity> saveForm (FormEntity update)
	{
		return ofy().save().entity(update).now();
	}
	
	public static FormEntity getForm (Key<FormEntity> key)
	{
		return ofy().load().key(key).now();
	}
	
	public static FormEntity getForm (long formId)
	{
		return getForm(Key.create(FormEntity.class, formId));
	}
}
