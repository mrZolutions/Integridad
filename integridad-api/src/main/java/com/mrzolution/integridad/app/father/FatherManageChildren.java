package com.mrzolution.integridad.app.father;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.mrzolution.integridad.app.interfaces.Child;
import com.mrzolution.integridad.app.interfaces.ChildRepository;


/**
 * Created by daniel on 5/5/17.
 */
public class FatherManageChildren {
    Father father;
    ChildRepository childRepository;
    CrudRepository crudRepository;

    public FatherManageChildren(Father father, ChildRepository childRepository, CrudRepository crudRepository) {
        this.father = father;
        this.childRepository = childRepository;
        this.crudRepository = crudRepository;
    }

    public void updateChildren () {
        List<UUID> childrenOldIdList = new ArrayList<>();
        List<UUID> childrenNewIdList = new ArrayList<>();

        List childList = new ArrayList();

        if (father.obtainChildren() != null) {
            List<Child> childrenNew = father.obtainChildren();
            childrenNew.forEach(children -> childrenNewIdList.add(children.getId()));

            Iterable<UUID> childrenOld = childRepository.findByFather(father.getDad());
            childrenOld.forEach(childId -> childrenOldIdList.add(childId));
            
            childrenNew.stream().forEach(child -> {
                try {
                    Class[] cArg = new Class[1];
                    cArg[0] = father.getDad().getClass();
                    child.getClass().getMethod("set" + father.getDad().getClass().getSimpleName(), cArg).invoke(child, father.getDad());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                childList.add(child);
//                crudRepository.save(child);
            });
            if(!childList.isEmpty()){
                crudRepository.save(childList);
            }
            childrenOldIdList.stream().filter(
                    childId -> !childrenNewIdList.contains(childId)
            ).forEach(childId -> {crudRepository.delete(childId);});
        }
        
    }
}
