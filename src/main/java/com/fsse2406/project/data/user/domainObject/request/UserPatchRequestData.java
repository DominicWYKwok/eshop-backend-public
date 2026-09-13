package com.fsse2406.project.data.user.domainObject.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserPatchRequestData {
    @Pattern(regexp = ".*\\S.*", message = "Nickname must not be blank")
    @Size(max = 80, message = "Nickname must not exceed 80 characters")
    private String nickname;

    @Size(max = 2048, message = "User icon URL must not exceed 2048 characters")
    private String userIcon;

    @Pattern(regexp = ".*\\S.*", message = "Phone number must not be blank")
    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phoneNumber;

    @Pattern(regexp = "(?s).*\\S.*", message = "Address must not be blank")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    private Boolean marketingPreference;
}
