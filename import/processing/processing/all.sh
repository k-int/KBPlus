#!/bin/bash

rm so_imp_log  so_stats.csv  stats.txt st_imp_log cons_import_log orgs_imp_log

echo filename,num_packages_created,titles_matched_by_identifier,tipps_created,titles_matched_by_title,num_bad_rows > so_stats.csv

echo Clean up old db
mongo <<!!!
use kbplus_ds_reconciliation
db.dropDatabase();
!!!

# Clear down ES indexes
curl -XDELETE 'http://localhost:9200/kbplus'


./PlatformData.groovy

echo SO import
rm ../kb_plus_datafiles/*BAD
find ../kb_plus_datafiles -name "*.csv" -exec ./SOData.groovy '{}' >> so_imp_log \;

echo Orgs import
rm ../orgs_data/*BAD
./OrgsData.groovy ../orgs_data/subscribing\ organisations.csv  >> orgs_imp_log

./Consortium.groovy ../orgs_data/consortium.csv  >> cons_import_log

rm ../st_datafiles/*BAD
find ../st_datafiles -name "*.csv" -exec ./STData.groovy '{}' >> st_imp_log \;

find ../license_data -name "*.zip" -exec ./LicenseData.groovy '{}' >> license_imp_log \;


