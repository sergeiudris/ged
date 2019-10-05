(ns ged.core
  (:require [clojure.repl :as repl]
            [clojure.string :as str]
            ["ol/format/WFS" :default OlFormatWFS]
            ["ol/format/GeoJSON" :default OlFormatGeoJSON]))

(defn deep-merge [a & maps]
  (if (map? a)
    (apply merge-with deep-merge a maps)
    (apply merge-with deep-merge maps)))



(defn basic-creds
  [uname pass]
  (str "Basic " (js/btoa (str uname ":" pass))))
