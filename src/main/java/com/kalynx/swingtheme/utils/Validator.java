package com.kalynx.swingtheme.utils;

/**
 * Functional interface for validating input values.
 */
@FunctionalInterface
public interface Validator {

    /**
     * Validates the input value.
     *
     * @param value the value to validate
     * @return {@link ValidationResult} containing validation status and optional error message
     */
    ValidationResult validate(String value);

    /**
     * Result of a validation operation.
     */
    class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;

        private ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        /**
         * Returns a successful validation result.
         *
         * @return valid result
         */
        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        /**
         * Returns a failed validation result with an error message.
         *
         * @param errorMessage description of the validation failure
         * @return invalid result
         */
        public static ValidationResult invalid(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }

        /**
         * Returns whether the validation passed.
         *
         * @return true if valid
         */
        public boolean isValid() {
            return isValid;
        }

        /**
         * Returns the error message, or {@code null} if the result is valid.
         *
         * @return error message or null
         */
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

