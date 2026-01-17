(ns bounce.core
  (:require [quil.core :as q]
            [quil.middleware :as m])
  (:gen-class))

;; Constants
(def width 1920)
(def height 1080)
(def background 0)
(def dt 0.05)
(def gravity 9.81)
(def ball-number 1)
(def ball-color [255 0 0])

;; --- Math Helpers ---
(defn dot-product [vec1 vec2]
  (+ (* (vec1 0) (vec2 0)) (* (vec1 1) (vec2 1))))

(defn scalar-product [scalar vec]
  [(* scalar (vec 0)) (* scalar (vec 1))])

(defn vec-minus [vec1 vec2]
  [(- (vec1 0) (vec2 0)) (- (vec1 1) (vec2 1))])

(defn distance-sq [vec1 vec2]
  (let [diff (vec-minus vec1 vec2)]
    (dot-product diff diff)))

;; --- Ball Logic ---
;; Updated to ignore arguments so we can call it easily
(defn gen-ball [_]
  {:x (+ 100 (rand 300))
   :y (+ 100 (rand 300))
   :vx (- (rand 100) 50)
   :vy (- (rand 100) 50)
   :ax 0.0
   :ay gravity
   :radius (+ 10 (rand 20))
   :mass (+ 1 (rand 5))})

(defn collision [ball1 ball2]
  (let [pos1 [(:x ball1) (:y ball1)]
        vel1 [(:vx ball1) (:vy ball1)]
        mass1 (:mass ball1)
        radius1 (:radius ball1)
        pos2 [(:x ball2) (:y ball2)]
        vel2 [(:vx ball2) (:vy ball2)]
        mass2 (:mass ball2)
        radius2 (:radius ball2)
        rad-sum2 (* (+ radius1 radius2) (+ radius1 radius2))
        dis2 (distance-sq pos1 pos2)]
    
    (if (and (<= dis2 rad-sum2) (> dis2 0))
      (let [mass-sum (+ mass1 mass2)
            pos-vec-diff (vec-minus pos1 pos2)
            vel-vec-diff (vec-minus vel1 vel2)
            dot-v-x (dot-product vel-vec-diff pos-vec-diff)]
        
        ;; "Sticking" fix: Only bounce if moving towards each other
        (if (< dot-v-x 0) 
          (let [mass-factor1 (/ (* 2 mass2) mass-sum)
                scalar-val (/ dot-v-x dis2)
                delta-v (scalar-product (* mass-factor1 scalar-val) pos-vec-diff)
                new-vel1 (vec-minus vel1 delta-v)]
            (assoc ball1
                   :vx (new-vel1 0)
                   :vy (new-vel1 1)))
          ball1))
      ball1)))

(defn update-ball [ball all-balls]
  (let [collided-ball (reduce collision ball all-balls)
        {:keys [x y vx vy ax ay radius]} collided-ball

        new-vx (if (or (<= x radius) (>= x (- width radius))) (* -0.9 vx) vx)
        new-vy (if (or (<= y radius) (>= y (- height radius))) (* -0.9 vy) vy)

        final-vx (+ new-vx (* ax dt))
        final-vy (+ new-vy (* ay dt))
        
        new-x (+ x (* new-vx dt) (* ax dt dt 1/2))
        new-y (+ y (* new-vy dt) (* ay dt dt 1/2))

        final-x (max radius (min new-x (- width radius)))
        final-y (max radius (min new-y (- height radius)))]

    (assoc collided-ball
           :x final-x :y final-y
           :vx final-vx :vy final-vy)))

;; --- Quil Handlers ---

(defn setup []
  (vec (map gen-ball (range ball-number))))

(defn update-state [balls]
  (mapv #(update-ball % balls) balls))

(defn draw-state [balls]
  (q/background background)
  (apply q/fill ball-color)
  (doseq [ball balls]
    (q/ellipse (:x ball) (:y ball) (* 2 (:radius ball)) (* 2 (:radius ball)))))

;; NEW: Handle key presses
(defn key-pressed [balls event]
  (cond
    (= (:key event) :up)   (conj balls (gen-ball 0))     ;; Add ball
    (and (= (:key event) :down) (pos? (count balls)))    ;; Remove ball (safe check)
                           (pop balls)
    :else                  balls))

(q/defsketch bounce
    :title "Bounce"
    :size [width height]
    :setup #'setup
    :update #'update-state
    :draw #'draw-state
    :key-pressed #'key-pressed ;; Register the handler
    :middleware [m/fun-mode])

(defn -main [& args]
  (println "Use UP arrow to add balls, DOWN arrow to remove them."))
