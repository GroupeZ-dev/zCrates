package fr.traqueur.crates.api.settings;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import fr.traqueur.structura.api.Loadable;

public interface Settings extends Loadable {

    ClassToInstanceMap<Settings> INSTANCES = MutableClassToInstanceMap.create();

    static <T extends Settings> T get(Class<T> clazz) {
        return INSTANCES.getInstance(clazz);
    }

    static <T extends Settings> void register(Class<T> clazz, T instance) {
        INSTANCES.putInstance(clazz, instance);
    }

}
