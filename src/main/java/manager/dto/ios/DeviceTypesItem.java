package manager.dto.ios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceTypesItem implements Serializable {

    @JsonProperty("productFamily")
    private String productFamily;

    @JsonProperty("identifier")
    private String identifier;

    @JsonProperty("bundlePath")
    private String bundlePath;

    @JsonProperty("maxRuntimeVersion")
    private Long maxRuntimeVersion;

    @JsonProperty("name")
    private String name;

    @JsonProperty("minRuntimeVersion")
    private Integer minRuntimeVersion;
}