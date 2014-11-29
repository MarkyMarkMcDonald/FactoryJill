import java.util.Map;

public class Blueprint<T> {

    private Map<String, Object> attributes;

    private Class<T> clazz;

    public Blueprint() {
    }

    public Blueprint(Class<T> clazz, Map<String, Object> attributes) {
        this.attributes = attributes;
        this.clazz = clazz;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }
}
