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
(def energy-loss 0.1)


;; Estado inicial
(defn setup []
  {:x 100
   :y 100
   :vx 1
   :vy 2
   :ax 0.0
   :ay -0.1
   :color [255 0 0]})

;; Actualización del estado
(defn update-state [state]
  (let [x (:x state)
        y (:y state)
        vx (:vx state)
        vy (:vy state)
        ax (:ax state)
        ay (:ay state)

        next-vx (+ vx (* ax dt))
        next-vy (+ vy (* ay dt))

        next-x (+ x (* next-vx dt))
        next-y (+ y (* next-vy dt))

        final-vx (if (or (< next-x 0) (> next-x width))
                   (* next-vx (- energy-loss 1))
                   next-vx)

        final-vy (if (or (< next-y 0) (> next-y height))
                   (* next-vy (- energy-loss 1)))

        final-x (max 0 (min width next-x))
        final-y (max 0 (min height next-y))]

    (assoc state
           :x final-x
           :y final-y
           :vx final-vx
           :vy final-vy)))

;; Dibujar estado
(defn draw-state [state]
  (q/background background)
  (apply q/fill (:color state))
  (q/ellipse (:x state) (:y state) ball-radius ball-radius))

(q/defsketch bounce
  :title "Bounce"
  :size [width height]
  :setup setup
  :update update-state
  :draw draw-state
  :middleware [m/fun-mode])

(defn -main [& args]
  println "Bouncing")


