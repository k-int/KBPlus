#!/bin/bash

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

# Load MYSQL

# Trigger ES reindex
