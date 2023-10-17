package com.jtorleonstudios.libraryferret.conf;

import com.jtorleonstudios.libraryferret.gui.AbstractScreen;
import com.jtorleonstudios.libraryferret.gui.AbstractUI;
import com.jtorleonstudios.libraryferret.gui.ScrollableTextUI;
import com.jtorleonstudios.libraryferret.utils.Color;
import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationScreen extends AbstractScreen {
  private final Screen lastScreen;
  private boolean hasPropsChanged = false;
  private int listWidth;
  private final Map<String, List<Props>> propsByGroups;
  private Props currentPropsDisplayed = null;
  private List<WidgetStringList.StringEntry> currentListDisplay;
  private List<WidgetStringList.StringEntry> unsortedCurrentListDisplay;
  private String lastFilterText = "";
  private WidgetStringList propsGroupSelectionList;
  private AbstractUI rightSection;
  private TextFieldWidget search;
  private ButtonWidget btnSwapList;
  private ButtonWidget btnDone;
  private int rightSectionWidth;
  private int indexRightSection;
  private final Configuration config;

  public ConfigurationScreen(Screen parentScreen, String modID, Configuration cfg) {
    super(new TranslatableText("gui.bettercommandblock.configscreen.title"));
    this.lastScreen = parentScreen;
    this.propsByGroups = new HashMap<>();
    this.config = cfg;
    this.config.getPropsRegistry().values().forEach((v) -> {
      if (!this.propsByGroups.containsKey(v.getGroup())) {
        ArrayList<Props> l = new ArrayList<>();
        l.add(v);
        this.propsByGroups.put(v.getGroup(), l);
      } else {
        this.propsByGroups.get(v.getGroup()).add(v);
      }
    });
  }

  public void init() {
    this.listWidth = 0;
    this.propsByGroups.forEach((group, propsList) -> {
      int i1 = Math.max(this.listWidth, this.getFontRenderer().getWidth(new TranslatableText("gui.libraryferret.group.name." + group)) + 10);
      if (i1 > this.listWidth) this.listWidth = i1;
      propsList.forEach((props) -> {
        int i2 = Math.max(this.listWidth, this.getFontRenderer().getWidth(new TranslatableText("gui.libraryferret.props.name." + props.getKey())) + 10);
        if (i2 > this.listWidth) this.listWidth = i2;
      });
    });
    this.listWidth = Math.max(Math.min(this.listWidth, this.width / 3), 100);
    this.rightSectionWidth = this.width - this.listWidth - 18;
    int doneButtonWidth = Math.min(this.rightSectionWidth, 200);
    int y = this.height - 20 - 6;
    this.addButton(this.btnDone = new ButtonWidget((this.listWidth + 6 + this.width - doneButtonWidth) / 2, y, doneButtonWidth, 20, new TranslatableText("gui.done"), (b) -> {
      if (this.btnDone.getMessage().equals(new TranslatableText("gui.done"))) {
        this.onClose();
      } else {
        this.onSwapList();
      }

    }));
    this.addButton(new ButtonWidget(6, y, this.listWidth, 20, new TranslatableText("gui.libraryferret.openconfigfile"), (b) -> Util.getOperatingSystem().open(new File(config.getPropertiesPath()))));
    y -= 26;
    this.addButton(this.btnSwapList = new ButtonWidget(6, y, this.listWidth, 20, new TranslatableText("menu.options"), (b) -> this.onSwapList()));
    this.btnSwapList.active = false;
    y -= 21;
    this.search = new TextFieldWidget(this.getFontRenderer(), 7, y, this.listWidth - 2, 14, new TranslatableText("gui.libraryferret.search"));
    this.setInitialFocus(this.search);
    this.propsGroupSelectionList = new WidgetStringList(this.listWidth, 6, this.search.y - this.getLineHeight() - 6);
    this.propsGroupSelectionList.setLeftPos(6);
    if (this.currentPropsDisplayed == null) {
      this.rightSection = new WelcomeSection(this, this.listWidth + 12, 6, this.rightSectionWidth, this.btnDone.y - 12);
    } else {
      this.rightSection = new PropsSection(this, this.currentPropsDisplayed, this.listWidth + 12, 6, this.rightSectionWidth, this.btnDone.y - 12);
    }

    this.children.add(this.search);
    this.children.add(this.propsGroupSelectionList);
    this.indexRightSection = this.children.size();
    this.children.add(this.rightSection);
    super.init();
  }

  public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(mStack);
    this.propsGroupSelectionList.render(mStack, mouseX, mouseY, partialTicks);
    this.rightSection.render(mStack, mouseX, mouseY, partialTicks);
    TranslatableText text = new TranslatableText("gui.libraryferret.search");
    int x = this.propsGroupSelectionList.getLeft() + (this.propsGroupSelectionList.getRight() - this.propsGroupSelectionList.getLeft()) / 2 - this.getFontRenderer().getWidth(text) / 2;
    this.getFontRenderer().draw(mStack, text.asOrderedText(), (float) x, (float) (this.search.y - this.getLineHeight()), 16777215);
    this.search.render(mStack, mouseX, mouseY, partialTicks);
    super.render(mStack, mouseX, mouseY, partialTicks);
  }

  public void tick() {
    this.search.tick();
    this.rightSection.tick();
    if (!this.search.getText().equals(this.lastFilterText)) {
      this.reloadProps();
      this.propsGroupSelectionList.refreshList();
    }

  }

  public void onClose() {
    if (this.client != null) {
      if (this.hasPropsChanged) {
        this.client.openScreen(new ConfirmScreen((b) -> {
          if (b) config.save();
          this.client.openScreen(this.lastScreen);
        }, new TranslatableText("gui.libraryferret.requiresave.title"), new TranslatableText("gui.libraryferret.requiresave.message")));
      } else {
        this.client.openScreen(this.lastScreen);
      }
    }
  }

  private void onSwapList() {
    if (this.currentPropsDisplayed != null) {
      this.setCurrentPropsDisplayed(null);
    }

    this.propsGroupSelectionList.swapList();
  }

  public void resize(MinecraftClient mc, int width, int height) {
    String s = this.search.getText();
    this.init(mc, width, height);
    this.search.setText(s);
    if (!this.search.getText().isEmpty()) {
      this.reloadProps();
    }

  }

  private void reloadProps() {
    this.currentListDisplay = this.unsortedCurrentListDisplay.stream().filter((v) -> stripColor(v.value).toLowerCase().contains(this.search.getText().toLowerCase())).collect(Collectors.toList());
    this.lastFilterText = this.search.getText();
  }

  private void setCurrentPropsDisplayed(@Nullable Props p) {
    this.currentPropsDisplayed = p;
    if (this.currentPropsDisplayed == null) {
      this.rightSection = new WelcomeSection(this, this.listWidth + 12, 6, this.rightSectionWidth, this.btnDone.y - 12);
    } else {
      this.rightSection = new PropsSection(this, this.currentPropsDisplayed, this.listWidth + 12, 6, this.rightSectionWidth, this.btnDone.y - 12);
    }

    this.children.set(this.indexRightSection, this.rightSection);
  }

  private void setCurrentListDisplayed(List<WidgetStringList.StringEntry> v) {
    this.currentListDisplay = Collections.unmodifiableList(v);
    this.unsortedCurrentListDisplay = this.currentListDisplay;
  }

  private class OptionSliderWidget extends SliderWidget {
    private final Props props;

    public OptionSliderWidget(int x, int y, int w, Props props) {
      super(x, y, w, 20, new TranslatableText(""), 0.0);
      this.props = props;
      double p = MathHelper.clamp(ConfigurationScreen.this.config.getIntOrDefault(props), 0.0, 100.0);
      p -= 0.0;
      this.value = p / (100.0 - 0.0);
      this.updateMessage();
    }

    protected void updateMessage() {
      this.setMessage((new TranslatableText("gui.libraryferret.props.name." + this.props.getKey())).append(": ").append(new LiteralText(String.valueOf(ConfigurationScreen.this.config.getIntOrDefault(this.props)))));
    }

    protected void applyValue() {
      int v = (int) MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), 0.0, 100.0);
      ConfigurationScreen.this.config.set(this.props, v);
      if (!ConfigurationScreen.this.hasPropsChanged) ConfigurationScreen.this.hasPropsChanged = true;
    }
  }

  private class OptionToggleWidget extends ButtonWidget {
    private final Props props;

    public OptionToggleWidget(int x, int y, int w, Props props) {
      super(x, y, w, 20, new TranslatableText(""), null);
      this.props = props;
      boolean v = ConfigurationScreen.this.config.getBoolOrDefault(props);
      this.setMessage(new TranslatableText(v ? "gui.libraryferret.true" : "gui.libraryferret.false"));
    }

    public void onPress() {
      Configuration configSingleton = ConfigurationScreen.this.config;
      boolean v = !configSingleton.getBoolOrDefault(this.props);
      this.setMessage(new TranslatableText(v ? "gui.libraryferret.true" : "gui.libraryferret.false"));
      configSingleton.set(this.props, v);
      if (!ConfigurationScreen.this.hasPropsChanged) ConfigurationScreen.this.hasPropsChanged = true;
    }
  }

  private class OptionKeyBindWidget extends ButtonWidget {
    private final Props props;
    private boolean isEdited;
    private InputUtil.Key keyInput;

    public OptionKeyBindWidget(int x, int y, int w, Props props) {
      super(x, y, w, 20, new TranslatableText(""), null);
      this.props = props;
      this.isEdited = false;
      this.keyInput = Type.KEYSYM.createFromCode(ConfigurationScreen.this.config.getIntOrDefault(props));
      this.setMessage(new LiteralText(this.keyInput.getLocalizedText().getString()));
    }

    public void onPress() {
      this.isEdited = !this.isEdited;
      if (this.isEdited) {
        this.setMessage((new LiteralText(">")).append(this.keyInput.getLocalizedText().getString()).append("<"));
      } else {
        this.setMessage(new LiteralText(this.keyInput.getLocalizedText().getString()));
      }

    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.isEdited) {
        this.isEdited = false;
        this.keyInput = InputUtil.fromKeyCode(keyCode, scanCode);
        this.setMessage(new LiteralText(this.keyInput.getLocalizedText().getString()));
        ConfigurationScreen.this.config.set(this.props, this.keyInput.getCode());
        if (!ConfigurationScreen.this.hasPropsChanged) {
          ConfigurationScreen.this.hasPropsChanged = true;
        }

        return true;
      } else {
        return super.keyPressed(keyCode, scanCode, modifiers);
      }
    }
  }

  private class OptionItemStackTextWidget extends OptionTextWidget {
    public OptionItemStackTextWidget(int x, int y, int w, Props props) {
      super(x, y, w, props);
      this.setChangedListener(this::valueChanged);
    }

    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
      super.render(matrix, mouseX, mouseY, partialTicks);
      int quadX = this.x - 6 - this.getHeight();
      int quadY = this.y;
      fill(matrix, quadX - 1, quadY - 1, this.x - 6 + 1, this.y + this.getHeight() + 1, this.isFocused() ? -1 : -6250336);
      fill(matrix, quadX, quadY, this.x - 6, this.y + this.getHeight(), Color.BLACK);
      Identifier test = new Identifier(this.getText());
      ItemStack itemstack = new ItemStack(Registry.ITEM.containsId(test) ? Registry.ITEM.get(test) : Items.BARRIER);
      ConfigurationScreen.this.itemRenderer.renderInGuiWithOverrides(itemstack, quadX + 2, quadY + 2);
      ConfigurationScreen.this.itemRenderer.renderGuiItemOverlay(ConfigurationScreen.this.getFontRenderer(), itemstack, quadX + 2, quadY + 2);
    }

    private void valueChanged(String value) {
      if (Registry.ITEM.containsId(new Identifier(this.getText()))) {
        ConfigurationScreen.this.config.set(this.props, value);
        if (!ConfigurationScreen.this.hasPropsChanged) {
          ConfigurationScreen.this.hasPropsChanged = true;
        }
      }

    }
  }

  private class OptionColorTextWidget extends OptionTextWidget {
    public OptionColorTextWidget(int x, int y, int w, Props props) {
      super(x, y, w, props);
      this.setChangedListener(this::valueChanged);
    }

    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
      super.render(matrix, mouseX, mouseY, partialTicks);
      int quadX = this.x - 6 - this.getHeight();
      int quadY = this.y;
      fill(matrix, quadX - 1, quadY - 1, this.x - 6 + 1, this.y + this.getHeight() + 1, this.isFocused() ? -1 : -6250336);
      fill(matrix, quadX, quadY, this.x - 6, this.y + this.getHeight(), Color.toHex(this.getText()));
    }

    private void valueChanged(String value) {
      ConfigurationScreen.this.config.set(this.props, Color.isRGBorRGBA(value) ? Color.toString(Color.toRGBA(value)) : Color.toString(0, 0, 0, 255));
      if (!ConfigurationScreen.this.hasPropsChanged) {
        ConfigurationScreen.this.hasPropsChanged = true;
      }

    }
  }

  private class OptionTextWidget extends TextFieldWidget {
    protected final Props props;

    public OptionTextWidget(int x, int y, int w, Props props) {
      super(ConfigurationScreen.this.getFontRenderer(), x, y, w, 20, new TranslatableText(""));
      this.props = props;
      this.setText(ConfigurationScreen.this.config.getStringOrDefault(props));
      this.setChangedListener(this::valueChanged);
    }

    private void valueChanged(String value) {
      ConfigurationScreen.this.config.set(this.props, value);
      if (!ConfigurationScreen.this.hasPropsChanged) {
        ConfigurationScreen.this.hasPropsChanged = true;
      }

    }
  }

  protected class WidgetStringList extends AlwaysSelectedEntryListWidget<WidgetStringList.StringEntry> {

    public WidgetStringList(int listWidth, int top, int bottom) {
      super(ConfigurationScreen.this.client, listWidth, ConfigurationScreen.this.height, top, bottom, ConfigurationScreen.this.getLineHeight() + 8);
      this.swapList();
    }

    public void setSelected(StringEntry entry) {
      StringEntry lastSelected = this.getSelected();
      super.setSelected(entry);
      if (entry == null) {
        if (ConfigurationScreen.this.rightSection instanceof WelcomeSection) {
          ((WelcomeSection) ConfigurationScreen.this.rightSection).weclome.setTexts(I18n.translate("gui.libraryferret.config_welcome"));
        }

      } else {
        if (!ConfigurationScreen.this.btnSwapList.active) {
          ConfigurationScreen.this.btnSwapList.active = true;
        }

        if (ConfigurationScreen.this.propsByGroups.containsKey(entry.keyGroup)) {
          if (ConfigurationScreen.this.currentPropsDisplayed != null) {
            ConfigurationScreen.this.setCurrentPropsDisplayed(null);
          }

          if (entry.equals(lastSelected)) {
            this.swapList();
          } else if (ConfigurationScreen.this.rightSection instanceof WelcomeSection) {
            ((WelcomeSection) ConfigurationScreen.this.rightSection).weclome.setTexts(I18n.translate("gui.libraryferret.group.description." + entry.keyGroup));
          }
        } else if (ConfigurationScreen.this.config.getPropsRegistry().containsKey(entry.keyProps)) {
          ConfigurationScreen.this.setCurrentPropsDisplayed(ConfigurationScreen.this.config.getPropsRegistry().get(entry.keyProps));
        } else {
          System.err.println("not implemented 565 :" + entry.value);
        }

      }
    }

    public void swapList() {
      this.clearEntries();
      if (this.getSelected() != null && ConfigurationScreen.this.propsByGroups.containsKey(this.getSelected().keyGroup)) {
        ArrayList<StringEntry> l = new ArrayList<>();
        ConfigurationScreen.this.propsByGroups.get(this.getSelected().keyGroup).forEach((v) -> {
          l.add(new StringEntry("gui.libraryferret.props.name." + v.getKey(), null, v.getKey()));
          this.addEntry(new StringEntry("gui.libraryferret.props.name." + v.getKey(), null, v.getKey()));
        });
        ConfigurationScreen.this.setCurrentListDisplayed(l);
        ConfigurationScreen.this.btnSwapList.setMessage(new TranslatableText("gui.back"));
        ConfigurationScreen.this.btnDone.setMessage(new TranslatableText("gui.back"));
        ConfigurationScreen.this.btnSwapList.active = true;
      } else {
        this.setSelected(null);
        ArrayList<StringEntry> l = new ArrayList<>();
        ConfigurationScreen.this.propsByGroups.keySet().forEach((k) -> {
          l.add(new StringEntry("gui.libraryferret.group.name." + k, k, null));
          this.addEntry(new StringEntry("gui.libraryferret.group.name." + k, k, null));
        });
        ConfigurationScreen.this.setCurrentListDisplayed(l);
        ConfigurationScreen.this.btnSwapList.setMessage(new TranslatableText("menu.options"));
        ConfigurationScreen.this.btnDone.setMessage(new TranslatableText("gui.done"));
        ConfigurationScreen.this.btnSwapList.active = false;
      }
      super.setSelected(null);
    }

    public void refreshList() {
      this.clearEntries();
      ConfigurationScreen.this.currentListDisplay.forEach(this::addEntry);
    }

    protected int getScrollbarPositionX() {
      return ConfigurationScreen.this.listWidth;
    }

    public int getRowWidth() {
      return ConfigurationScreen.this.listWidth;
    }

    protected void renderBackground(MatrixStack stack) {
      ConfigurationScreen.this.renderBackground(stack);
    }

    protected boolean isFocused() {
      return ConfigurationScreen.this.getFocused() == this;
    }

    public int getLeft() {
      return this.left;
    }

    public int getRight() {
      return this.right;
    }

    public final class StringEntry extends AlwaysSelectedEntryListWidget.Entry<StringEntry> {
      private final String value;
      private final String keyGroup;
      private final String keyProps;

      public StringEntry(String value, String keyGroup, String keyProps) {
        this.value = value;
        this.keyGroup = keyGroup;
        this.keyProps = keyProps;
      }

      public void render(MatrixStack mStack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
        TranslatableText n = new TranslatableText(this.value);
        TextRenderer font = ConfigurationScreen.this.getFontRenderer();
        font.draw(mStack, Language.getInstance().reorder(StringVisitable.concat(font.trimToWidth(n, ConfigurationScreen.this.listWidth))), (float) (left + 3), (float) (top + 2), 16777215);
      }

      public boolean mouseClicked(double a, double b, int c) {
        if (c == 0) {
          WidgetStringList.this.setSelected(this);
          return true;
        } else {
          return false;
        }
      }

      public int hashCode() {
        int result = 1;
        result = 31 * result + this.getEnclosingInstance().hashCode();
        result = 31 * result + Objects.hash(this.keyGroup, this.keyProps, this.value);
        return result;
      }

      public boolean equals(Object obj) {
        if (this == obj) {
          return true;
        } else if (obj == null) {
          return false;
        } else if (this.getClass() != obj.getClass()) {
          return false;
        } else {
          StringEntry other = (StringEntry) obj;
          if (!this.getEnclosingInstance().equals(other.getEnclosingInstance())) {
            return false;
          } else {
            return Objects.equals(this.keyGroup, other.keyGroup) && Objects.equals(this.keyProps, other.keyProps) && Objects.equals(this.value, other.value);
          }
        }
      }

      private WidgetStringList getEnclosingInstance() {
        return WidgetStringList.this;
      }
    }
  }

  private class PropsSection extends AbstractUI {
    private final List<Element> children = Lists.newArrayList();
    private final ButtonWidget btnReset;
    private final ScrollableTextUI description;
    private final Props props;
    private final TranslatableText textLine1;
    private final TranslatableText textLine2;
    private final TranslatableText textLine3;
    private final TranslatableText textLine4;
    private TextFieldWidget input = null;
    private ButtonWidget inputButton = null;
    private SliderWidget slide = null;

    public PropsSection(AbstractScreen parent, Props propsDisplayed, int x, int y, int w, int h) {
      super(parent, x, y, w, h);
      this.props = propsDisplayed;
      this.textLine1 = new TranslatableText("gui.libraryferret.props.title_value", new TranslatableText("gui.libraryferret.props.name." + this.props.getKey()));
      this.textLine2 = new TranslatableText("gui.libraryferret.props.key_value", this.props.getKey());
      this.textLine3 = new TranslatableText("gui.libraryferret.props.default_value", this.props.getDefaultValue());
      this.textLine4 = new TranslatableText("gui.libraryferret.props.type_value", this.props.getType());
      int numberLinesBeforeInput = 4;
      int inputWidth = this.width / 2;
      int inputX = this.x + 6;
      int inputY = 12 + (ConfigurationScreen.this.getLineHeight() + 6) * numberLinesBeforeInput;
      if (this.props.getType().contains("boolean")) {
        this.inputButton = ConfigurationScreen.this.new OptionToggleWidget(inputX, inputY, inputWidth, this.props);
        this.children.add(this.inputButton);
      } else if (!this.props.getType().contains("number") && !this.props.getType().contains("int") && !this.props.getType().contains("double") && !this.props.getType().contains("float") && !this.props.getType().contains("long")) {
        if (!this.props.getType().contains("item") && !this.props.getType().contains("block")) {
          if (this.props.getType().contains("color")) {
            inputX += 26;
            this.input = ConfigurationScreen.this.new OptionColorTextWidget(inputX, inputY, inputWidth, this.props);
            this.children.add(this.input);
          } else if (!this.props.getType().contains("key") && !this.props.getType().contains("input")) {
            this.input = ConfigurationScreen.this.new OptionTextWidget(inputX, inputY, inputWidth, this.props);
            if (this.props.getType().contains("char")) {
              this.input.setMaxLength(1);
            }

            this.children.add(this.input);
          } else {
            this.inputButton = ConfigurationScreen.this.new OptionKeyBindWidget(inputX, inputY, inputWidth, this.props);
            this.children.add(this.inputButton);
          }
        } else {
          inputX += 26;
          this.input = ConfigurationScreen.this.new OptionItemStackTextWidget(inputX, inputY, inputWidth, this.props);
          this.children.add(this.input);
        }
      } else {
        this.slide = ConfigurationScreen.this.new OptionSliderWidget(inputX, inputY, inputWidth, this.props);
        this.children.add(this.slide);
      }

      int btnResetX = inputX + inputWidth + 6;
      this.btnReset = new ButtonWidget(btnResetX, inputY, this.right - btnResetX - 6, 20, new TranslatableText("controls.reset"), (e) -> {
        ConfigurationScreen.this.config.reset(this.props);
        ConfigurationScreen.this.setCurrentPropsDisplayed(this.props);
        if (!ConfigurationScreen.this.hasPropsChanged) {
          ConfigurationScreen.this.hasPropsChanged = true;
        }

      });
      this.children.add(this.btnReset);
      int descriptionY = inputY + 20 + 6;
      this.description = new ScrollableTextUI(ConfigurationScreen.this, I18n.translate("gui.libraryferret.props.description." + this.props.getKey()), x, descriptionY, w, this.bottom - descriptionY);
      this.children.add(this.description);
    }

    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground();
      float y = (float) (this.y + 6);
      ConfigurationScreen.this.getFontRenderer().drawWithShadow(matrix, this.textLine1, (float) (this.left + 6), y, 16777215);
      y += (float) (ConfigurationScreen.this.getLineHeight() + 6);
      ConfigurationScreen.this.getFontRenderer().drawWithShadow(matrix, this.textLine2, (float) (this.left + 6), y, 16777215);
      y += (float) (ConfigurationScreen.this.getLineHeight() + 6);
      ConfigurationScreen.this.getFontRenderer().drawWithShadow(matrix, this.textLine3, (float) (this.left + 6), y, 16777215);
      y += (float) (ConfigurationScreen.this.getLineHeight() + 6);
      ConfigurationScreen.this.getFontRenderer().drawWithShadow(matrix, this.textLine4, (float) (this.left + 6), y, 16777215);
      if (this.input != null) this.input.render(matrix, mouseX, mouseY, partialTicks);
      else if (this.inputButton != null) this.inputButton.render(matrix, mouseX, mouseY, partialTicks);
      else if (this.slide != null) this.slide.render(matrix, mouseX, mouseY, partialTicks);
      this.btnReset.render(matrix, mouseX, mouseY, partialTicks);
      this.description.render(matrix, mouseX, mouseY, partialTicks);
    }

    public void tick() {
      if (this.input != null) this.input.tick();
    }

    public List<? extends Element> children() {
      return this.children;
    }
  }

  private final class WelcomeSection extends AbstractUI {
    private final ArrayList<Element> children = new ArrayList<>();
    private final ScrollableTextUI weclome;

    public WelcomeSection(AbstractScreen parent, int x, int y, int w, int h) {
      super(parent, x, y, w, h);
      this.weclome = new ScrollableTextUI(ConfigurationScreen.this, I18n.translate("gui.libraryferret.config_welcome"), x, 6, w, this.bottom - 6);
      this.children.add(this.weclome);
    }

    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground();
      this.weclome.render(matrix, mouseX, mouseY, partialTicks);
    }

    public List<? extends Element> children() {
      return this.children;
    }
  }
}
