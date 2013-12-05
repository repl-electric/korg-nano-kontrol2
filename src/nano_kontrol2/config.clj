(ns nano-kontrol2.config
  (:use
   [overtone.live])
  (:require
   [nano-kontrol2.stateful-device :as nksd]
   [nano-kontrol2.state-maps      :as nksm]
   [nano-kontrol2.machine         :as nk-conn]
   [overtone.libs.event           :as e]
   [overtone.osc                  :as osc]
   [clojure.edn                   :as edn]))

(defonce mixer-init-state (merge (nksd/nk-state-map 0)
                                 {:slider7 0}
                                 {:pot2 1}
                                 {:pot3 1}
                                 {:pot5 1}
                                 {:pot6 1}
                                 {:pot7 0.5}))

(defonce basic-mixer-init-state (merge (nksd/nk-state-map 0)
                                       {:slider7 1
                                        :slider6 0}))

(defn setup! [cfg banks]

  (defn nk-bank
    "Returns the nk bank number for the specified bank key"
    [bank-k]
    (banks bank-k))

  (defonce __ADD-STATE-MAPS__
    ;; Adds a new set of state-maps to the initial nk state-maps. This
    ;; allows us to specify which nk button to bind the location and also
    ;; which event key to use.

    (do
      (doseq [[bank-name settings] cfg]
        (doseq [[key mixer-state] settings]
          (if (sequential? mixer-state)
            (let [[name state] mixer-state]
              (nksm/add-state nk-conn/state-maps (nk-bank bank-name) name key state))
            (nksm/add-state nk-conn/state-maps (nk-bank bank-name) key mixer-state))))

      ;; give each nk an initial state
      (doseq [nk nk-conn/nano-kons]
        (nksm/switch-state nk-conn/state-maps nk 0 :s7))))

  (defonce nano-kontrol-dev (osc-server 4499))

  (osc-handle nano-kontrol-dev
              "/nk-event/simple"
              (fn [m]
                (let [payload (edn/read-string (first (:args m)))]
                  (event [:v-nanoKON2
                          (:bank payload)
                          (:key payload)
                          :control-change
                          (:id payload)]
                         payload)))))
