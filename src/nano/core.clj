(ns nano.core
  (:require
   [nano.timed]
   [nano.state-maps]
   [nano.stateful-device]
   [nano.machine]
   [nano.config]))

(defn setup []
  (nano.machine/register!)
  (nano.config/setup!))