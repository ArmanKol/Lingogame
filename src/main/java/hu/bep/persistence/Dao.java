package hu.bep.persistence;

import java.util.List;

public interface Dao {
    Object getObjectByID(int id);
    List<String> getAll();
    Object saveObject(Object obj);

}
