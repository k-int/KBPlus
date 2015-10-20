package com.k_int.kbplus

import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.client.transport.TransportClient
import grails.util.Holders

class ESWrapperService {

  static transactional = false

  def clientSettings = [:];

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");

    updateSettings()

    log.debug("Init completed");
  }

  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy");
    // gNode.close()
    log.debug("Destroy completed");
  }

  def getClient() {
    log.debug("getClient()");
    updateSettings()
    TransportClient esclient = new TransportClient(clientSettings.settings)
    esclient.addTransportAddress(clientSettings.address)
    return esclient
  }

  def updateSettings() {
    ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder()
    def es_cluster = Holders.config.aggr_es_cluster?:"elasticsearch"
    builder.put("cluster.name", es_cluster).put("client.transport.sniff", true)
    Settings settings = builder.build()
    clientSettings.settings = settings
    clientSettings.address = new InetSocketTransportAddress("localhost",9300)
  }

}
