(ns noise.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; setup function returns initial state.
  {:t 0
   :x 0
   :y 0})

(defn get-y-using-noise [t]
  (* (q/noise t) (q/height)))

(defn get-y-using-random [min max]
  (q/random min max))

(defn update-state [state]
  (let [t (+ (:t state) 0.1001)
        x (+ (:x state) 1)
        y (get-y-using-noise t)]
    ; (println (str "t: " t " x: " x " y: " y))
    {:t t
     :x x
     :y y}))

(defn draw-state [state]
  ; Set circle color.
  (q/fill 120 255 120)
  ; No stroke on circles
  (q/no-stroke)
  ; Draw the circle.
  (q/ellipse (:x state) (:y state) 2 2))

(q/defsketch noise
  :title "Noise"
  :size [640 480]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
