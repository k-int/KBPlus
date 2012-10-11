package com.k_int.kbplus

import com.gmongo.GMongo

class MongoService {

  def mongo = null;

  @javax.annotation.PostConstruct
  def startup() {
    log.debug("Init mongo service");
    def options = new com.mongodb.MongoOptions()
    options.socketKeepAlive = true
    options.autoConnectRetry = true
    options.slaveOk = true
    mongo = new com.gmongo.GMongo('127.0.0.1', options);
  }

  @javax.annotation.PreDestroy
  def shutdown() {
    log.debug("Close mongo service");
    mongo.close()
  }

  def getMongo() {
    mongo
  }
}
