package manager.dto.ios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceObject implements Serializable {

    @JsonProperty("isAvailable")
    private Boolean isAvailable;

    @JsonProperty("logPathSize")
    private Long logPathSize;

    @JsonProperty("logPath")
    private String logPath;

    @JsonProperty("name")
    private String name;

    @JsonProperty("state")
    private String state;

    @JsonProperty("udid")
    private String udid;

    @JsonProperty("deviceTypeIdentifier")
    private String deviceTypeIdentifier;

    @JsonProperty("dataPath")
    private String dataPath;

    @JsonProperty("dataPathSize")
    private Long dataPathSize;
}