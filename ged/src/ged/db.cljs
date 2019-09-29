(ns ged.db
  (:require [clojure.spec.alpha :as s])
  )

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
    ; settings
     :ged.settings/settings {:group "31-50"}



    ;
     }))

(def default-db (gen-default-db))

#_(get-in default-db [:ged.core/api :base-url] )