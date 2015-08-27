package com.k_int.kbplus

import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.settings.Settings

class ESWrapperService {

  static transactional = false

  def grailsApplication;


  def client = [:];

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");

    ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder()
    builder.put("cluster.name", "elasticsearch").put("client.transport.sniff", true)
    Settings settings = builder.build()
    client.settings = settings
    client.address = new InetSocketTransportAddress("localhost",9300)

    log.debug("Init completed");
  }

  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy");
    // gNode.close()
    log.debug("Destroy completed");
  }

  def getClient() {
    log.debug("getNode()");
    client
  }

}
