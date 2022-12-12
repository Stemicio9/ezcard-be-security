package com.metra.ezcardbesecurity.utils;

public class ProfilePaths {

    private ProfilePaths() {}
    private static final String PROTECTED_BASE_PATH = "protected/profile";
    private static final String PUBLIC_BASE_PATH = "public/profile";
    private static final String GET_PROFILE = PROTECTED_BASE_PATH + "/get";
    private static final String GET_PROFILE_PUBLIC = PUBLIC_BASE_PATH + "/get";
    private static final String UPDATE_PROFILE = PROTECTED_BASE_PATH + "/update";



    public static final String UPDATE_PROFILE_CONTAINER = UPDATE_PROFILE + "/profile";
    public static final String UPDATE_PRESENTATION = UPDATE_PROFILE + "/presentation";
    public static final String UPDATE_PARTNER = UPDATE_PROFILE + "/partner";
    public static final String UPDATE_GALLERY = UPDATE_PROFILE + "/gallery";
    public static final String UPDATE_CONTACTS = UPDATE_PROFILE + "/contacts";
    public static final String UPDATE_COMPANIES = UPDATE_PROFILE + "/companies";
    public static final String UPDATE_SOCIAL = UPDATE_PROFILE + "/social";



    public static final String GET_PROFILE_CONTAINER = GET_PROFILE + "/profile";
    public static final String GET_PRESENTATION = GET_PROFILE + "/presentation";
    public static final String GET_PARTNER = GET_PROFILE + "/partner";
    public static final String GET_GALLERY = GET_PROFILE + "/gallery";
    public static final String GET_CONTACTS = GET_PROFILE + "/contacts";
    public static final String GET_COMPANIES = GET_PROFILE + "/companies";
    public static final String GET_SOCIAL = GET_PROFILE + "/social";

    public static final String SERVE_FILE = GET_PROFILE + "/file";
    public static final String GET_PROFILE_SHOWN = GET_PROFILE_PUBLIC + "/profile-shown/{id}";







}
