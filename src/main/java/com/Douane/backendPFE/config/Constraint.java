package com.Douane.backendPFE.config;

public class Constraint {
    /*Password Constraints * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * /
     * Front end: /(?=^.{8,}$)((?=.*\d)|(?=.*\W+))(?![.\n])(?=.*[A-Z])(?=.*[a-z]).*$/
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public final static String PASSWORD_PATTERN = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$";
    public final static String PASSWORD_PATTERN_MESSAGE = "Valid passwords must be a mix of upper case, lower case, number/special characters!";
    public final static int PASSWORD_MIN = 6;
    public final static int PASSWORD_MAX = 20;

    /*Firstname Constraints * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * /
     * Front end: /^([A-ZΑ-Ω]){1}[^0-9]*$/
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public final static String FIRSTNAME_PATTERN = "^([A-ZΑ-Ω]){1}[^0-9]*$";
    public final static String FIRSTNAME_PATTERN_MESSAGE = "A valid firstname must consist only of alphabet characters!";
    public final static int FIRSTNAME_MIN = 3;
    public final static int FIRSTNAME_MAX = 20;

    /*Lastname Constraints * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * /
     * Front end: /^([A-ZΑ-Ω]){1}[^0-9]*$/
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public final static String LASTNAME_PATTERN = "^([A-ZΑ-Ω]){1}[^0-9]*$";
    public final static String LASTNAME_PATTERN_MESSAGE = "A valid lastname must consist only of alphabet characters!";
    public final static int LASTNAME_MIN = 3;
    public final static int LASTNAME_MAX = 20;
}
