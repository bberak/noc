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
  ; Setup function returns initial state.
  ; The :tx and :ty values do not really represent time. More like
  ; space (location) on a fixed noise signal
  {:tx 0 ; Often referred to as the x-offset (xoff)
   :ty 10000 ; Often referred to as the y-offset (yoff). In this case, we want to
             ; sample the noise signal for our y-value from a different location than
             ; the x location. Otherwise our walker would move in a diagnol line
   :x (/ (q/width) 2)
   :y (/ (q/height) 2)})

(defn get-random-using-noise [t min max]
  (q/map-range (q/noise t) 0 1 min max))

(defn get-random [min max]
  (q/random min max))

(defn update-state [state]
  (let [tx (+ (:tx state) 0.01)
        ty (+ (:ty state) 0.01)
        ; Uncomment lines below to plot a y vs time graph
        ; x (+ (:x state) 1)
        ; y (get-random-using-noise ty 0 (q/height))]
        ; Uncomment lines below to render a random walker
        ; whose x and y values are determined by Perlin noise
        x (get-random-using-noise tx 0 (q/width))
        y (get-random-using-noise ty 0 (q/height))]
        ; Uncomment lines below to render a random walker
        ; whose x and y stepsizes vary using Perlin noise
        ; stepsize-x (get-random-using-noise tx -5 5)
        ; stepsize-y (get-random-using-noise ty -5 5)
        ; x (+ (:x state) stepsize-x)
        ; y (+ (:y state) stepsize-y)]
    {:tx tx
     :ty ty
     :x x
     :y y}))

(defn draw-state [state]
  ; Set circle color.
  (q/fill 120 255 120)
  ; No stroke on circles
  (q/no-stroke)
  ; Draw the circle.
  (q/ellipse (:x state) (:y state) 5 5))

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
