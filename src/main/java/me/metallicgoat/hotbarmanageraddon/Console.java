package me.metallicgoat.hotbarmanageraddon;

public class Console {

  public static void printWarn(String warn) {
    HotbarManagerPlugin.getInstance().getLogger().warning(warn);
  }

  public static void printInfo(String info) {
    HotbarManagerPlugin.getInstance().getLogger().info(info);
  }

  public static void printInfo(String... strings) {
    for (String s : strings)
      printInfo(s);
  }

  private static void printInfo(String tag, String msg) {
    printInfo("(" + tag + ") " + msg);
  }

  private static void printWarn(String tag, String msg) {
    printWarn("(" + tag + ") " + msg);
  }

  public static void printConfigWarn(String warn, String config) {
    printWarn("[config][" + config + "]", warn);
  }

  public static void printConfigInfo(String info, String config) {
    printInfo("[config][" + config + "]", info);
  }
}
