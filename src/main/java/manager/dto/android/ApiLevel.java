package manager.dto.android;

import lombok.Getter;

/**
 * See full list: <a href='https://source.android.com/setup/start/build-numbers'>Source</a>
 */
@Getter
public enum ApiLevel {
    API_26("26", "8.0.0"),
    API_27("27", "8.1.0"),
    API_28("28", "9"),
    API_29("29", "10"),
    API_30("30", "11"),
    API_31("31", "12");

    private final String apiLevel;
    private final String androidVersion;

    ApiLevel(String apiLevel, String androidVersion) {
        this.apiLevel = apiLevel;
        this.androidVersion = androidVersion;
    }

    public static ApiLevel getApiByVersion(String version) {
        for (ApiLevel apiLevel : ApiLevel.values()) {
            if (apiLevel.getAndroidVersion().equals(version)) return apiLevel;
        }
        throw new IllegalArgumentException(version + " version doesn't match any API");
    }
}
