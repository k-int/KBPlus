#!/bin/bash


#clear down mongo
# db.titles.ensureIndex({"identifier.value":1, "identifier.type":1});
echo Clean up old db
mongo <<!!!
use kbplus_ds_reconciliation
db.dropDatabase();
db.orgs.ensureIndex({"ukfam": 1});
db.tipps.ensureIndex({"lastmod": 1});
db.pkgs.ensureIndex({"sub": 1});
db.platforms.ensureIndex({"normname": 1});
db.titles.ensureIndex({"identifier":1});
db.tipps.ensureIndex({"titleid":1, "pkgid":1, "platformid":1});
db.st.ensureIndex({"tipp_id":1, "org_id":1, "sub_id":1});
!!!

# Clear down ES indexes
curl -XDELETE 'http://localhost:9200/kbplus'

curl -X PUT "localhost:9200/kbplus" -d '{
  "settings" : {}
}'

curl -X PUT "localhost:9200/kbplus/com.k_int.kbplus.TitleInstance/_mapping" -d '{
  "com.k_int.kbplus.TitleInstance" : {
    "properties" : {
      "title" : {
        type : "string",
        analyzer : "snowball"
      }
    }
  }
}'

curl -X PUT "localhost:9200/kbplus/com.k_int.kbplus.Org/_mapping" -d '{
  "com.k_int.kbplus.Org" : {
    "properties" : {
      "name" : {
        type : "string",
        analyzer : "snowball"
      }
    }
  }
}'


curl -X PUT "localhost:9200/kbplus/com.k_int.kbplus.Package/_mapping" -d '{
  "com.k_int.kbplus.Org" : {
    "properties" : {
      "name" : {
        type : "string",
        analyzer : "snowball"
      }
    }
  }
}'

curl -X PUT "localhost:9200/kbplus/com.k_int.kbplus.Platform/_mapping" -d '{
  "com.k_int.kbplus.Org" : {
    "properties" : {
      "name" : {
        type : "string",
        analyzer : "snowball"
      }
    }
  }
}'

curl -X PUT "localhost:9200/kbplus/com.k_int.kbplus.Subscription/_mapping" -d '{
  "com.k_int.kbplus.Subscription" : {
    "properties" : {
      "name" : {
        type : "string",
        analyzer : "snowball"
      }
    }
  }
}'


curl -X PUT "localhost:9200/kbplus/com.k_int.kbplus.License/_mapping" -d '{
  "com.k_int.kbplus.License" : {
    "properties" : {
      "name" : {
        type : "string",
        analyzer : "snowball"
      }
    }
  }
}'
# Load MYSQL

# Trigger ES reindex
