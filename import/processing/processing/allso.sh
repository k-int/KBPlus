#!/bin/bash

rm ../kb_plus_datafiles/*BAD
> so_imp_log
find ../kb_plus_datafiles -name "*.csv" -exec ./SOData.groovy {} >> so_imp_log \;
