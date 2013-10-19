(ns nano-kontrol2.core
  (:use [nano-kontrol2.config :only [mixer-init-state]])
  (:require
   [nano-kontrol2.machine]
   [nano-kontrol2.config]
   [nano-kontrol2.buttons :as btn]))

(def cfg
  {:synths {:s0 mixer-init-state :s1 mixer-init-state :s2 mixer-init-state :m0 mixer-init-state :m1 mixer-init-state :r0 mixer-init-state}
   :riffs  {:s0 mixer-init-state :s1 mixer-init-state :m0 mixer-init-state :m1 mixer-init-state}
   :master {:s7 mixer-init-state :m7 mixer-init-state :r7 mixer-init-state}})

(def banks
  {:master btn/record
   :m64    btn/play
   :m128   btn/stop
   :riffs  btn/fast-forward
   :synths btn/rewind})

(defn start!
  ([] (start! banks cfg))
  ([banks cfg]
     (nano-kontrol2.machine/register!)
     (nano-kontrol2.config/setup! cfg banks)))