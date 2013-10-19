(ns nano-kontrol2.machine
  (:require
   [nano-kontrol2.stateful-device :as nksd]
   [nano-kontrol2.state-maps :as nksm]
   [overtone.studio.midi :as midi]
   [overtone.libs.event :as e]))

(defn start-registering []
  (defonce nk-connected-rcvs (midi/midi-find-connected-receivers "nanoKONTROL2"))
  (defonce nk-connected-devs (midi/midi-find-connected-devices "nanoKONTROL2"))
  (defonce nk-stateful-devs (map nksd/stateful-nk nk-connected-devs))
  (defonce nano-kons (nksd/merge-nano-kons nk-connected-rcvs nk-stateful-devs))
  (defonce state-maps (nksm/mk-state-map nano-kons))

  (e/on-event [:nanoKON2 :control-change :marker-right]
              (fn [m]
                (when (< 0 (:val m))
                  (nksm/refresh state-maps (:nk m))))
              ::refresh)

  (e/on-event [:nanoKON2 :control-change :record]
              (fn [m]
                (if (< 0 (:val m))
                  (nksm/nk-clutch-on state-maps (:nk m))
                  (nksm/nk-clutch-off state-maps (:nk m))))
              ::clutch)

  (e/on-event [:nanoKON2 :control-change :marker-left]
              (fn [m]
                (if (< 0 (:val m))
                  (nksm/nk-absolute-val-viz-on state-maps (:nk m))
                  (nksm/nk-absolute-val-viz-off state-maps (:nk m))))
              ::viz)

  (e/on-event [:nanoKON2 :control-change :marker-set]
              (fn [m]
                (when (< 0 (:val m))
                  (nksm/nk-force-sync-all state-maps (:nk m))))
              ::force-sync-all)

  (e/on-event [:nanoKON2 :control-change :cycle]
              (fn [m]
                (when (< 0 (:val m))
                  (nksm/nk-switcher-mode state-maps (:nk m))))
              ::switch-state)

  (e/on-latest-event [:nanoKON2 :control-change]
                     (fn [m]
                       (nksm/nk-update-states state-maps
                                              (:nk m)
                                              (:id m)
                                              (:val m)))
                     ::update-state)

      ;; Things To Do:
      ;;
      ;; * Save and load states
      ;; * Switch between groups
      ;; *

      ;; To help debug:
      ;;
      (println :error (agent-error state-maps)))

(defn register! []
  (if (seq (midi/midi-find-connected-devices "nanoKONTROL2"))
    (start-registering)
    (throw (Exception. "No nanoKONTROL2 connected"))))