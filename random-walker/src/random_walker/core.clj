(ns random-walker.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; Set the bg-color to a light grey (only once)
  (q/background 240)
  ; setup function returns initial state. It contains
  ; x and y coordinates set to the middle of the window
  {:x (/ (q/width) 2)
   :y (/ (q/height) 2)})

(defn get-random [min max]
  (q/random min max))

(defn get-monte-carlo-random [min max]
  (let [r1 (get-random min max)
        p r1
        r2 (get-random min max)]
    (if (< r2 p)
      r1
      (get-monte-carlo-random min max))))

(defn update-state [state]
  ; Update sketch state by changing the x and y coordinates
  (let [stepsize (get-monte-carlo-random 0 5)
        neg-stepsize (* stepsize -1)]
    {:x (+ (get-random neg-stepsize stepsize) (:x state))
     :y (+ (get-random neg-stepsize stepsize) (:y state))}))

(defn draw-state [state]
  ; Draw a point in the x and y position
  (q/point (:x state) (:y state)))

(q/defsketch random-walker
  :title "Random Walkker"
  :size [500 500]
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
