package fr.traqueur.crates.models.wrappers;

import fr.traqueur.crates.api.Logger;
import org.mozilla.javascript.NativeArray;

/**
 * Helper class for JavaScript array conversions.
 * Provides utilities to convert JavaScript arrays to Java arrays.
 */
public class ArrayHelper {

    /**
     * Converts a JavaScript array to a Java int[] array.
     * Accepts both JavaScript arrays (NativeArray) and Java int[] arrays.
     *
     * @param array the array to convert (can be NativeArray or int[])
     * @return the converted int[] array, or null if conversion fails
     */
    public static int[] toIntArray(Object array) {
        if (array == null) {
            Logger.warning("Cannot convert null to int array");
            return null;
        }

        // Already a Java int array
        if (array instanceof int[] javaArray) {
            return javaArray;
        }

        // JavaScript NativeArray
        if (array instanceof NativeArray nativeArray) {
            int length = (int) nativeArray.getLength();
            int[] result = new int[length];

            for (int i = 0; i < length; i++) {
                Object element = nativeArray.get(i);
                if (element instanceof Number num) {
                    result[i] = num.intValue();
                } else {
                    Logger.warning("Invalid array element at index {}: {}", i, element);
                    return null;
                }
            }

            return result;
        }

        Logger.warning("Cannot convert {} to int array", array.getClass().getName());
        return null;
    }
}