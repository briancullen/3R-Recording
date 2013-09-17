package net.mrcullen.targetrecording.process;

import static net.mrcullen.targetrecording.OfyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import net.mrcullen.targetrecording.entities.TeacherEntity;

public class TeacherInformation {

	public static List<TeacherEntity> getTeachers ()
	{
		return ofy().load().type(TeacherEntity.class).list();
	}
	
	public static void removeTeacher (Key<TeacherEntity> key)
	{
		ofy().delete().entity(key);
	}
	
	public static Key<TeacherEntity> saveTeacher (TeacherEntity update)
	{
		return ofy().save().entity(update).now();
	}
	
	public static TeacherEntity getTeacher (String teacherEmail)
	{
		if (teacherEmail == null)
			return null;
		
		return getTeacher(Key.create(TeacherEntity.class, teacherEmail));
	}
	
	public static TeacherEntity getTeacher (Key<TeacherEntity> key)
	{
		return ofy().load().key(key).now();
	}
	
	public static TeacherEntity getTeacherByStaffCode (String staffCode)
	{
		return ofy().load().type(TeacherEntity.class).filter("staffCode", staffCode.toUpperCase()).first().now();
	}
}
