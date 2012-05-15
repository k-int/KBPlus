#!/bin/bash

rm so_imp_log  so_stats.csv  stats.txt

echo filename,num_packages_created,titles_matched_by_identifier,tipps_created,titles_matched_by_title,num_bad_rows > so_stats.csv

echo Clean up old db
mongo <<!!!
use kbplus_ds_reconciliation
db.dropDatabase();
!!!

./PlatformData.groovy

echo SO import
rm ../kb_plus_datafiles/*BAD
find ../kb_plus_datafiles -name "*.csv" -exec ./SOData.groovy {} >> so_imp_log \;

echo Orgs import
rm ../orgs_data/*BAD
./OrgsData.groovy ../orgs_data/subscribing\ organisations.csv  >> so_imp_log

find ../st_datafiles -name "*.csv" -exec ./STData.groovy {} >> st_imp_log \;
