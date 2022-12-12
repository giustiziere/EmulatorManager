package manager.dto.ios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuntimesItem implements Serializable {

    @JsonProperty("identifier")
    private String identifier;

    @JsonProperty("isAvailable")
    private Boolean isAvailable;

    @JsonProperty("bundlePath")
    private String bundlePath;

    @JsonProperty("buildversion")
    private String buildVersion;

    @JsonProperty("runtimeRoot")
    private String runtimeRoot;

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;
}