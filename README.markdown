Alternativa JSON Builder & Formatter
====================================

The Problem
-----------
The current JSON Builder & Formatter doesn't allow certain valid JSON:

 1. The JSON has to start with "{", i.e. there can't be a JSONArray at 
    the root.
 1. Only the first key-value pair will be considered. This is because JSON 
    is mapped to XML-like datastructure, and XML must have a root, so the 
    first key-value pair is considered as root.
 1. The value of the first key-value pair must not be an array. 
    This because the converter has to know which XML tag should be used for 
    each value:

        e.g.: ... { "key": ["val1", "val2", ...]} -> <key>val1</key><key>val2</key>....


The solution
------------
We add a virtual root so that the problems explained above won't occur. 
This will be fixed in the future with a new JSON Builder & Formatter for
Axis2.


Build and install
-----------------
1. Build the JAR with maven.

       mvn package

2. Install

   a. Copy the JAR to the WSO2 dropins folder

          cp target/jsonbuilderformatter-1.0.0.jar <WSO2 root dir>/repository/components/dropins

   b. Edit the axis2.xml and comment the existing JSON builder & formatter and 
      add the JSON builder and formatter

          vim <WSO2 root dir>/repository/conf/axis2.xml

          <!--
          <messageBuilder contentType="application/json"
            class="org.apache.axis2.json.JSONOMBuilder"/>
          -->
          <messageFormatter contentType="application/json"
            class="es.tangrambpm.wso2esb.JSONMessageFormatter"/>

          <!--
          <messageBuilder contentType="application/json"
                        class="org.apache.axis2.json.JSONOMBuilder"/>
          -->
          <messageBuilder contentType="application/json"
            class="es.tangrambpm.wso2esb.JSONOMBuilder"/>
 
3. Restart the ESB.


Feedback
--------

Please send me any feedback and pull requests to erevilla@yaco.es.

