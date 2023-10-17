
package com.jtorleonstudios.libraryferret;

import com.jtorleonstudios.libraryferret.conf.ConfigurationScreen;
import com.google.common.collect.ImmutableMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.util.Map;

public class ModMenuModMenuCompat implements ModMenuApi {
  public ModMenuModMenuCompat() {
  }

  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (parent) -> new ConfigurationScreen(parent, LibraryFerret.MOD_ID, LibraryFerret.CONF);
  }

  public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
    return ImmutableMap.of(LibraryFerret.MOD_ID, (parent) -> new ConfigurationScreen(parent, LibraryFerret.MOD_ID, LibraryFerret.CONF));
  }
}
