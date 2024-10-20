package io.github.betterclient.fabric.api;

import com.google.gson.stream.JsonReader;
import io.github.betterclient.fabric.relocate.loader.api.metadata.CustomValue;

import java.io.IOException;
import java.util.*;

public abstract class CustomValueImpl implements CustomValue {
    static final CustomValue BOOLEAN_TRUE = new BooleanImpl(true);
    static final CustomValue BOOLEAN_FALSE = new BooleanImpl(false);
    static final CustomValue NULL = new NullImpl();

    public static CustomValue readCustomValue(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case BEGIN_OBJECT:
                reader.beginObject();

                // To preserve insertion order
                final Map<String, CustomValue> values = new LinkedHashMap<>();

                while (reader.hasNext()) {
                    values.put(reader.nextName(), readCustomValue(reader));
                }

                reader.endObject();

                return new ObjectImpl(values);
            case BEGIN_ARRAY:
                reader.beginArray();

                final List<CustomValue> entries = new ArrayList<>();

                while (reader.hasNext()) {
                    entries.add(readCustomValue(reader));
                }

                reader.endArray();

                return new ArrayImpl(entries);
            case STRING:
                return new StringImpl(reader.nextString());
            case NUMBER:
                return new NumberImpl(reader.nextDouble());
            case BOOLEAN:
                if (reader.nextBoolean()) {
                    return BOOLEAN_TRUE;
                }

                return BOOLEAN_FALSE;
            case NULL:
                reader.nextNull();
                return NULL;
            default:
                throw new RuntimeException("default");
        }
    }

    @Override
    public final CvObject getAsObject() {
        if (this instanceof ObjectImpl) {
            return (ObjectImpl) this;
        } else {
            throw new ClassCastException("can't convert "+getType().name()+" to Object");
        }
    }

    @Override
    public final CvArray getAsArray() {
        if (this instanceof ArrayImpl) {
            return (ArrayImpl) this;
        } else {
            throw new ClassCastException("can't convert "+getType().name()+" to Array");
        }
    }

    @Override
    public final String getAsString() {
        if (this instanceof StringImpl) {
            return ((StringImpl) this).value;
        } else {
            throw new ClassCastException("can't convert "+getType().name()+" to String");
        }
    }

    @Override
    public final Number getAsNumber() {
        if (this instanceof NumberImpl) {
            return ((NumberImpl) this).value;
        } else {
            throw new ClassCastException("can't convert "+getType().name()+" to Number");
        }
    }

    @Override
    public final boolean getAsBoolean() {
        if (this instanceof BooleanImpl) {
            return ((BooleanImpl) this).value;
        } else {
            throw new ClassCastException("can't convert "+getType().name()+" to Boolean");
        }
    }

    private static final class ObjectImpl extends CustomValueImpl implements CvObject {
        private final Map<String, CustomValue> entries;

        ObjectImpl(Map<String, CustomValue> entries) {
            this.entries = Collections.unmodifiableMap(entries);
        }

        @Override
        public CvType getType() {
            return CvType.OBJECT;
        }

        @Override
        public int size() {
            return entries.size();
        }

        @Override
        public boolean containsKey(String key) {
            return entries.containsKey(key);
        }

        @Override
        public CustomValue get(String key) {
            return entries.get(key);
        }

        @Override
        public Iterator<Map.Entry<String, CustomValue>> iterator() {
            return entries.entrySet().iterator();
        }
    }

    private static final class ArrayImpl extends CustomValueImpl implements CvArray {
        private final List<CustomValue> entries;

        ArrayImpl(List<CustomValue> entries) {
            this.entries = Collections.unmodifiableList(entries);
        }

        @Override
        public CvType getType() {
            return CvType.ARRAY;
        }

        @Override
        public int size() {
            return entries.size();
        }

        @Override
        public CustomValue get(int index) {
            return entries.get(index);
        }

        @Override
        public Iterator<CustomValue> iterator() {
            return entries.iterator();
        }
    }

    private static final class StringImpl extends CustomValueImpl {
        final String value;

        StringImpl(String value) {
            this.value = value;
        }

        @Override
        public CvType getType() {
            return CvType.STRING;
        }
    }

    private static final class NumberImpl extends CustomValueImpl {
        final Number value;

        NumberImpl(Number value) {
            this.value = value;
        }

        @Override
        public CvType getType() {
            return CvType.NUMBER;
        }
    }

    private static final class BooleanImpl extends CustomValueImpl {
        final boolean value;

        BooleanImpl(boolean value) {
            this.value = value;
        }

        @Override
        public CvType getType() {
            return CvType.BOOLEAN;
        }
    }

    private static final class NullImpl extends CustomValueImpl {
        @Override
        public CvType getType() {
            return CvType.NULL;
        }
    }
}
