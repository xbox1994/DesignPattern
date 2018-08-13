package Other.xml;

public class SensorInfo {
    private String name;
    private String value;
    private String status;

    public SensorInfo(String name, String value, String status) {
        this.name = name;
        this.value = value;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SensorInfo{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
