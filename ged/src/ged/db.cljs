(ns ged.db
  (:require [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            ))

(defn gen-default-conf
  "Returns the default api conf"
  []
  (let [api-v1-baseurl ""]
    {:api.v1/base-url api-v1-baseurl}))

(defn gen-default-db
  "gens the deafult db"
  []
  (let [conf (gen-default-conf)
        base-url (:api.v1/base-url conf)]
    {; core

     :ged.core/name "ged"
     :ged.core/count 0
     :ged.core/module-count 0
     :ged.core/active-panel nil
     :ged.core/conf conf
     :ged.core/api {:base-url base-url
                    :search (str base-url "/usda/search")}

     :ged.core/username "admin"
     :ged.core/password "myawesomegeoserver"

      ; dbquery
     :ged.dbquery/example-queries-res nil

      ; feats
     :ged.feats/search-res nil
     :ged.feats/search-input ""
     :ged.feats/select-feature nil
     :ged.feats/tx-res nil
     :ged.feats/search-table-mdata {:pagination {:showSizeChanger false
                                                 :showQuickJumper true
                                                 :defaultPageSize 5
                                                 :pageSizeOptions  ["5" "10" "20"]
                                                 :position "top"
                                                 :total 0
                                                 :current 1
                                                 :pageSize 10}
                                    :filters {}
                                    :sorter {}
                                    :extra {:currentDataSource []}}
     :ged.feats/feature-type-input "dev:usa_major_cities"
     :ged.feats/feature-ns "http://www.opengis.net/wfs/dev"

    ; settings
     :ged.settings/settings {:group "31-50"}
     :ged.settings/proxy-path "/geoserver"
     :ged.settings/proxy-geoserver-host "http://geoserver:8080"
     :ged.settings/geoserver-host "http://localhost:8600"

     ; map

     :ged.map/checked-layer-ids ["dev:usa_major_cities"
                                 "dev:usa_major_highways"
                                 #_"dev:world_cities"
                                 #_"dev:world_continents"]

     :ged.map/tab-button nil
     :ged.map/fetch-all-layers-res nil

     :ged.map/selected-layers-ids ["dev:usa_major_cities"
                                   "dev:usa_major_highways"
                                   #_"dev:world_cities"
                                   #_"dev:world_continents"]
     :ged.map/selected-layers-checked []
     :ged.map/all-layers-checked []

     :ged.map/wfs-search-layer-input ""
     :ged.map/wfs-search-area-type nil
     :ged.map/wfs-search-table-mdata {:pagination {:showSizeChanger false
                                                   :showQuickJumper true
                                                   :defaultPageSize 5
                                                   :pageSizeOptions  ["5" "10" "20"]
                                                   :position "top"
                                                   :total 0
                                                   :current 1
                                                   :pageSize 10}
                                      :filters {}
                                      :sorter {}
                                      :extra {:currentDataSource []}}
     :ged.map/wfs-search-res nil

     ; rest
     :ged.rest/fetch-selected-url-res nil
     :ged.rest/selected-url "/rest/workspaces/dev/featuretypes.json"
     :ged.rest/select-item nil
     :ged.rest/tx-res nil
     :ged.rest/search-table-mdata {:pagination false
                                   #_{:showSizeChanger false
                                      :showQuickJumper false
                                      :defaultPageSize 5
                                      :pageSizeOptions  ["5" "10" "20"]
                                      :position "top"
                                      :total 0
                                      :current 1
                                      :pageSize 10}
                                   :filters {}
                                   :sorter {}
                                   :extra {:currentDataSource []}}
     :ged.rest/selected-item-href nil
     :ged.rest/selected-item-path nil


    ;
     }))




(def default-db (gen-default-db))

#_(get-in default-db [:ged.core/api :base-url] )