(ns ged.settings.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::settings
 (fn [db _]
   (:ged.db.settings/settings db)))



