include apt

exec {
  'set-licence-selected':
  command => '/bin/echo debconf shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections';

  'set-licence-seen':
  command => '/bin/echo debconf shared/accepted-oracle-license-v1-1 seen true | /usr/bin/debconf-set-selections';
}

apt::ppa { 'ppa:webupd8team/java':
}

package { "oracle-java7-installer":
  ensure => installed,
  require  => [ Apt::Ppa['ppa:webupd8team/java'], Exec['set-licence-selected'], Exec['set-licence-seen'] ]
}

package { "oracle-java7-set-default":
  ensure => installed,
  require  => Package['oracle-java7-installer'],
}
