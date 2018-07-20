package io.dev.tanners.bakerhelper.aac.db.config;

public class DBConfig {
    public final static String TABLE_NAME_RECIPES = "recipes";
    public final static String ORDER_BY_FIELD = "name";
    public final static String GET_ALL_RECIPE_QUERY = "SELECT * FROM" + " " + TABLE_NAME_RECIPES + " " + "ORDER BY" + " " + ORDER_BY_FIELD;
    public final static String GET_RECIPE_BY_ID_QUERY = "SELECT * FROM" + " " + TABLE_NAME_RECIPES + " " + "WHERE id" + " = " + ":id";
    public static final String DATABASE_NAME = "yummy_recipes";
    public static final int DATABASE_VERISON = 1;
}
