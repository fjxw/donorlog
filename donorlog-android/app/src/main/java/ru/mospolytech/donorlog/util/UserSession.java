package ru.mospolytech.donorlog.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static final String PREF_NAME = "donorlog_user_session";
    private static final String KEY_LOGGED_IN = "key_logged_in";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_BLOOD_GROUP = "key_blood_group";
    private static final String KEY_RH_FACTOR = "key_rh_factor";
    private static final String KEY_REGION = "key_region";
    private static final String KEY_IS_MALE = "key_is_male";

    public static final String REGION_MOSCOW = "г. Москва";
    public static final String REGION_OTHER = "Другой регион РФ";

    private static volatile UserSession INSTANCE;
    private final SharedPreferences prefs;

    private UserSession(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static UserSession getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UserSession.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserSession(context);
                }
            }
        }
        return INSTANCE;
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, true); // default true for seamless initial experience
    }

    public void setLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply();
    }

    public String getName() {
        String name = prefs.getString(KEY_NAME, null);
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        if (isLoggedIn()) {
            return "Ряховский Алексей Александрович";
        }
        return "Гость";
    }

    public void setName(String name) {
        prefs.edit().putString(KEY_NAME, name).apply();
    }

    public String getEmail() {
        String email = prefs.getString(KEY_EMAIL, null);
        if (email != null && !email.trim().isEmpty()) {
            return email;
        }
        if (isLoggedIn()) {
            return "alexey.ryakhovsky@example.com";
        }
        return "";
    }

    public void setEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public void logout() {
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .putString(KEY_NAME, "")
                .putString(KEY_EMAIL, "")
                .apply();
    }

    public String getBloodGroup() {
        return prefs.getString(KEY_BLOOD_GROUP, "A(II)");
    }

    public void setBloodGroup(String bloodGroup) {
        prefs.edit().putString(KEY_BLOOD_GROUP, bloodGroup).apply();
    }

    public String getRhFactor() {
        return prefs.getString(KEY_RH_FACTOR, "Rh+");
    }

    public void setRhFactor(String rhFactor) {
        prefs.edit().putString(KEY_RH_FACTOR, rhFactor).apply();
    }

    public String getFullBloodType() {
        return getBloodGroup() + " " + getRhFactor();
    }

    public String getRegion() {
        return prefs.getString(KEY_REGION, REGION_MOSCOW);
    }

    public void setRegion(String region) {
        prefs.edit().putString(KEY_REGION, region).apply();
    }

    public boolean isMoscowRegion() {
        String reg = getRegion();
        return reg != null && reg.toLowerCase().contains("москв");
    }

    public boolean isMale() {
        return prefs.getBoolean(KEY_IS_MALE, true);
    }

    public void setMale(boolean male) {
        prefs.edit().putBoolean(KEY_IS_MALE, male).apply();
    }

    public void saveProfile(String name, String email, String bloodGroup, String rhFactor, String region, boolean isMale) {
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_BLOOD_GROUP, bloodGroup)
                .putString(KEY_RH_FACTOR, rhFactor)
                .putString(KEY_REGION, region)
                .putBoolean(KEY_IS_MALE, isMale)
                .apply();
    }
}
