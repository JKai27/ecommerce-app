package shopeazy.com.ecommerceapp.service.serviceImplementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private final Logger logger = LoggerFactory.getLogger(PasswordValidator.class);
    private static final String PASSWORD_PATTERN =
            "^(?!.*[\\s%$§°^;`\"#€~])(?!.*\\bUSERNAME\\b)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@!%*?&])[A-Za-z\\d@!%*?&]{6,}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        logger.info("PasswordValidator is being executed for password: {}", password);

        if (password == null || password.trim().isEmpty()) {
            logger.error("Password validation failed: Password is null or empty");

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password cannot be empty.")
                    .addConstraintViolation();

            return false;
        }
        boolean matchesPattern = password.matches(PASSWORD_PATTERN);

        if (!matchesPattern) {
            logger.error("Password validation failed: Password does not match required pattern");
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password does not match required pattern")
                    .addConstraintViolation();
        } else {

            logger.info("Password: {} | Matches pattern: {}", password, true);
        }

        return matchesPattern;
    }
}
