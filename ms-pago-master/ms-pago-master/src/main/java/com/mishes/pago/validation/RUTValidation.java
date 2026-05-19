package com.mishes.pago.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RUTValidation implements ConstraintValidator<RUT, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true; // dejar que @NotNull haga la validación

        boolean tieneGuion = false;
        for (char c : value.toCharArray())
            if (c == '-')
                if (!tieneGuion)
                    tieneGuion = true;
                else
                    return false;
        if (!tieneGuion)
            return false;

        String rutRef = value.substring(0, value.indexOf("-"));
        String digitVerifierRef = value.substring(value.indexOf("-") + 1);
        if (digitVerifierRef.length() != 1)
            return false;

        try {
            Integer.parseInt(rutRef);
        } catch (NumberFormatException e) {
            return false;
        }
        return getCalculatedDigitChar(rutRef) == Character.toUpperCase(digitVerifierRef.charAt(0));
    }

    private char getCalculatedDigitChar(String rutRef) {
        int digitSum = 0;
        int multiplier = 2;
        char[] rutDigitsArray = rutRef.toCharArray();

        for (int i = 0; i < rutDigitsArray.length; i++) {
            if (multiplier > 7)
                multiplier = 2;
            char d = rutDigitsArray[rutDigitsArray.length - 1 - i];
            digitSum += (d - '0') * multiplier;
            multiplier++;
        }

        int calculatedDigit = 11 - (digitSum % 11);
        return (char)(switch (calculatedDigit) {
            case 11 -> '0';
            case 10 -> 'K';
            default -> calculatedDigit + '0';
        });
    }

}
