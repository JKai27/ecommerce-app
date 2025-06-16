package shopeazy.com.ecommerceapp.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import shopeazy.com.ecommerceapp.enums.Gender;
import shopeazy.com.ecommerceapp.service.serviceImplementation.ValidPassword;

@Data
public class CreateUserRequest {
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$", message = "Username must be 3-20 characters long and can contain letters, numbers, hyphens, and underscores.")
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @ValidPassword
    private String password;

    @Size(min = 3, max = 25, message = "firstname must be between 3 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z]+(?:[ '-][a-zA-Z]+)*$", message = "First name must contain only letters, spaces, apostrophes, or hyphens.")
    private String firstName;

    @Size(min = 3, max = 25, message = "lastname must be between 3 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z]+(?:[ '-][a-zA-Z]+)*$", message = "Last name must contain only letters, spaces, apostrophes, or hyphens.")
    private String lastName;

    @NotNull(message = "Gender is required.")
    private Gender gender;
}
