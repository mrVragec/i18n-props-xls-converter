/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.i18n.propsxlsconverter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.everit.i18n.propsxlsconverter.api.I18nConverter;
import org.everit.i18n.propsxlsconverter.internal.I18nConverterImpl;

/**
 * The main class to manage export or import function.
 */
public final class PropsXlsConverter {

  private static final String ARG_FILE_REGULAR_EXPRESSION = "fileRegularExpression";

  private static final String ARG_FUNCTION = "function";

  private static final String ARG_LANGUAGES = "languages";

  private static final String ARG_WORKING_DIRECTORY = "workingDirectory";

  private static final String ARG_XLS_FILE_NAME = "xlsFileName";

  private static Options createOptions() {
    Options options = new Options();
    options.addOption("f", ARG_FUNCTION, true, "The function: import or export.\n"
        + "Import: Processes the given excel file and creates the properties files "
        + "from it.\nExport: exports the properties files to a human readable and "
        + "editable excel file.\nMandatory.");
    options.addOption("xls", ARG_XLS_FILE_NAME, true,
        "The excel file used for the import or export function.\n"
            + "For example: translation.xls.\nMandatory.");
    options.addOption("wd", ARG_WORKING_DIRECTORY, true,
        "The working directory used as a base directory for searching the properties "
            + "files reqursively.\nFor example: /home/foo or C:\\Users\\foo.\nMandatory.");
    options.addOption("r", ARG_FILE_REGULAR_EXPRESSION, true,
        "Regular expression used to search the properties files recursively.\n"
            + "For example: .*\\.properties$\nMandatory for export function.");
    options.addOption("langs", ARG_LANGUAGES, true,
        "List of the languages to be processed.\nMandatory for the export function.");
    return options;
  }

  private static String getEvaluateMandatoryOptionValue(final String key,
      final CommandLine commandLine,
      final Options options) {
    String result = commandLine.getOptionValue(key);
    if (result == null) {
      PropsXlsConverter.printHelp(options);
      IllegalArgumentException e = new IllegalArgumentException("Missing mandatory argument: "
          + key);
      throw e;
    }
    return result;
  }

  /**
   * The Main class starter method.
   */
  public static void main(final String[] args) {
    Options options = PropsXlsConverter.createOptions();

    CommandLineParser commandLineParser = new BasicParser();
    CommandLine commandLine;
    try {
      commandLine = commandLineParser.parse(options, args, true);
    } catch (ParseException e) {
      PropsXlsConverter.printHelp(options);
      throw new RuntimeException(e);
    }

    String function = PropsXlsConverter.getEvaluateMandatoryOptionValue(ARG_FUNCTION,
        commandLine, options);
    String xlsFileName = PropsXlsConverter.getEvaluateMandatoryOptionValue(ARG_XLS_FILE_NAME,
        commandLine, options);
    String workingDirectory = PropsXlsConverter.getEvaluateMandatoryOptionValue(
        ARG_WORKING_DIRECTORY,
        commandLine,
        options);

    I18nConverter i18nConverter = new I18nConverterImpl();
    if ("import".equals(function)) {
      i18nConverter.importFromXls(xlsFileName, workingDirectory);

    } else if ("export".equals(function)) {
      String fileRegularExpression = PropsXlsConverter.getEvaluateMandatoryOptionValue(
          ARG_FILE_REGULAR_EXPRESSION,
          commandLine,
          options);
      String allLanguages = PropsXlsConverter.getEvaluateMandatoryOptionValue(
          ARG_LANGUAGES, commandLine, options);

      String[] languages = allLanguages.split(",");

      i18nConverter.exportToXls(xlsFileName,
          workingDirectory,
          fileRegularExpression,
          languages);

    } else {
      PropsXlsConverter.printHelp(options);
      return;
    }
  }

  private static void printHelp(final Options options) {
    HelpFormatter helperFormatter = new HelpFormatter();
    helperFormatter.printHelp(
        "java -jar *.jar -f export -xls example.xls -wd /tmp/ -langs hu,de -r .*\\.properties "
            + "\n java -jar *.jar -f import -xls example.xls -wd /tmp/",
        options);
  }

  private PropsXlsConverter() {
  }

}
