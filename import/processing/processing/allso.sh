#!/bin/bash


echo Clean up old db
mongo <<!!!
use kbplus_ds_reconciliation
db.dropDatabase();
!!!

./PlatformData.groovy

echo running import
rm ../kb_plus_datafiles/*BAD
rm so_imp_log
find ../kb_plus_datafiles -name "*.csv" -exec ./SOData.groovy {} >> so_imp_log \;
