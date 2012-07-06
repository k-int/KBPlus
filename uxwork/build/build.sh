#!/bin/bash
#
# Simple bash script to combine the contents of the sites plugin folder
# into a single file and then pass the contents through JSMin.

SITE_ROOT="../site/"
PATH="$(pwd)/depend/JSMin:$PATH"

# Combine Plugins

cd $SITE_ROOT"js"
touch plugins.js
cat plugins/*.js > plugins.js

# Minify Plugins
jsmin <plugins.js>"plugins.min.js"

