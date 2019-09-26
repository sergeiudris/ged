(ns ged.settings.spec
  (:require [clojure.repl :as repl]
            [clojure.spec.alpha :as s]
            [clojure.test.check.generators]
            [clojure.spec.gen.alpha :as gen]
            ))

(s/def ::uuid uuid?)
(s/def ::type keyword?)

(s/def ::entity (s/keys :req [::uuid
                              ::type] ))
