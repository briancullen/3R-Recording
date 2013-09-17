package net.mrcullen.targetrecording;

import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.PersonEntity;
import net.mrcullen.targetrecording.entities.PupilEntity;
import net.mrcullen.targetrecording.entities.PupilTargetEntity;
import net.mrcullen.targetrecording.entities.SubjectEntity;
import net.mrcullen.targetrecording.entities.TargetProgressEntity;
import net.mrcullen.targetrecording.entities.TeacherEntity;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
    static {
    	ObjectifyService.register(PersonEntity.class);
    	ObjectifyService.register(PupilEntity.class);
    	ObjectifyService.register(TeacherEntity.class);
    	ObjectifyService.register(SubjectEntity.class);
    	ObjectifyService.register(TargetProgressEntity.class);
    	ObjectifyService.register(PupilTargetEntity.class);
    	ObjectifyService.register(FormEntity.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
