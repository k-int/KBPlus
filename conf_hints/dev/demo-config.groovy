println("------- Using demo config from conf hints ------- ")
localauth=true
dataSource.url="jdbc:mysql://localhost/kbplustest?autoReconnect=true&amp;characterEncoding=utf8"
aggr_es_cluster='tigerelasticsearch'
onix_ghost_licence="Jisc Collections Model Journals Licence 2015"
aggr_es_index='kbplustest'
publicationService.baseurl='http://knowplus.edina.ac.uk:2012/kbplus/api'
// no slash at end of SystemBaseURL
SystemBaseURL="http://localhost:19080/demo"
docstore='http://deprecated/deprecated'
kbplusSystemId='KITestIntsance'
ZenDeskBaseURL='https://projectname.zendesk.com'
ZenDeskDropboxID=20000000
ZenDeskLoginEmail='Zen.Desk@Host.Name'
ZenDeskLoginPass='Zen.Desk.Password'
KBPlusMaster=true
juspThreadPoolSize=10
doDocstoreMigration=false
JuspApiUrl='https://www.jusp.mimas.ac.uk/'
sysusers = [
  [ 
    name:'userb',
    pass:'userb',
    display:'UserB',
    email:'read@localhost',
    roles:['ROLE_USER','INST_USER']
  ],
    [ 
    name:'userc',
    pass:'userc',
    display:'UserC',
    email:'read@localhost',
    roles:['ROLE_USER','INST_USER'
]  ],
    [ 
    name:'usera',
    pass:'usera',
    display:'UserA',
    email:'read@localhost',
    roles:['ROLE_USER','INST_USER']  
  ],
    [ 
    name:'admin',
    pass:'admin',
    display:'TestAdmin',
    email:'read@localhost',
    roles:['ROLE_USER','ROLE_ADMIN',"INST_ADM"]
  ]
]

