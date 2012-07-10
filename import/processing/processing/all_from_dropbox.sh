#!/bin/bash

./PlatformData.groovy  ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Platforms/platforms.csv

echo SO import
rm ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Subscriptions\ Offered/*BAD
find ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Subscriptions\ Offered -name "*.csv" -exec ./SOData.groovy '{}' >> so_imp_log \;

rm ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Masters/*BAD
find ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Masters -name "*.csv" -exec ./SOData.groovy '{}' >> so_imp_log \;

echo Orgs import
rm ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Subscribing\ Orgs/*BAD
./OrgsData.groovy  ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Subscribing\ Orgs/subscribing\ organisations.csv  >> orgs_imp_log

./Consortium.groovy  ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Subscribing\ Orgs/consortium.csv  >> cons_import_log

rm ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Subscriptions\ Taken/*BAD
find ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/Subscriptions\ Taken -name "*.csv" -exec ./STData.groovy '{}' >> st_imp_log \;

find ~/Dropbox2/Dropbox/KB+/System\ -\ Data/Current/licences -name "*.zip" -exec ./LicenseData.groovy '{}' >> license_imp_log \;
