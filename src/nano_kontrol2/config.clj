(ns nano-kontrol2.config
  (:use
   [overtone.live])
  (:require
   [nano-kontrol2.stateful-device :as nksd]
   [nano-kontrol2.state-maps :as nksm]
   [nano-kontrol2.machine :as nk-conn]
   [overtone.libs.event :as e]
   [overtone.osc :as osc]
   [clojure.edn :as edn]))

(defn setup! []
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

  (defn nk-bank
    "Returns the nk bank number for the specified bank key"
    [bank-k]
    (case bank-k
      :master 0
      :m64 2
      :m128 4
      :riffs 8
      :synths 16))

  (defonce __ADD-STATE-MAPS__
    ;; Adds a new set of state-maps to the initial nk state-maps. This
    ;; allows us to specify which nk button to bind the location and also
    ;; which event key to use.
    (do
      (nksm/add-state nk-conn/state-maps (nk-bank :synths) :s0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :synths) :s1 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :synths) :s2 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :synths) :m0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :synths) :m1 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :synths) :r0 mixer-init-state)

      (nksm/add-state nk-conn/state-maps (nk-bank :riffs) :s0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :riffs) :s1 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :riffs) :m0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :riffs) :m1 mixer-init-state)

      (nksm/add-state nk-conn/state-maps (nk-bank :m128) "m128-0" :s0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m128) "m128-1" :m0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m128) "m128-2" :r0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m128) "m128-3" :s1 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m128) "m128-4" :m1 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m128) "m128-5" :r1 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m128) "m128-triggers" :s3 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m128) "m128-master" :r7 basic-mixer-init-state)

      (nksm/add-state nk-conn/state-maps (nk-bank :m64) "m64-0" :s0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m64) "m64-1" :m0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m64) "m64-2" :r0 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m64) "m64-3" :s1 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m64) "m64-4" :m1 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m64) "m64-5" :r1 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m64) "m64-triggers" :s3 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :m64) "m64-master" :r7 basic-mixer-init-state)

      (nksm/add-state nk-conn/state-maps (nk-bank :master) :s7 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :master) :m7 mixer-init-state)
      (nksm/add-state nk-conn/state-maps (nk-bank :master) :r7 mixer-init-state)

      ;; give each nk an initial state
      (doseq [nk nk-conn/nano-kons]
        (nksm/switch-state nk-conn/state-maps nk 0 :s7))))

  (defonce nano-kontrol-dev (osc-server 4499))

  (osc-handle nano-kontrol-dev
              "/nk-event/simple"
              (fn [m]
                (println :event m)
                (let [payload (edn/read-string (first (:args m)))]
                  (event [:v-nanoKON2
                          (:bank payload)
                          (:key payload)
                          :control-change
                          (:id payload)]
                         payload)))))