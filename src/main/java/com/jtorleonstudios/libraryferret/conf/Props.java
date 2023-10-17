package com.jtorleonstudios.libraryferret.conf;

@SuppressWarnings("unused")
public class Props {
  private final String key;
  private final String type;
  private final String group;
  private final String name;
  private final String defaultValue;

  private Props(String type, String group, String name, String defaultValue) {
    this.type = type.toLowerCase();
    this.group = group.toLowerCase();
    this.name = name.toLowerCase();
    this.key = type + "." + group + "." + name;
    this.defaultValue = defaultValue;
  }

  public static Props create(String type, String group, String name, String defaultValue) {
    return new Props(type, group, name, defaultValue);
  }

  public static Props create(String group, String name, String defaultValue) {
    return create("string", group, name, defaultValue);
  }

  public static Props create(String group, String name, char defaultValue) {
    return create("char", group, name, defaultValue + "");
  }

  public static Props create(String group, String name, boolean defaultValue) {
    return create("boolean", group, name, defaultValue ? "true" : "false");
  }

  public static Props create(String group, String name, int defaultValue) {
    return create("int", group, name, Integer.toString(defaultValue));
  }

  public static Props create(String group, String name, double defaultValue) {
    return create("double", group, name, Double.toString(defaultValue));
  }

  public static Props create(String group, String name, float defaultValue) {
    return create("float", group, name, Float.toString(defaultValue));
  }

  public static Props create(String group, String name, long defaultValue) {
    return create("long", group, name, Long.toString(defaultValue));
  }

  public String getKey() {
    return this.key;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public String getType() {
    return this.type;
  }

  public String getGroup() {
    return this.group;
  }

  public String getName() {
    return this.name;
  }
}
