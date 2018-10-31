package de.mc_anura.eisenbahn;

public class DataContainer {

    private String name;
    private Object value;
    private DataType datatype;

    public DataContainer(String name, Object value, DataType datatype) {
        this.name = name;
        this.value = value;
        this.datatype = datatype;
    }

    public String getName() {
        return name;
    }

    public <T> T getValue() {
        return (T) value;
    }

    public DataType getDataType() {
        return datatype;
    }

    public enum DataType {
        BOOLEAN(Boolean.class),
        INT(Integer.class),
        ;

        private final Class<?> clazz;

        DataType(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public static DataType getDataTypeById(int id) {
            return DataType.values()[id];
        }
    }
}
