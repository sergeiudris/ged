# resources

https://github.com/kartoza/docker-geoserver

https://gist.github.com/SKalt/0f4b757209687331c8a1d40aecbf69f9

https://docs.geoserver.org/latest/en/user/services/wfs/reference.html#wfs-wfst

https://stackoverflow.com/questions/22363192/cors-tomcat-geoserver

https://tomcat.apache.org/tomcat-9.0-doc/manager-howto.html

https://gis.stackexchange.com/questions/210109/enabling-cors-in-geoserver-jetty

https://github.com/Esri/arcgis-runtime-samples-data


## geoserver rest

http://localhost:8600/geoserver/rest/workspaces/dev/datastores/pgdb/featuretypes

http://localhost:8600/geoserver/rest/workspaces/dev/datastores/pgdb/featuretypes?list=configured 
http://localhost:8600/geoserver/rest/workspaces/dev/datastores/pgdb/featuretypes?list=available   fail
http://localhost:8600/geoserver/rest/workspaces/dev/datastores/pgdb/featuretypes?list=available_with_geom fail
http://localhost:8600/geoserver/rest/workspaces/dev/datastores/pgdb/featuretypes?list=all  fail


http://localhost:8600/geoserver/rest/workspaces/dev/featuretypes
http://localhost:8600/geoserver/rest/workspaces/dev/featuretypes?list=configured

http://localhost:8600/geoserver/rest/workspaces/dev/featuretypes/usa_major_cities




#### creating layers

https://docs.geoserver.org/latest/en/api/#/latest/en/api/1.0.0/featuretypes.yaml


#### wfs

http://localhost:8600/geoserver/wfs?service=wfs&version=1.1.0&request=GetCapabilities

http://localhost:8600/geoserver/wfs?service=wfs&version=1.1.0&request=DescribeFeatureType&typeNames=dev:usa_major_cities

