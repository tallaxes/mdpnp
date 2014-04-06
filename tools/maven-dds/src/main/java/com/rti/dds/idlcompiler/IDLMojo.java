/**
 * Copyright 2013, Real-Time Innovations (RTI)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rti.dds.idlcompiler;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * RTI IDL Code Generator.
 *
 * @goal idl-compile
 * @phase generate-sources
 */
public class IDLMojo extends AbstractMojo
{
    /**
     * Items to be converted by rtiddsgen needs to be either
     * idl, xsd, xml, or wsdl files.
     */
    private static final String TOKEN_SEPARATOR = ",";
	private static final String[] IDL_FILE_EXTENSIONS = {".idl", ".xsd", ".xml", ".wsdl"};
    private static final FilenameFilter IDL_FILE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
        	for (String ext : IDL_FILE_EXTENSIONS) {
                if (name.endsWith(ext)) {
                	return true;
                }
        	}
            return false;
        }
    };

    /**
     * Location of the items to convert using rtiddsgen
     * Note: All items in this directory is expected to follow the following
     * conventions for their extensions:
     *
     * *.idl - for IDL files
     * *.xml - for XML files
     * *.xsd - for XSD files
     * *.wsdl - for WSDL files
     * 
     * @parameter expression="${rti.idl.idlDir}" default-value="${basedir}/src/main/resources"
     */
    private File idlDir;

    /**
     * Location of the output directory.
     *
     * nddsgen option: -d outDir
     *
     * @parameter expression="${rti.idl.outDir}" default-value="${project.build.directory}/generated-sources"
     */
    private File outDir;

    /**
     * Language to convert IDL to, options are C|C++|Java|C++/CLI|C#
     *
     * nddsgen option: -language lang
     *
     * @parameter expression="${rti.idl.lang}" default-value="Java"
     */
    private String lang;

    /**
     * Namespace (only for language=C++)
     *
     * nddsgen option: -namespace
     *
     * @parameter expression="${rti.idl.namespace}" default-value=""
     */
    private String namespace;

    /**
     * Specifies the root package into which generated classes will be placed.
     * This only applies for language=Java.  Modules declared in
     * type-definition file is considered subpackages of this specified package.
     *
     * nddsgen option: -package packagePrefix
     *
     * @parameter expression="${rti.idl.packagePrefix}" default-value=""
     */
    private String packagePrefix;
    
    /**
     * Enables rtiddsgen to overwrite any existing generated files.  If this
     * is not enabled rtiddsgen will issue warnings if existing files are
     * found but will not replace them.
     *
     * nddsgen option: -replace
     *
     * @parameter expression="${rti.idl.replace}" default-value="true"
     */
    private boolean replace;

    /**
     * Creates XML files for debugging rtiddsgen only.  Use this
     * option only at the direction of RTI support, it is unlikely
     * to be useful to you otherwise.
     * 
     * nddsgen option: -debug
     *
     * @parameter expression="${rti.idl.debug}" default-value="false"
     */
    private boolean debug;

    /**
     * This option is only available if the language=C|C++.
     * 0: No optimization.
     * 1: Compiler generates type-plugin and support code for
     *    typedefs but optimizes its use.
     * 2: Same as level 1 with the addition that hte type-plugin
     *    and support code typedefs are not generated (since they
     *    would not be used by the code for data types defined in the
     *    same IDL file that uses the typedefs)
     * 
     * nddsgen option: -optimization optimizationLevel
     *
     * @parameter expression="${rti.idl.optimizationLevel}" default-value="0"
     */
    private int optimizationLevel;

    /**
     * Sets the size assigned to unbounded strings, not counting a terminating
     * NULL character.  The default value is 255 bytes.
     *
     * nddsgen option: -stringSize stringSize
     *
     * @parameter expression="${rti.idl.stringSize}" default-value="255"
     */
    private int stringSize;

    /**
     * Sets the size assigned to unbounded sequences.  The default value is 100
     * elements.
     * 
     * nddsgen option: -sequenceSize sequenceSize
     *
     * @parameter expression="${rti.idl.sequenceSize}" default-value="100"
     */
    private int sequenceSize;

    /**
     * Disables type-code support.  By using this option you can generate
     * code that can be used in a standalone manner.  This is only useful
     * when languages=C|C++
     *
     * Note: If you are using a large data type (more than 64K) and type
     * code support, you will see warnings when type code information is sent.
     * RTI DDS has a type code size limit of 64K.  To avoid the warning
     * when working with data types with type codes larger than 64K,
     * turn off type code support by using this option, -notypecode.
     *
     * nddsgen option: -notypecode
     *
     * @parameter expression="${rti.idl.noTypeCode}" default-value="false"
     */
    private boolean noTypeCode;

    /**
     * Disables the preprocessor.  This is only useful when language=C|C++.
     *
     * nddsgen option: -ppDisable
     *
     * @parameter expression="${rti.idl.ppDisable}" default-value="true"
     */
    private boolean ppDisable;

    /**
     * Specifies the preprocessor.  This is only useful when language=C|C++
     *
     * nddsgen option: -ppPath ppPath
     *
     * @parameter expression="${rti.idl.ppPath}" default-value=""
     */
    private String ppPath;

    /**
     * Specifies the preprocessor options.  This is only useful when
     * language=C|C++
     *
     * nddsgen option: -ppOption ppOption
     *
     * @parameter expression="${rti.idl.ppOption}" default-value=""
     */
    private String ppOption;

    /**
     * Defines the preprocessor macros.  This is only useful when
     * language=C|C++
     *
     * Note: preprocessorMacros should be setup as name/value pairs,
     * IE name=value.  Use "," to separate the name/value pairs.
     * 
     * nddsgen option: -D ppMacros
     *
     * @parameter expression="${rti.idl.ppMacros}" default-value=""
     */
    private String ppMacro;

    /**
     * Cancels the previous definiton of name (name is the item
     * defined in the preprocessorMacros).  This is only useful when
     * language=C|C++
     *
     * nddsgen option: -U name
     *
     * @parameter expression="${rti.idl.name}" default-value=""
     */
    private String name;

    /**
     * Adds to the list of directories to be searched for type-definition
     * files (IDL, XML, XSD, or WSDL files).  Note: A type-definition file
     * in one format cannot include a file in another format.
     *
     * nddsgen option: -I includeDirectory
     *
     * @parameter expression="${rti.idl.includeDir}" default-value=""
     */
    private String includeDir;

    /**
     * Forces rtiddsgen to put 'copy' logic into the corresponding TypeSupport
     * class rather than the type itself.  This option is only used for Java code
     * generation.
     *
     * nddsgen option: -noCopyable
     *
     * @parameter expression="${rti.idl.noCopyable}" default-value="false"
     */
    private boolean noCopyable;

    /**
     * Makes the generated code compatible with RTI DDS 4.2e.
     *
     * nddsgen option: -use42eAlignment
     *
     * @parameter expression="${rti.idl.use42eAlignment}" default-value="false"
     */
    private boolean use42eAlignment;

    /**
     * Enables use of the escape character '_' in IDL identifiers.
     *
     * nddsgen option: -enableEscapeChar
     *
     * @parameter expression="${rti.idl.enableEscapeChar}" default-value="false"
     */
    private boolean enableEscapeChar;

    /**
     * Converts the input type descritpion file into XML format.  This option
     * creates a new file with the same name as the input file and a .xml
     * extension.
     *
     * nddsgen option: -convertToXml
     *
     * @parameter expression="${rti.idl.convertToXml}" default-value="false"
     */
    private boolean convertToXml;

    /**
     * Converts the input type descritpion file into XSD format.  This option
     * creates a new file with the same name as the input file and a .xsd
     * extension.
     *
     * nddsgen option: -convertToXsd
     *
     * @parameter expression="${rti.idl.convertToXsd}" default-value="false"
     */
    private boolean convertToXsd;

    /**
     * Converts the input type descritpion file into WSDL format.  This option
     * creates a new file with the same name as the input file and a .wsdl
     * extension.
     *
     * nddsgen option: -convertToWsdl
     *
     * @parameter expression="${rti.idl.convertToWsdl}" default-value="false"
     */
    private boolean convertToWsdl;

    /**
     * Converts the input type descritpion file into IDL format.  This option
     * creates a new file with the same name as the input file and a .idl
     * extension.
     *
     * nddsgen option: -convertToIdl
     *
     * @parameter expression="${rti.idl.convertToIdl}" default-value="false"
     */
    private boolean convertToIdl;

    /**
     * Converts the input type description file into CCL format.
     * This option creates a new file with the same name as the input
     * file with a .ccl extension.
     *
     * nddsgen option: -convertToCcl
     *
     * @parameter expression="${rti.idl.convertToCcl}" default-value="false"
     */
    private boolean convertToCcl;

    /**
     * Converts the input type description file into CCs format.
     * This option creates a new file with the same name as the input
     * file with a .ccs extension.
     *
     * nddsgen option: -convertToCcs
     *
     * @parameter expression="${rti.idl.convertToCcs}" default-value="false"
     */
    private boolean convertToCcs;

    /**
     * Displays the version of the rtiddsgen being used.
     * Note, setting this option will cause all other options
     * to be ignored.
     *
     * nddsgen option: -version
     *
     * @parameter expression="${rti.idl.version}" default-value="false"
     */
    private boolean version;

    /**
     * To set the level of output logging from the rttiddsgen.
     * 1: Exceptions
     * 2: Exceptions and warnings
     * 3: Exceptions, warnings and information (default)
     *
     * nddsgen option: -verbosity verbosityLevel
     *
     * @parameter expression="${rti.idl.verbosityLevel}" default-value="3"
     */
    private int verbosityLevel;

    /**
     * NDDS home is the location of the RTI NDDS installation.  This is required
     * to find all required resources for rtiddsgen.

     * @parameter expression="${rti.idl.nddsHome}" default-value="";
     */
    private File nddsHome;

    private List<String> rtiDdsGenArgumentList = new ArrayList<String>();

    private List<File> idlFileList = new ArrayList<File>();
    
    

    @Override
    public void execute()
        throws MojoExecutionException {
        try {
            createRtiDdsGenArguments();
        } catch (IDLException ex) {
            this.getLog().error(ex.getMessage());
            throw new MojoExecutionException(ex.getMessage());
        }
    }

    private void runRtiDdsGen() throws IDLException {
        String[] arguments = new String[rtiDdsGenArgumentList.size()];

        rtiDdsGenArgumentList.toArray(arguments);

        StringBuilder sb = new StringBuilder();
        for (String arg : arguments) {
        	sb.append(" ");
        	sb.append(arg);
        }
        getLog().info("Running nddsgen with arguments:" + sb.toString());

        try {
            com.rti.ndds.nddsgen.Main.main(arguments);
        } catch (Exception ex) {
            throw new IDLException(ex);
        }
    }
    
    private void createRtiDdsGenArguments() throws IDLException {
        validateNddsHome();
        
        /*
         * If the version option is set, the only thing this plugin will
         * do is print the version out.
         */
        if (version) {
            getLog().warn("Version option is set, all other options being ignored.");
            rtiDdsGenArgumentList.add("-version");
            runRtiDdsGen();
            return;
        }

        findItemsToConvert();
        validateLanguageOption();
        validateNamespace();

        if (replace) {
            rtiDdsGenArgumentList.add("-replace");
        }
        if (debug) {
            rtiDdsGenArgumentList.add("-debug");
        }

        validateOptimization();
        validateStringSize();
        validateSequenceSize();
        validateNoTypeCode();
        validatePpDisable();
        validatePpPath();
        validatePpMacros();
        validatePackagePrefix();
        validateNames();
        validateIncludeDirectories();
        validateNoCopyable();

        if (!outDir.exists()) {
            if (!outDir.mkdirs()) {
                throw new IDLException("Output directory '" + outDir + "' cannot be created");    
            }
        }
        if ((outDir.isDirectory()) && (outDir.canWrite())) {
            rtiDdsGenArgumentList.add("-d");
            rtiDdsGenArgumentList.add(outDir.getAbsolutePath());
        } else {
            throw new IDLException("Output directory '" + outDir + "' is invalid");
        }
        if (use42eAlignment) {
            rtiDdsGenArgumentList.add("-use42eAlignment");
        }
        if (enableEscapeChar) {
            rtiDdsGenArgumentList.add("-enableEscapeChar");
        }

        validateVerbosity();

        if (convertToCcl) {
            rtiDdsGenArgumentList.add("-convertToCcl");
        }
        if (convertToCcs) {
            rtiDdsGenArgumentList.add("-convertToCcs");
        }
        if (convertToIdl) {
            rtiDdsGenArgumentList.add("-convertToIdl");
        }
        if (convertToXml) {
            rtiDdsGenArgumentList.add("-convertToXml");
        }
        if (convertToXsd) {
            rtiDdsGenArgumentList.add("-convertToXsd");
        }
        if (convertToWsdl) {
            rtiDdsGenArgumentList.add("-convertToWsdl");
        }

        for (File idlFile : idlFileList) {
            System.out.println("Converting file: " + idlFile.getAbsolutePath());
            /* Push the file onto the arguments list */
            rtiDdsGenArgumentList.add(idlFile.getAbsolutePath());
            
            runRtiDdsGen();

            /* Pop the file off the arguments list */
            rtiDdsGenArgumentList.remove(rtiDdsGenArgumentList.size() - 1);
        }
    }

    private void validatePackagePrefix() {
        if ((packagePrefix == null) || (packagePrefix.isEmpty())) {
            return;
        }
        
        rtiDdsGenArgumentList.add("-package");
        rtiDdsGenArgumentList.add(packagePrefix);
    }
    private void validateVerbosity() {
        if ((verbosityLevel < 1) || (verbosityLevel > 3)) {
            getLog().debug("Ignoring invalid verbosity level");
        } else {
            rtiDdsGenArgumentList.add("-verbosity");
            rtiDdsGenArgumentList.add(String.valueOf(verbosityLevel));
        }
    }
    
    private void validateNoCopyable() {
        if (noCopyable) {
            if (!lang.equals("Java")) {
                rtiDdsGenArgumentList.add("-noCopyable");
            } else {
                getLog().info("Ignoring noCopyable, noCopyable is only valid "
                    + "for Java");
            }
        }
    }
    
    private void validateIncludeDirectories() {
        if ((includeDir == null) || (includeDir.isEmpty())) {
            return;
        }

        /*
         * Multiple preprocessor paths can be sent, so we split
         * on the token character
         */
        String[] includeDirs = includeDir.split(TOKEN_SEPARATOR);

        for (String option : includeDirs) {
            if ((option != null) && (!option.isEmpty())) {
                rtiDdsGenArgumentList.add("-I");
                rtiDdsGenArgumentList.add(option);
            } else {
                getLog().warn("Ignoring name '" + option + "'");
            }
        }
    }
    private void validateNames() throws IDLException {
        if ((name == null) || (name.isEmpty())) {
            /* No preprocessor paths, so just return */
            return;
        }

        if ((!lang.equals("C")) && (!lang.equals("C++"))){
            getLog().info("Ignoring name, name is only valid "
                    + "for C|C++");

            return;
        }
        /*
         * Multiple preprocessor paths can be sent, so we split
         * on the token character
         */
        String[] names = name.split(TOKEN_SEPARATOR);

        for (String option : names) {
            if ((option != null) && (!option.isEmpty())) {
                rtiDdsGenArgumentList.add("-U");
                rtiDdsGenArgumentList.add(option);
            } else {
                getLog().warn("Ignoring name '" + option + "'");
            }
        }
    }
    private void validatePpMacros() throws IDLException {
        if ((ppMacro == null) || (ppMacro.isEmpty())) {
            /* No preprocessor paths, so just return */
            return;
        }

        if ((!lang.equals("C")) && (!lang.equals("C++"))){
            getLog().info("Ignoring ppMacro, ppMacro is only valid "
                    + "for C|C++");

            return;
        }
        /*
         * Multiple preprocessor paths can be sent, so we split
         * on the token character
         */
        String[] ppMacros = ppMacro.split(TOKEN_SEPARATOR);

        for (String option : ppMacros) {
            if ((option != null) && (!option.isEmpty())) {
                rtiDdsGenArgumentList.add("-D");
                rtiDdsGenArgumentList.add("\"" + option + "\"");
            } else {
                getLog().warn("Ignoring ppMacro '" + option + "'");
            }
        }
    }

    private void validatePpPath() throws IDLException {
        if ((ppPath == null) || (ppPath.isEmpty())) {
            /* No preprocessor paths, so just return */
            return;
        }

        if ((!lang.equals("C")) && (!lang.equals("C++"))){
            getLog().info("Ignoring ppPath, ppPath is only valid "
                    + "for C|C++");

            return;
        }

        rtiDdsGenArgumentList.add("-ppPath");
        rtiDdsGenArgumentList.add(ppPath);
    }

    private void validatePpOptions() throws IDLException {
        if ((ppOption == null) || (ppOption.isEmpty())) {
            /* No preprocessor options, so just return */
            return;
        }

        if ((!lang.equals("C")) && (!lang.equals("C++"))){
            getLog().info("Ignoring ppOptions, ppOptions is only valid "
                    + "for C|C++");

            return;
        }

        /*
         * Multiple preprocessor options can be sent, so we split
         * on the token character
         */
        String[] ppOptions = ppOption.split(TOKEN_SEPARATOR);

        for (String option : ppOptions) {
            if ((option != null) && (!option.isEmpty())) {
                rtiDdsGenArgumentList.add("-ppOption");
                rtiDdsGenArgumentList.add(option);
            } else {
                getLog().warn("Ignoring ppOption '" + option + "'");
            }
        }
    }

    private void validatePpDisable() {
        if (ppDisable) {
            //if ((lang.equals("C")) || (lang.equals("C++"))) {
                rtiDdsGenArgumentList.add("-ppDisable");
            //} else {
            //    getLog().info("Ignoring ppDisable, ppDisable is only valid "
            //            + "for C|C++");
            //}
        }
    }
    private void validateNoTypeCode() {
        if (noTypeCode) {
            if ((lang.equals("C")) || (lang.equals("C++"))) {
                rtiDdsGenArgumentList.add("-notypecode");
            } else {
                getLog().info("Ignoring noTypeCode, noTypeCode is only valid "
                        + "for C|C++");
            }
        }
    }
    private void validateStringSize() {
        if (stringSize <= 0) {
            getLog().info("Ignoring invalid value for stringSize ("
                    + stringSize + ").  Using default 255.");
        }

        rtiDdsGenArgumentList.add("-stringSize");
        rtiDdsGenArgumentList.add(String.valueOf(stringSize));
    }

    private void validateSequenceSize() {
        if (sequenceSize <= 0) {
            getLog().info("Ignoring invalid value for sequenceSize ("
                    + sequenceSize + ").  Using default 100.");
        }

        rtiDdsGenArgumentList.add("-sequenceSize");
        rtiDdsGenArgumentList.add(String.valueOf(sequenceSize));
    }

    private void validateOptimization() {
        if ((optimizationLevel < 0) || (optimizationLevel > 2)) {
            getLog().info("Ignoring invalid value for optimization level ("
                    + optimizationLevel + ").  Must be 0|1|2");
        }

        if ((!lang.equals("C")) && (!lang.equals("C++"))){
            getLog().info("Ignoring optimization, optimization is only valid "
                    + "for C|C++");

            return;
        }

        rtiDdsGenArgumentList.add("-optimization");
        rtiDdsGenArgumentList.add(String.valueOf(optimizationLevel));
    }
    
    private void validateNamespace() throws IDLException {
        rtiDdsGenArgumentList.add("-namespace");
    }
    
    private void findItemsToConvert() throws IDLException {
        if ((idlDir == null) || (!idlDir.exists())) {
            throw new IDLException("IDL Directory does not exist ("
                    + idlDir + ")");
        } else if (!idlDir.canRead()) {
            throw new IDLException("IDL Directory cannot be read");
        } else {
            File[] idlFiles = idlDir.listFiles(IDL_FILE_FILTER);
            
            idlFileList.addAll(Arrays.asList(idlFiles));
        }

        if (idlFileList.isEmpty()) {
            throw new IDLException("No items found to convert");
        }
    }
    
    private void validateLanguageOption() throws IDLException {
        if ((lang == null) || (lang.isEmpty())) {
            throw new IDLException("Language is not set properly. "
                    + "set to '" + lang + "'. "
                    + "lang must be C|C++|Java|C++/CLI|C#");
        }

        rtiDdsGenArgumentList.add("-language");

        if (lang.equals("C")) {
            rtiDdsGenArgumentList.add("C");
        } else if (lang.equals("C++")) {
            rtiDdsGenArgumentList.add("C++");
        } else if (lang.equals("Java")) {
            rtiDdsGenArgumentList.add("Java");
        } else if (lang.equals("C++/CLI")) {
            rtiDdsGenArgumentList.add("C++/CLI");
        } else if (lang.equals("C#")) {
            rtiDdsGenArgumentList.add("C#");
        } else {
            throw new IDLException("Language is not set properly. "
                    + "set to '" + lang + "'. "
                    + "lang must be C|C++|Java|C++/CLI|C#");
        }

        getLog().warn("Language is set to: " + lang);
    }
    

    /**
     * For using RTI's DDS Generation tool the NDDSHOME environment
     * variable needs to be set, or specified in the configuration
     * file.  If it is not, then rtiddsgen cannot run and this will
     * cause this plugin to throw a MojoExecutionException.
     * @throws IDLException When nddsHome is not set and the
     * environment variable NDDSHOME is not set or invalid.
     */
    private void validateNddsHome() throws IDLException {
        if (nddsHome == null) {
            String envNddsHome = System.getenv("NDDSHOME");

            if (envNddsHome == null) {
                throw new IDLException("nddsHome is not set");
            } else {
                getLog().info("Setting nddsHome to environment variable NDDSHOME "
                        + "'" + envNddsHome + "'");
                nddsHome = new File(envNddsHome);
            }
        }

        if (!nddsHome.exists()) {
            throw new IDLException("nddsHome is invalid (does not exist)");
        } else if (!nddsHome.canRead()) {
            throw new IDLException("nddsHome is invalid (cannot read)");
        } else {
            File nddsHomeResource = new File(nddsHome, "resource");

            if ((!nddsHomeResource.exists()) ||
                (!nddsHomeResource.canRead())) {
                throw new IDLException("nddsHome is invalid (required files cannot be found)");
            } else {
                System.setProperty("NDDSHOME", nddsHome.getAbsolutePath());
                System.setProperty("NDDS_RESOURCE_DIR", nddsHomeResource.getAbsolutePath());
            }
        }
    }
}
