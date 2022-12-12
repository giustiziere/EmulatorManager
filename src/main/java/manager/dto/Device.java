package manager.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class Device implements Serializable {
    private String platform;
    private String id;
    private String name;
    private String osVersion;
    private String status;
}
