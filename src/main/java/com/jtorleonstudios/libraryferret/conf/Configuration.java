
package com.jtorleonstudios.libraryferret.conf;

import com.jtorleonstudios.libraryferret.LibraryFerret;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("unused")
public class Configuration {
  // upgrade this number for force reset file in client
  private static final String FORCE_RESET = "1";
  private final String identifier;
  private final Properties properties;
  private final String propertiesPath;
  private final Map<String, Props> propsRegistry;

  public Configuration(String mod_id, Props... props) {
    this.propertiesPath = FabricLoader.getInstance().getConfigDir().toString() + "/" + mod_id + "_" + FORCE_RESET + ".properties";
    this.identifier = mod_id;

    this.propsRegistry = new HashMap<>();
    this.properties = new Properties();

    Properties oldUserProperties = new Properties();
    boolean userConfigFileExists = new File(this.propertiesPath).exists();
    if (userConfigFileExists) {
      try (FileInputStream fs = new FileInputStream(this.propertiesPath)) {
        oldUserProperties.load(fs);
      } catch (IOException e) {
        LibraryFerret.LOGGER.warn("Failed to load user properties, error message: " + e.getMessage());
      }
    }

    for (Props v : props) {

      if (this.propsRegistry.containsKey(v.getKey()))
        throw new IllegalArgumentException("Duplicate keys in map, register Props is not possible for " + mod_id + ", Props.key: " + v.getKey());

      this.propsRegistry.put(v.getKey(), v);

      this.properties.setProperty(v.getKey(),
              userConfigFileExists
                      // try get config value or use default value
                      ? oldUserProperties.getProperty(v.getKey(), v.getDefaultValue())
                      // use default value
                      : v.getDefaultValue());
    }

    this.save();
    if (!userConfigFileExists)
      LibraryFerret.LOGGER.info("Welcome! " + this.propertiesPath + " created and initialized with default value");
  }

  public void save() {
    try (FileWriter fw = new FileWriter(this.propertiesPath)) {
      this.properties.store(fw, "Need help? Ask on Discord or Github/Gitlab <3 (ps: it is possible that this file contains nothing it's normal)");
    } catch (IOException e) {
      LibraryFerret.LOGGER.error("Failed to store " + this.identifier + ".properties, error message: " + e.getMessage());
    }
  }

  public void reset(Props props) {
    this.set(props, props.getDefaultValue());
  }

  public void set(Props props, String newValue) {
    this.properties.setProperty(props.getKey(), newValue);
  }

  public void set(Props props, char newValue) {
    this.set(props, newValue + "");
  }

  public void set(Props props, boolean newValue) {
    this.set(props, newValue ? "true" : "false");
  }

  public void set(Props props, int newValue) {
    this.set(props, Integer.toString(newValue));
  }

  public void set(Props props, double newValue) {
    this.set(props, Double.toString(newValue));
  }

  public void set(Props props, float newValue) {
    this.set(props, Float.toString(newValue));
  }

  public void set(Props props, long newValue) {
    this.set(props, Long.toString(newValue));
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public Map<String, Props> getPropsRegistry() {
    return this.propsRegistry;
  }

  public String getPropertiesPath() {
    return this.propertiesPath;
  }

  public String getStringOrDefault(Props props) {
    return this.properties.getProperty(props.getKey(), props.getDefaultValue());
  }

  public char getCharOrDefault(Props props) {
    return this.getStringOrDefault(props).charAt(0);
  }

  public boolean getBoolOrDefault(Props props) {
    return "true".equalsIgnoreCase(this.getStringOrDefault(props));
  }

  public int getIntOrDefault(Props props) {
    try {
      return Integer.parseInt(this.getStringOrDefault(props));
    } catch (Exception unused) {
      try {
        return Integer.parseInt(props.getDefaultValue());
      } catch (Exception e) {
        LibraryFerret.LOGGER.warn("Failed to get properties (int):" + props.getKey());
        LibraryFerret.LOGGER.error(e);
        return 0;
      }
    }
  }

  public double getDoubleOrDefault(Props props) {
    try {
      return Double.parseDouble(this.getStringOrDefault(props));
    } catch (Exception unused) {
      try {
        return Double.parseDouble(props.getDefaultValue());
      } catch (Exception e) {
        LibraryFerret.LOGGER.warn("Failed to get properties (double):" + props.getKey());
        LibraryFerret.LOGGER.error(e);
        return 0.0;
      }
    }
  }

  public float getFloatOrDefault(Props props) {
    try {
      return Float.parseFloat(this.getStringOrDefault(props));
    } catch (Exception unused) {
      try {
        return Float.parseFloat(props.getDefaultValue());
      } catch (Exception e) {
        LibraryFerret.LOGGER.warn("Failed to get properties (float):" + props.getKey());
        LibraryFerret.LOGGER.error(e);
        return 0.0F;
      }
    }
  }

  public long getLongOrDefault(Props props) {
    try {
      return Long.parseLong(this.getStringOrDefault(props));
    } catch (Exception unused) {
      try {
        return Long.parseLong(props.getDefaultValue());
      } catch (Exception e) {
        LibraryFerret.LOGGER.warn("Failed to get properties (long):" + props.getKey());
        LibraryFerret.LOGGER.error(e);
        return 0L;
      }
    }
  }
}
