package manager.dto.ios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IOSDeviceListDTO implements Serializable {

    @JsonProperty("devicetypes")
    private List<DeviceTypesItem> deviceTypes;

    @JsonProperty("runtimes")
    private List<RuntimesItem> runtimes;

    @JsonProperty("devices")
    private Map<String, List<DeviceObject>> devices;
}