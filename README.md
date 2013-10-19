Korg NanoKontrol2
==================

Using NanoKontrol2 with Overtone.

To be clear all credit and hardwork is that of @samaaron and @meta-ex.
I extracted this out of their Ignite project: https://github.com/meta-ex/ignite

## Device Setup

You will need to setup your NanoKontrol2 such that software can control all the LEDS.

    1. Download KORG KONTROL Editor http://www.korg.co.uk/support/downloads/nano2_dl.php
    2. Set LED MODE to EXTERNAL

![](http://s14.postimg.org/5qot9xyq9/Korg_Kontrol_Editor_Untitled_2013_10_19_12_2.jpg)

    3. Write the scene data (this saves the change to the device)

## Control

In order to push the use of the NanoKontrol some buttons are mapped to do different things.

   * cycle -> Enter select bank mode.
     * rewind, fastforward, stop, play and record all select a bank.
     * within a bank mode s,m & r buttons highlighted respresent further different states.
      

What you assign the banks is completely up to you. One example:

```Clojure
(defn nk-bank
  "Returns the nk bank number for the specified bank key"
  [bank-k]
  (case bank-k
    :master 0    ; record button (bank 0)
    :m64 2       ; play button (bank 2)
    :m128 4      ; stop button (bank 4)
    :riffs 8     ; fast-forward button (bank 8)
    :synths 16)) ; rewind button (bank 16)
```
