package edu.ucr.cs.riple.core;

import com.google.common.base.Preconditions;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Config {
  public final boolean bailout;
  public final boolean optimized;
  public final boolean lexicalPreservationEnabled;
  public final boolean chain;
  public final boolean useCache;
  public final Path dir;
  public final Path nullAwayConfigPath;
  public final Path cssConfigPath;
  public final String buildCommand;
  public final String nullableAnnot;
  public final String initializerAnnot;
  public final int depth;

  /**
   * Builds config from command line arguments.
   *
   * @param args arguments.
   */
  public Config(String[] args) {

    Options options = new Options();

    // Build
    Option buildCommandOption =
        new Option("b", "command", true, "Build Command to Run the NullAway");
    buildCommandOption.setRequired(true);
    options.addOption(buildCommandOption);

    // Nullable Annotation
    Option nullableOption =
        new Option("n", "nullable", true, "Fully Qualified name of the Nullable annotation");
    nullableOption.setRequired(false);
    options.addOption(nullableOption);

    // Initializer Annotation
    Option initializerOption =
        new Option("i", "initializer", true, "Fully Qualified name of the Initializer annotation");
    initializerOption.setRequired(true);
    options.addOption(initializerOption);

    // Format
    Option formatOption =
        new Option("pf", "preserve-format", false, "Activates lexical preservation");
    formatOption.setRequired(false);
    options.addOption(formatOption);

    // Bailout
    Option bailoutOption =
        new Option(
            "b",
            "disable-bailout",
            false,
            "Disables bailout, Annotator will not bailout from the search tree as soon as its effectiveness hits zero or less and completely traverses the tree until no new fix is suggested");
    bailoutOption.setRequired(false);
    options.addOption(bailoutOption);

    // Depth
    Option depthOption = new Option("d", "depth", true, "Depth of the analysis");
    depthOption.setRequired(false);
    options.addOption(depthOption);

    // Cache
    Option cacheOption = new Option("dc", "disable-cache", false, "Disables cache usage");
    cacheOption.setRequired(false);
    options.addOption(cacheOption);

    // Chain
    Option chainOption =
        new Option("ch", "chain", false, "Injects the complete tree of fixes associated to the fix");
    chainOption.setRequired(false);
    options.addOption(chainOption);

    // Optimized
    Option optimizedOption = new Option("do", "disable-optimized", false, "Disables optimizations");
    optimizedOption.setRequired(false);
    options.addOption(optimizedOption);

    // Dir
    Option dirOption = new Option("d", "dir", true, "Directory of the output files");
    dirOption.setRequired(true);
    options.addOption(dirOption);

    // NullAway Config Path
    Option nullAwayConfigPathOption =
        new Option("ncp", "nullawayconfig", true, "Path to the NullAway Config");
    nullAwayConfigPathOption.setRequired(true);
    options.addOption(nullAwayConfigPathOption);

    // CSS Config Path
    Option cssConfigPathOption = new Option("ccp", "cssConfigPath", true, "Path to the CSS Config");
    cssConfigPathOption.setRequired(true);
    options.addOption(cssConfigPathOption);

    HelpFormatter formatter = new HelpFormatter();

    if (args.length == 1 && args[0].equals("--help")) {
      formatter.printHelp("Annotator config Flags", options);
      System.exit(1);
    }

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      formatter.printHelp("Annotator config Flags", options);
      System.exit(1);
    }
    Preconditions.checkNotNull(cmd, "Error parsing cmd, cmd cannot bu null");
    this.buildCommand = cmd.getOptionValue(buildCommandOption.getLongOpt());
    this.nullableAnnot =
        cmd.hasOption(nullableOption.getLongOpt())
            ? cmd.getOptionValue(nullableOption.getLongOpt())
            : "javax.annotation.Nullable";
    this.initializerAnnot = cmd.getOptionValue(initializerOption.getLongOpt());
    this.depth =
        Integer.parseInt(
            cmd.hasOption(depthOption.getLongOpt())
                ? cmd.getOptionValue(depthOption.getLongOpt())
                : "5");
    this.dir = Paths.get(cmd.getOptionValue(dirOption.getLongOpt()));
    this.nullAwayConfigPath = Paths.get(cmd.getOptionValue(nullAwayConfigPathOption.getLongOpt()));
    this.cssConfigPath = Paths.get(cmd.getOptionValue(cssConfigPathOption.getLongOpt()));
    this.lexicalPreservationEnabled = cmd.hasOption(formatOption.getLongOpt());
    this.chain = cmd.hasOption(chainOption.getLongOpt());
    this.bailout = !cmd.hasOption(bailoutOption.getLongOpt());
    this.useCache = !cmd.hasOption(cacheOption.getLongOpt());
    this.optimized = !cmd.hasOption(optimizedOption.getLongOpt());
  }

  /**
   * Builds config from json config file.
   *
   * @param configPath path to config file.
   */
  public Config(String configPath) {
    Preconditions.checkNotNull(configPath);
    JSONObject jsonObject;
    try {
      Object obj =
          new JSONParser()
              .parse(Files.newBufferedReader(Paths.get(configPath), Charset.defaultCharset()));
      jsonObject = (JSONObject) obj;
    } catch (Exception e) {
      throw new RuntimeException("Error in reading/parsing config at path: " + configPath, e);
    }
    this.depth = getValueFromKey(jsonObject, "DEPTH", Long.class).orElse((long) 1).intValue();
    this.chain = getValueFromKey(jsonObject, "CHAIN", Boolean.class).orElse(false);
    this.useCache = getValueFromKey(jsonObject, "CACHE", Boolean.class).orElse(true);
    this.lexicalPreservationEnabled =
        getValueFromKey(jsonObject, "FORMAT", Boolean.class).orElse(false);
    this.optimized = getValueFromKey(jsonObject, "OPTIMIZED", Boolean.class).orElse(true);
    this.bailout = getValueFromKey(jsonObject, "BAILOUT", Boolean.class).orElse(true);
    this.nullableAnnot =
        getValueFromKey(jsonObject, "ANNOTATION:NULLABLE", String.class)
            .orElse("javax.annotation.Nullable");
    this.initializerAnnot =
        getValueFromKey(jsonObject, "ANNOTATION:INITIALIZER", String.class)
            .orElse("javax.annotation.Nullable");
    this.nullAwayConfigPath =
        Paths.get(
            getValueFromKey(jsonObject, "NULLAWAY_CONFIG_PATH", String.class)
                .orElse("/tmp/NullAwayFix/config.xml"));
    this.cssConfigPath =
        Paths.get(
            getValueFromKey(jsonObject, "CSS_CONFIG_PATH", String.class)
                .orElse("/tmp/NullAwayFix/css.xml"));
    this.dir =
        Paths.get(
            getValueFromKey(jsonObject, "OUTPUT_DIR", String.class).orElse("/tmp/NullAwayFix"));
    this.buildCommand = getValueFromKey(jsonObject, "BUILD_COMMAND", String.class).orElse(null);
  }

  static class OrElse<T> {
    final Object value;
    final Class<T> klass;

    OrElse(Object value, Class<T> klass) {
      this.value = value;
      this.klass = klass;
    }

    T orElse(T other) {
      return value == null ? other : klass.cast(this.value);
    }
  }

  private <T> OrElse<T> getValueFromKey(JSONObject json, String key, Class<T> klass) {
    if (json == null) {
      return new OrElse<>(null, klass);
    }
    try {
      ArrayList<String> keys = new ArrayList<>(Arrays.asList(key.split(":")));
      while (keys.size() != 1) {
        if (json.containsKey(keys.get(0))) {
          json = (JSONObject) json.get(keys.get(0));
          keys.remove(0);
        } else {
          return new OrElse<>(null, klass);
        }
      }
      return json.containsKey(keys.get(0))
          ? new OrElse<>(json.get(keys.get(0)), klass)
          : new OrElse<>(null, klass);
    } catch (Exception e) {
      return new OrElse<>(null, klass);
    }
  }
}
