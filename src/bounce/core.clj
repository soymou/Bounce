(ns bounce.core
  (:require [quil.core :as q]
            [quil.middleware :as m])
  (:gen-class))

;; Constantes
(def width 600)
(def height 400)
(def ball-radius 20)
(def background [0 0 0])
(def dt 0.5)


(defn setup []
  {:x 100
   :y 100
   :vx 1
   :vy 2
   :ax 0.0
   :ay -0.1
   :color [255 0 0]})

(defn update-state [state]
  (let [x (:x state)
        y (:y state)
        vx (:vx state)
        vy (:vy state)
        ax (:ax state)
        ay (:ay state)
        ]))
