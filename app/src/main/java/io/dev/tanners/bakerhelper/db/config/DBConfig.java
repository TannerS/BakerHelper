package io.dev.tanners.bakerhelper.db.config;

public class DBConfig {
    public final static String TABLE_NAME_RECIPE = "recipes";
    public final static String ORDER_BY_FIELD_RECIPE = "id";
    public final static String GET_ALL_RECIPES_QUERY = "SELECT * FROM" + " " + TABLE_NAME_RECIPE + " " + "ORDER BY" + " " + ORDER_BY_FIELD_RECIPE;
    public final static String GET_RECIPE_BY_ID_QUERY = "SELECT * FROM" + " " + TABLE_NAME_RECIPE + " " + "WHERE id" + " = " + ":id";
    public static final String DATABASE_NAME = "baker";
    public static final int DATABASE_VERISON = 1;
}
