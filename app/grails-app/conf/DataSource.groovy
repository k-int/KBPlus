dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            // dbCreate==update  - II: Trial Removing this, we manually update the schema now, so don't do this!
            dbCreate = "update"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect=org.hibernate.dialect.MySQL5Dialect
            username = "k-int"
            password = "k-int"
            url = "jdbc:mysql://localhost/KBPlus?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8"
            pooled = true
            // logSql = true
            // formatSql = true
            properties {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="select 1"
            }
        }
    }
    test {
        dataSource {
            dbCreate = "create-drop"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect=org.hibernate.dialect.MySQL5Dialect
            username = "k-int"
            password = "k-int"
            url = "jdbc:mysql://localhost/kbplustest?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8"
            pooled = true
            // logSql = true
            // formatSql = true
            properties {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="select 1"
            }
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "k-int"
            password = "k-int"
            url = "jdbc:mysql://localhost/KBPlus?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8"
            pooled = true
            dialect=org.hibernate.dialect.MySQL5Dialect
            properties {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="select 1"
            }
        }

    }
}
