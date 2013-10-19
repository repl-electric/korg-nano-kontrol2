(ns nano-kontrol2.core
  (:require
   [nano-kontrol2.machine]
   [nano-kontrol2.config]))

(defn start! []
  (nano-kontrol2.machine/register!)
  (nano-kontrol2.config/setup!))